"""Model-size ablation: UniXcoder with its top encoder layers dropped.

For every seed and layer count, fine-tunes UniXcoder keeping only the bottom
k encoder layers (0 = embeddings only) on the unbalanced train split of
datasets/dataset_seed<seed>.csv and scores it on the test split. Each run
appends one row to results/model_size.csv (finished runs are skipped on a
rerun); the figure plots accuracy, F1 and fine-tuning time against the
parameter count, from the full model on the left to the smallest on the right.

The classifier mean-pools UniXcoder's token embeddings, as UniXcoder's own
fine-tuning scripts do, and feeds them to a linear head. Inputs follow the
encoder-only format <s> <encoder-only> </s> code </s> with whitespace
collapsed. The model of the last epoch is scored; there is no early stopping,
so the test split is never used for training decisions.

Usage:
    pip install torch transformers matplotlib codecarbon
    python scripts/3_model_size.py                       # seeds 42 43, layers 12 10 8 6 4 2 1 0
    python scripts/3_model_size.py --no-codecarbon       # skip the energy measurement
    python scripts/3_model_size.py --layers 12 6 --seeds 42
    python scripts/3_model_size.py --plot-only
"""
import argparse
import csv
import importlib
import math
import os
import random
import statistics
import time

# The module name starts with a digit, so a plain import statement cannot load it
analysis = importlib.import_module("0_dataset_analysis")

RESULT_COLUMNS = ["seed", "layers", "params_total", "params_trainable", "representation",
                  "accuracy", "precision", "recall", "f1", "mcc", "tp", "fp", "fn", "tn",
                  "train_seconds", "test_seconds", "peak_gpu_mb", "energy_kwh", "emissions_kg", "country",
                  "epochs", "lr", "batch_size", "max_len"]

ACC_COLOR = "#2a78d6"
F1_COLOR = "#eb6834"
INK = "#0b0b0b"
MUTED = "#52514e"
GRID = "#e4e3df"


def log(message):
    print(f"[{time.strftime('%H:%M:%S')}] {message}", flush=True)


def set_seed(seed):
    import numpy as np
    import torch
    random.seed(seed)
    np.random.seed(seed)
    torch.manual_seed(seed)
    torch.cuda.manual_seed_all(seed)


def load_split(dataset_csv, data_dir, augmented=False):
    """Return (train, test) lists of (code, label); originals only unless augmented."""
    train, test = [], []
    with open(dataset_csv, newline="", encoding="utf-8") as f:
        for r in csv.DictReader(f):
            if r["is_augmented"] != "0" and not (augmented and r["split"] == "train"):
                continue
            with open(os.path.join(data_dir, r["is_augmented"], r["filename"]), encoding="utf-8") as src:
                code = src.read()
            (train if r["split"] == "train" else test).append((code, int(r["label"])))
    return train, test


def load_model_and_tokenizer(model_name):
    from transformers import AutoModel, AutoTokenizer
    return AutoModel.from_pretrained(model_name), AutoTokenizer.from_pretrained(model_name)


def encode(tokenizer, code, max_len):
    tokens = tokenizer.tokenize(analysis.normalize_whitespace(code))[:max_len - analysis.SPECIAL_TOKENS]
    tokens = [tokenizer.cls_token, "<encoder-only>", tokenizer.sep_token] + tokens + [tokenizer.sep_token]
    return tokenizer.convert_tokens_to_ids(tokens)


def batches(examples, batch_size, pad_id, shuffle, rng):
    import torch
    order = list(range(len(examples)))
    if shuffle:
        rng.shuffle(order)
    for i in range(0, len(order), batch_size):
        chunk = [examples[j] for j in order[i:i + batch_size]]
        width = max(len(ids) for ids, _ in chunk)
        ids = torch.full((len(chunk), width), pad_id, dtype=torch.long)
        for row, (seq, _) in enumerate(chunk):
            ids[row, :len(seq)] = torch.tensor(seq)
        yield ids, torch.tensor([label for _, label in chunk])


def build_classifier(encoder, n_layers, pad_id, dropout=0.1):
    import torch
    from torch import nn

    # Keep the bottom n_layers transformer blocks; 0 leaves the embeddings only
    encoder.encoder.layer = encoder.encoder.layer[:n_layers]
    encoder.config.num_hidden_layers = n_layers
    encoder.pooler = None

    class Classifier(nn.Module):
        def __init__(self):
            super().__init__()
            self.encoder = encoder
            self.dropout = nn.Dropout(dropout)
            self.head = nn.Linear(encoder.config.hidden_size, 2)

        def embed(self, ids):
            mask = ids.ne(pad_id)
            hidden = self.encoder(ids, attention_mask=mask)[0]
            return (hidden * mask[:, :, None]).sum(1) / mask.sum(1, keepdim=True)

        def forward(self, ids):
            return self.head(self.dropout(self.embed(ids)))

    return Classifier()


def scores(labels, preds):
    tp = sum(l == 1 and p == 1 for l, p in zip(labels, preds))
    fp = sum(l == 0 and p == 1 for l, p in zip(labels, preds))
    fn = sum(l == 1 and p == 0 for l, p in zip(labels, preds))
    tn = sum(l == 0 and p == 0 for l, p in zip(labels, preds))
    precision = tp / (tp + fp) if tp + fp else 0.0
    recall = tp / (tp + fn) if tp + fn else 0.0
    f1 = 2 * precision * recall / (precision + recall) if precision + recall else 0.0
    denom = math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn))
    return {"accuracy": (tp + tn) / len(labels), "precision": precision, "recall": recall, "f1": f1,
            "mcc": (tp * tn - fp * fn) / denom if denom else 0.0, "tp": tp, "fp": fp, "fn": fn, "tn": tn}


def run(seed, n_layers, args, load=load_model_and_tokenizer):
    import torch
    from transformers import get_linear_schedule_with_warmup

    set_seed(seed)
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    log(f"seed {seed}, {n_layers} layers: loading {args.model} (downloaded on first use)")
    encoder, tokenizer = load(args.model)
    model = build_classifier(encoder, n_layers, tokenizer.pad_token_id).to(device)
    gpu = f" ({torch.cuda.get_device_name(0)})" if device.type == "cuda" else " (no GPU found, this will be slow)"
    log(f"seed {seed}, {n_layers} layers: training on {device.type}{gpu}")

    log("reading and tokenizing the dataset")
    data_dir = args.comment_dir if args.representation == "comment" else args.nocomment_dir
    train, test = load_split(os.path.join(args.dataset_dir, f"dataset_seed{seed}.csv"), data_dir)
    train = [(encode(tokenizer, code, args.max_len), label) for code, label in train]
    test = [(encode(tokenizer, code, args.max_len), label) for code, label in test]
    log(f"{len(train)} train and {len(test)} test methods")

    optimizer = torch.optim.AdamW(model.parameters(), lr=args.lr, weight_decay=args.weight_decay)
    steps = args.epochs * math.ceil(len(train) / args.batch_size)
    scheduler = get_linear_schedule_with_warmup(optimizer, int(args.warmup * steps), steps)
    loss_fn = torch.nn.CrossEntropyLoss()
    use_amp = device.type == "cuda"
    scaler = torch.amp.GradScaler("cuda", enabled=use_amp)
    rng = random.Random(seed)

    tracker = None
    if args.codecarbon:
        try:
            from codecarbon import OfflineEmissionsTracker
        except ImportError:
            raise SystemExit("CodeCarbon is not installed: pip install codecarbon, or run with --no-codecarbon")
        # Offline mode takes the grid carbon intensity of the given country instead of
        # guessing the location from the IP address, so every machine reports the same way
        log(f"starting CodeCarbon ({args.country})")
        tracker = OfflineEmissionsTracker(country_iso_code=args.country, log_level="error", save_to_file=False)
        tracker.start()
    if device.type == "cuda":
        torch.cuda.reset_peak_memory_stats()
    start = time.time()

    model.train()
    for epoch in range(1, args.epochs + 1):
        losses = []
        for ids, labels in batches(train, args.batch_size, tokenizer.pad_token_id, True, rng):
            ids, labels = ids.to(device), labels.to(device)
            with torch.autocast(device.type, enabled=use_amp):
                loss = loss_fn(model(ids), labels)
            optimizer.zero_grad()
            scaler.scale(loss).backward()
            scaler.unscale_(optimizer)
            torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
            scaler.step(optimizer)
            scaler.update()
            scheduler.step()
            losses.append(loss.item())
        log(f"  epoch {epoch:>2}/{args.epochs}: loss {statistics.mean(losses):.4f}, "
            f"{time.time() - start:.0f}s elapsed")

    train_seconds = time.time() - start
    energy = None
    if tracker:
        # CodeCarbon swallows its own errors in stop() and then never sets final_emissions_data
        tracker.stop()
        energy = getattr(tracker, "final_emissions_data", None)
        if energy is None:
            log("CodeCarbon could not report this run; energy and emissions are left empty")

    log("scoring the test split")
    model.eval()
    start = time.time()
    preds, labels = [], []
    with torch.no_grad():
        for ids, batch_labels in batches(test, args.batch_size, tokenizer.pad_token_id, False, rng):
            with torch.autocast(device.type, enabled=use_amp):
                logits = model(ids.to(device))
            preds += logits.argmax(-1).tolist()
            labels += batch_labels.tolist()
    test_seconds = time.time() - start

    return {
        "seed": seed, "layers": n_layers,
        "params_total": sum(p.numel() for p in model.parameters()),
        "params_trainable": sum(p.numel() for p in model.parameters() if p.requires_grad),
        "representation": args.representation,
        **{k: round(v, 4) if isinstance(v, float) else v for k, v in scores(labels, preds).items()},
        "train_seconds": round(train_seconds, 1),
        "test_seconds": round(test_seconds, 2),
        "peak_gpu_mb": round(torch.cuda.max_memory_allocated() / 2**20) if device.type == "cuda" else "",
        "energy_kwh": round(energy.energy_consumed, 6) if energy else "",
        "emissions_kg": round(energy.emissions, 6) if energy else "",
        "country": args.country if energy else "",
        "epochs": args.epochs, "lr": args.lr, "batch_size": args.batch_size, "max_len": args.max_len,
    }


def plot(results, out):
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    # DejaVu Sans ships with matplotlib, so the figure is the same on every machine
    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})
    fig, (ax, ax_time) = plt.subplots(2, 1, figsize=(7, 6.2), sharex=True,
                                      gridspec_kw={"height_ratios": [1.6, 1]})

    by_layers = {}
    for r in results:
        by_layers.setdefault(int(r["layers"]), []).append(r)
    layers = sorted(by_layers)
    params = [int(by_layers[k][0]["params_total"]) / 1e6 for k in layers]
    seeds = sorted({r["seed"] for r in results})

    def draw(axis, metric, color, scale=1.0):
        means = [statistics.mean(float(r[metric]) * scale for r in by_layers[k]) for k in layers]
        for k, x in zip(layers, params):
            ys = [float(r[metric]) * scale for r in by_layers[k]]
            axis.scatter([x] * len(ys), ys, s=14, color=color, alpha=0.35, edgecolors="none", zorder=2)
        axis.plot(params, means, color=color, linewidth=2, marker="o", markersize=6, zorder=3)
        return means

    # (a) scores; the x axis runs from the full model on the left to the smallest on the right
    for metric, color, name in [("accuracy", ACC_COLOR, "Accuracy"), ("f1", F1_COLOR, "F1-score")]:
        means = draw(ax, metric, color)
        ax.plot([], [], color=color, linewidth=2, marker="o", markersize=6, label=name)
        ax.text(params[0] - 1.5, means[0], name, color=INK, fontsize=9, va="center")
    ax.set_ylabel("Score on the test split", color=MUTED)
    ax.set_title(f"(a) Accuracy and F1-score (mean of seeds {', '.join(map(str, seeds))}; dots = single seeds)",
                 loc="left", fontsize=10, color=INK, pad=10)
    ax.legend(frameon=False, loc="lower left", fontsize=9)

    # (b) fine-tuning time
    draw(ax_time, "train_seconds", INK, scale=1 / 60)
    ax_time.set_ylabel("Minutes", color=MUTED)
    ax_time.set_ylim(bottom=0)
    ax_time.set_title("(b) Fine-tuning time", loc="left", fontsize=10, color=INK)
    ax_time.set_xlabel("Parameters (millions)", color=MUTED)

    ax.set_xlim(max(params) + 4, min(params) - 14)
    top = ax.secondary_xaxis("top")
    top.set_xticks(params, [f"{k}L" for k in layers])
    top.tick_params(colors=MUTED, labelsize=8, length=0)
    top.spines["top"].set_visible(False)
    top.set_xlabel("Encoder layers kept", color=MUTED, fontsize=9)
    for axis in (ax, ax_time):
        axis.spines[["top", "right"]].set_visible(False)
        axis.spines[["left", "bottom"]].set_color(GRID)
        axis.tick_params(colors=MUTED, labelsize=9)
        axis.grid(axis="y", color=GRID, linewidth=0.8)
        axis.set_axisbelow(True)
    fig.tight_layout()
    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    fig.savefig(out, dpi=300)


def read_results(path):
    if not os.path.exists(path):
        return []
    with open(path, newline="", encoding="utf-8") as f:
        return list(csv.DictReader(f))


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--seeds", type=int, nargs="*", default=[42, 43])
    parser.add_argument("--layers", type=int, nargs="*", default=[12, 10, 8, 6, 4, 2, 1, 0])
    parser.add_argument("--representation", choices=["comment", "nocomment"], default="comment")
    parser.add_argument("--dataset-dir", default="datasets")
    parser.add_argument("--comment-dir", default="ds_comment")
    parser.add_argument("--nocomment-dir", default="ds_nocomment")
    parser.add_argument("--model", default="microsoft/unixcoder-base")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--epochs", type=int, default=20)
    parser.add_argument("--lr", type=float, default=1e-5)
    parser.add_argument("--batch-size", type=int, default=8)
    parser.add_argument("--weight-decay", type=float, default=0.01)
    parser.add_argument("--warmup", type=float, default=0.1, help="share of steps with linear warmup")
    parser.add_argument("--codecarbon", action=argparse.BooleanOptionalAction, default=True,
                        help="measure training energy and emissions with CodeCarbon (on by default)")
    parser.add_argument("--country", default="IDN",
                        help="ISO 3166-1 alpha-3 code whose grid carbon intensity converts energy to emissions")
    parser.add_argument("--out", default="results/model_size.csv")
    parser.add_argument("--figure", default="figures/model_size.png")
    parser.add_argument("--plot-only", action="store_true")
    args = parser.parse_args()

    if not args.plot_only:
        # torch and transformers take a while to import, so say so before the screen goes quiet
        log("importing torch and transformers")
        import torch  # noqa: F401
        import transformers  # noqa: F401
        log("imports done")
        done = {(r["seed"], r["layers"], r["representation"]) for r in read_results(args.out)}
        os.makedirs(os.path.dirname(args.out) or ".", exist_ok=True)
        for seed in args.seeds:
            for n_layers in args.layers:
                if (str(seed), str(n_layers), args.representation) in done:
                    log(f"[skip] seed {seed}, {n_layers} layers: already in {args.out}")
                    continue
                result = run(seed, n_layers, args)
                new_file = not os.path.exists(args.out)
                with open(args.out, "a", newline="", encoding="utf-8") as f:
                    writer = csv.DictWriter(f, fieldnames=RESULT_COLUMNS)
                    if new_file:
                        writer.writeheader()
                    writer.writerow(result)
                log(f"seed {seed}, {n_layers:>2} layers, {result['params_total'] / 1e6:6.1f}M params: "
                    f"acc {result['accuracy']:.3f}, F1 {result['f1']:.3f}, {result['train_seconds']}s")

    results = [r for r in read_results(args.out) if r["representation"] == args.representation]
    if results:
        plot(results, args.figure)
        print(f"-> {args.out}, {args.figure}")


if __name__ == "__main__":
    main()
