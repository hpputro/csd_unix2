"""Model-size ablation: UniXcoder with its top encoder layers dropped.

For every seed and layer count, fine-tunes UniXcoder keeping only the bottom
k encoder layers (0 = embeddings only) on the unbalanced train split of
datasets/dataset_seed<seed>.csv and scores it on the test split. Each run
appends one row to results/model_size.csv (finished runs are skipped on a
rerun); the figure plots accuracy, F1, precision, fine-tuning time and, when
measured, energy against the parameter count, from the full model on the left
to the smallest on the right.

Model and training follow the configuration tuned in Putro et al. (2026,
IEEE Access): AutoModelForSequenceClassification with its [CLS] classification
head, learning rate 1e-5, batch 16, 50 warmup steps, cosine schedule, no weight
decay, no label smoothing, 20 epochs. Batch 16 is reached by gradient
accumulation (4 x 4 by default) so the full model fits on a shared GPU. Inputs
follow the encoder-only format <s> <encoder-only> </s> code </s> with
whitespace collapsed. The model of the last epoch is scored; unlike the earlier
paper no epoch is picked on held-out data, so the test split is never used for
training decisions.

Usage:
    pip install torch transformers matplotlib codecarbon
    python scripts/3_model_size.py --country JPN         # seeds 42 43 44, layers 12 10 8 6 5 4 3 2 1 0
    python scripts/3_model_size.py --no-codecarbon       # skip the energy measurement
    python scripts/3_model_size.py --allow-cpu           # train on the CPU when no GPU is found
    python scripts/3_model_size.py --batch-size 2 --grad-accum 8   # still batch 16, less memory
    python scripts/3_model_size.py --layers 12 6 --seeds 42
    python scripts/3_model_size.py --plot-only
"""
import argparse
import csv
import gc
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
                  "head", "epochs", "lr", "batch_size", "grad_accum", "scheduler", "warmup_steps", "weight_decay", "max_len"]
# A finished run is reused only when it was trained with the same settings
CONFIG_COLUMNS = ["representation", "head", "epochs", "lr", "batch_size", "grad_accum", "scheduler",
                  "warmup_steps", "weight_decay", "max_len"]

ACC_COLOR = "#2a78d6"
F1_COLOR = "#eb6834"
PRECISION_COLOR = "#1baf7a"
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
    from transformers import AutoModelForSequenceClassification, AutoTokenizer
    model = AutoModelForSequenceClassification.from_pretrained(model_name, num_labels=2)
    return model, AutoTokenizer.from_pretrained(model_name)


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


def build_classifier(model, n_layers, pad_id):
    """Keep the bottom n_layers encoder blocks (0 = embeddings only) under the [CLS] head."""
    from torch import nn

    encoder = model.base_model
    encoder.encoder.layer = encoder.encoder.layer[:n_layers]
    encoder.config.num_hidden_layers = n_layers

    class Classifier(nn.Module):
        def __init__(self):
            super().__init__()
            self.model = model

        def forward(self, ids):
            return self.model(input_ids=ids, attention_mask=ids.ne(pad_id)).logits

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
    from transformers import get_cosine_schedule_with_warmup, get_linear_schedule_with_warmup

    set_seed(seed)
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    log(f"seed {seed}, {n_layers} layers: loading {args.model} (downloaded on first use)")
    base, tokenizer = load(args.model)
    model = build_classifier(base, n_layers, tokenizer.pad_token_id).to(device)
    gpu = f" ({torch.cuda.get_device_name(0)})" if device.type == "cuda" else " (no GPU found, this will be slow)"
    log(f"seed {seed}, {n_layers} layers: training on {device.type}{gpu}")

    log("reading and tokenizing the dataset")
    data_dir = args.comment_dir if args.representation == "comment" else args.nocomment_dir
    train, test = load_split(os.path.join(args.dataset_dir, f"dataset_seed{seed}.csv"), data_dir)
    train = [(encode(tokenizer, code, args.max_len), label) for code, label in train]
    test = [(encode(tokenizer, code, args.max_len), label) for code, label in test]
    log(f"{len(train)} train and {len(test)} test methods")

    optimizer = torch.optim.AdamW(model.parameters(), lr=args.lr, weight_decay=args.weight_decay)
    steps = args.epochs * math.ceil(math.ceil(len(train) / args.batch_size) / args.grad_accum)
    schedule = get_cosine_schedule_with_warmup if args.scheduler == "cosine" else get_linear_schedule_with_warmup
    scheduler = schedule(optimizer, args.warmup_steps, steps)
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
        # The shuffle does not depend on the batch size, so batch 4 with 4 accumulation steps
        # sees the same groups of 16 as batch 16 and makes the same updates with less memory
        micro_batches = list(batches(train, args.batch_size, tokenizer.pad_token_id, True, rng))
        optimizer.zero_grad()
        for i, (ids, labels) in enumerate(micro_batches, start=1):
            ids, labels = ids.to(device), labels.to(device)
            with torch.autocast(device.type, enabled=use_amp):
                loss = loss_fn(model(ids), labels)
            scaler.scale(loss / args.grad_accum).backward()
            losses.append(loss.item())
            if i % args.grad_accum == 0 or i == len(micro_batches):
                scaler.unscale_(optimizer)
                torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0)
                scaler.step(optimizer)
                scaler.update()
                scheduler.step()
                optimizer.zero_grad()
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
        **config_values(args),
    }


def plot(results, out):
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    # DejaVu Sans ships with matplotlib, so the figure is the same on every machine
    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})
    # Energy gets its own panel only when CodeCarbon measured every run
    has_energy = all(r.get("energy_kwh") not in (None, "") for r in results)
    if has_energy:
        fig, (ax, ax_time, ax_energy) = plt.subplots(3, 1, figsize=(7, 8.4), sharex=True,
                                                     gridspec_kw={"height_ratios": [1.7, 1, 1]})
    else:
        fig, (ax, ax_time) = plt.subplots(2, 1, figsize=(7, 6.6), sharex=True,
                                          gridspec_kw={"height_ratios": [1.7, 1]})
        ax_energy = None

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
    panels = [(ax, f"(a) Accuracy, F1-score and precision (mean of seeds {', '.join(map(str, seeds))}; "
                   f"dots = single seeds)",
               [("accuracy", ACC_COLOR, "Accuracy"), ("f1", F1_COLOR, "F1-score"),
                ("precision", PRECISION_COLOR, "Precision")])]
    for axis, title, series in panels:
        ends = []
        for metric, color, name in series:
            means = draw(axis, metric, color)
            axis.plot([], [], color=color, linewidth=2, marker="o", markersize=6, label=name)
            ends.append([means[0], name])
        # Keep the end labels apart when the lines finish at nearly the same value
        ends.sort()
        gap = 0.06
        for lower, upper in zip(ends, ends[1:]):
            if upper[0] - lower[0] < gap:
                middle = (lower[0] + upper[0]) / 2
                lower[0], upper[0] = middle - gap / 2, middle + gap / 2
        for y, name in ends:
            axis.text(params[0] - 1.5, y, name, color=INK, fontsize=9, va="center")
        axis.set_ylabel("Score on the test split", color=MUTED)
        axis.set_title(title, loc="left", fontsize=10, color=INK, pad=10)
        axis.legend(frameon=False, loc="lower left", fontsize=9)

    # (b) fine-tuning time
    draw(ax_time, "train_seconds", INK, scale=1 / 60)
    ax_time.set_ylabel("Minutes", color=MUTED)
    ax_time.set_ylim(bottom=0)
    ax_time.set_title("(b) Fine-tuning time", loc="left", fontsize=10, color=INK)

    # (c) fine-tuning energy
    if ax_energy is not None:
        draw(ax_energy, "energy_kwh", INK, scale=1000)
        ax_energy.set_ylabel("Wh", color=MUTED)
        ax_energy.set_ylim(bottom=0)
        country = ", ".join(sorted({r["country"] for r in results if r.get("country")}))
        ax_energy.set_title(f"(c) Fine-tuning energy (CodeCarbon{', ' + country if country else ''})",
                            loc="left", fontsize=10, color=INK)
    (ax_energy or ax_time).set_xlabel("Parameters (millions)", color=MUTED)

    ax.set_xlim(max(params) + 4, min(params) - 14)
    top = ax.secondary_xaxis("top")
    top.set_xticks(params, [f"{k}L" for k in layers])
    top.tick_params(colors=MUTED, labelsize=8, length=0)
    top.spines["top"].set_visible(False)
    top.set_xlabel("Encoder layers kept", color=MUTED, fontsize=9)
    for axis in [a for a in (ax, ax_time, ax_energy) if a is not None]:
        axis.spines[["top", "right"]].set_visible(False)
        axis.spines[["left", "bottom"]].set_color(GRID)
        axis.tick_params(colors=MUTED, labelsize=9)
        axis.grid(axis="y", color=GRID, linewidth=0.8)
        axis.set_axisbelow(True)
    fig.tight_layout()
    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    fig.savefig(out, dpi=300)


def config_values(args):
    return {"representation": args.representation, "head": "cls", "epochs": args.epochs, "lr": args.lr,
            "batch_size": args.batch_size, "grad_accum": args.grad_accum, "scheduler": args.scheduler,
            "warmup_steps": args.warmup_steps, "weight_decay": args.weight_decay, "max_len": args.max_len}


def same_config(row, args):
    return all(str(row.get(c, "")) == str(v) for c, v in config_values(args).items())


def read_results(path):
    if not os.path.exists(path):
        return []
    with open(path, newline="", encoding="utf-8") as f:
        return list(csv.DictReader(f))


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--seeds", type=int, nargs="*", default=[42, 43, 44])
    parser.add_argument("--layers", type=int, nargs="*", default=[12, 10, 8, 6, 5, 4, 3, 2, 1, 0])
    parser.add_argument("--representation", choices=["comment", "nocomment"], default="comment")
    parser.add_argument("--dataset-dir", default="datasets")
    parser.add_argument("--comment-dir", default="ds_comment")
    parser.add_argument("--nocomment-dir", default="ds_nocomment")
    parser.add_argument("--model", default="microsoft/unixcoder-base")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--epochs", type=int, default=20)
    parser.add_argument("--lr", type=float, default=1e-5)
    parser.add_argument("--batch-size", type=int, default=4)
    parser.add_argument("--grad-accum", type=int, default=4,
                        help="micro-batches per update; the default 4 x 4 updates like batch 16")
    parser.add_argument("--weight-decay", type=float, default=0.0)
    parser.add_argument("--scheduler", choices=["cosine", "linear"], default="cosine")
    parser.add_argument("--warmup-steps", type=int, default=50)
    parser.add_argument("--codecarbon", action=argparse.BooleanOptionalAction, default=True,
                        help="measure training energy and emissions with CodeCarbon (on by default)")
    parser.add_argument("--country", default="IDN",
                        help="ISO 3166-1 alpha-3 code whose grid carbon intensity converts energy to emissions")
    parser.add_argument("--out", default="results/model_size.csv")
    parser.add_argument("--figure", default="figures/model_size.png")
    parser.add_argument("--allow-cpu", action="store_true",
                        help="train on the CPU when no GPU is found instead of stopping")
    parser.add_argument("--plot-only", action="store_true")
    args = parser.parse_args()

    if not args.plot_only:
        # Lets the CUDA allocator grow segments instead of fragmenting; must be set before CUDA starts
        os.environ.setdefault("PYTORCH_CUDA_ALLOC_CONF", "expandable_segments:True")
        # torch and transformers take a while to import, so say so before the screen goes quiet
        log("importing torch and transformers")
        import torch
        import transformers  # noqa: F401
        log("imports done")
        if not torch.cuda.is_available() and not args.allow_cpu:
            raise SystemExit("No usable GPU found (see the CUDA warning above, or check nvidia-smi). "
                             "CPU runs take far longer and their time and energy are not comparable; "
                             "pass --allow-cpu to run on the CPU anyway.")
        previous = read_results(args.out)
        if previous and list(previous[0]) != RESULT_COLUMNS:
            # Results from an older version of this script: add the new columns, keep the rows
            with open(args.out, "w", newline="", encoding="utf-8") as f:
                writer = csv.DictWriter(f, fieldnames=RESULT_COLUMNS)
                writer.writeheader()
                for r in previous:
                    writer.writerow({c: r.get(c, "1" if c == "grad_accum" else "") for c in RESULT_COLUMNS})
        done = {(r["seed"], r["layers"]) for r in previous if same_config(r, args)}
        os.makedirs(os.path.dirname(args.out) or ".", exist_ok=True)
        for seed in args.seeds:
            for n_layers in args.layers:
                if (str(seed), str(n_layers)) in done:
                    log(f"[skip] seed {seed}, {n_layers} layers: already in {args.out}")
                    continue
                result = run(seed, n_layers, args)
                # Hand the finished model's GPU memory back before the next run
                gc.collect()
                torch.cuda.empty_cache()
                new_file = not os.path.exists(args.out)
                with open(args.out, "a", newline="", encoding="utf-8") as f:
                    writer = csv.DictWriter(f, fieldnames=RESULT_COLUMNS)
                    if new_file:
                        writer.writeheader()
                    writer.writerow(result)
                log(f"seed {seed}, {n_layers:>2} layers, {result['params_total'] / 1e6:6.1f}M params: "
                    f"acc {result['accuracy']:.3f}, F1 {result['f1']:.3f}, {result['train_seconds']}s")

    results = [r for r in read_results(args.out) if same_config(r, args)]
    if results:
        plot(results, args.figure)
        print(f"-> {args.out}, {args.figure}")


if __name__ == "__main__":
    main()
