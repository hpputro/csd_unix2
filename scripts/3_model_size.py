"""Model-size ablation: UniXcoder with its top encoder layers dropped.

For every seed and layer count, fine-tunes UniXcoder keeping only the bottom
k encoder layers (0 = embeddings only) on the unbalanced train split of
datasets/dataset_seed<seed>.csv and scores it on the test split. Each run
appends one row to results/model_size.csv (finished runs are skipped on a
rerun) and the mean train loss of every epoch to results/model_size_loss.csv,
to see whether the larger models memorise the train split. Two figures plot,
against the parameter count, accuracy and F1-score
(figures/figure5_model_size_scores.png, Figure 5) and fine-tuning time and,
when measured, energy (figures/figure6_model_size_cost.png, Figure 6); a third
plots the train loss per epoch of a few sizes
(figures/figure7_model_size_loss.png, Figure 7).

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
    python scripts/3_model_size.py --plot-only --loss-layers 12 4 2 1
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
LOSS_COLUMNS = ["seed", "layers", "representation", "epoch", "train_loss"]
# A finished run is reused only when it was trained with the same settings
CONFIG_COLUMNS = ["representation", "head", "epochs", "lr", "batch_size", "grad_accum", "scheduler",
                  "warmup_steps", "weight_decay", "max_len"]

ACC_COLOR = "#2a78d6"
F1_COLOR = "#eb6834"
ENERGY_COLOR = "#1baf7a"
# Amber rather than pure yellow, which is hard to see on white
TIME_COLOR = "#e0a400"
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
    """Precision, recall and F1 of the smelly class (label 1), accuracy, and MCC over both classes.
    A zero denominator gives 0, as scikit-learn's zero_division=0 and its MCC do: a model that
    never predicts smelly gets precision, F1 and MCC 0 (recall is 0 by definition)."""
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

    epoch_losses = []
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
        epoch_losses.append(statistics.mean(losses))
        log(f"  epoch {epoch:>2}/{args.epochs}: loss {epoch_losses[-1]:.4f}, "
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

    result = {
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
    loss_rows = [{"seed": seed, "layers": n_layers, "representation": args.representation, "epoch": e,
                  "train_loss": round(l, 6)} for e, l in enumerate(epoch_losses, start=1)]
    return result, loss_rows


def plot(results, out_scores, out_cost):
    """Two figures against the parameter count, from the full model on the left to the smallest
    on the right, with the encoder layers kept on the top axis:
      out_scores  accuracy and F1-score of the smelly class on the test split; a line is the
                  mean over the seeds in results (42, 43 and 44 in the paper) and each faint
                  dot one seed
      out_cost    fine-tuning time (left axis, minutes) and, when CodeCarbon measured every
                  run, fine-tuning energy (right axis, Wh); means over the same seeds
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    # DejaVu Sans ships with matplotlib, so the figure is the same on every machine
    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})

    by_layers = {}
    for r in results:
        by_layers.setdefault(int(r["layers"]), []).append(r)
    layers = sorted(by_layers)
    params = [int(by_layers[k][0]["params_total"]) / 1e6 for k in layers]

    def draw(axis, metric, color, scale=1.0, linestyle="-", marker="o", dots=True):
        means = [statistics.mean(float(r[metric]) * scale for r in by_layers[k]) for k in layers]
        if dots:
            for k, x in zip(layers, params):
                ys = [float(r[metric]) * scale for r in by_layers[k]]
                axis.scatter([x] * len(ys), ys, s=14, color=color, alpha=0.35, edgecolors="none", zorder=2)
        axis.plot(params, means, color=color, linewidth=2, linestyle=linestyle, marker=marker,
                  markersize=7 if marker == "x" else 6, markeredgewidth=2, zorder=3)
        return means

    def frame(fig, axis, title, twin=None):
        axis.set_xlim(max(params) + 4, min(params) - 14)
        top = axis.secondary_xaxis("top")
        top.set_xticks(params, [f"{k}L" for k in layers])
        top.tick_params(colors=MUTED, labelsize=8, length=0)
        top.spines["top"].set_visible(False)
        top.set_xlabel("Encoder layers kept", color=MUTED, fontsize=9)
        axis.set_xlabel("Parameters (millions)", color=MUTED)
        axis.set_title(title, loc="left", fontsize=10, color=INK, pad=10)
        for a in [axis] + ([twin] if twin else []):
            a.spines["top"].set_visible(False)
            a.spines[["left", "bottom", "right"]].set_color(GRID)
            a.tick_params(colors=MUTED, labelsize=9)
        if twin is None:
            axis.spines["right"].set_visible(False)
        axis.grid(axis="y", color=GRID, linewidth=0.8)
        axis.set_axisbelow(True)
        fig.tight_layout()

    def save(fig, out):
        os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
        fig.savefig(out, dpi=300)
        plt.close(fig)

    # Accuracy and F1-score
    fig, ax = plt.subplots(figsize=(7, 4.4))
    ends = []
    for metric, color, name in [("accuracy", ACC_COLOR, "Accuracy"), ("f1", F1_COLOR, "F1-score")]:
        means = draw(ax, metric, color)
        ax.plot([], [], color=color, linewidth=2, marker="o", markersize=6, label=name)
        ends.append([means[0], name])
    # Keep the end labels apart when the lines finish at nearly the same value
    ends.sort()
    gap = 0.06
    for lower, upper in zip(ends, ends[1:]):
        if upper[0] - lower[0] < gap:
            middle = (lower[0] + upper[0]) / 2
            lower[0], upper[0] = middle - gap / 2, middle + gap / 2
    for y, name in ends:
        ax.text(params[0] - 1.5, y, name, color=INK, fontsize=9, va="center")
    ax.set_ylabel("Score on the test split", color=MUTED)
    ax.legend(frameon=False, loc="lower left", fontsize=9)
    frame(fig, ax, "Accuracy and F1-Score")
    save(fig, out_scores)

    # Fine-tuning time and energy; energy only when CodeCarbon measured every run
    has_energy = all(r.get("energy_kwh") not in (None, "") for r in results)
    fig, ax = plt.subplots(figsize=(7, 4.0))
    draw(ax, "train_seconds", TIME_COLOR, scale=1 / 60, dots=False)
    ax.plot([], [], color=TIME_COLOR, linewidth=2, marker="o", markersize=6, label="Time")
    ax.set_ylabel("Time (minutes)", color=MUTED)
    ax.set_ylim(bottom=0)
    twin = None
    if has_energy:
        twin = ax.twinx()
        draw(twin, "energy_kwh", ENERGY_COLOR, scale=1000, linestyle="--", marker="x", dots=False)
        ax.plot([], [], color=ENERGY_COLOR, linewidth=2, linestyle="--", marker="x", markersize=7,
                markeredgewidth=2, label="Energy")
        # Energy is measured by CodeCarbon (column country gives the grid used for the emissions)
        twin.set_ylabel("Energy (Wh)", color=MUTED)
        twin.set_ylim(bottom=0)
    ax.legend(frameon=False, loc="upper right", fontsize=9)
    frame(fig, ax, "Fine-tuning time and energy" if has_energy else "Fine-tuning time", twin)
    save(fig, out_cost)


def plot_loss(loss_rows, layers, out):
    """Mean train loss per epoch over the seeds in loss_rows (42, 43 and 44 in the paper), one line
    per layer count in layers, each seed as a faint line. The dotted line is the entropy of the
    class proportion of the train split (77 smelly of 280, 0.589 nats): the loss of a model that
    learns only that proportion and nothing about the code."""
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})
    colors = [F1_COLOR, ACC_COLOR, MUTED, ENERGY_COLOR, TIME_COLOR]
    fig, ax = plt.subplots(figsize=(7, 4.0))
    for n_layers, color in zip(layers, colors):
        runs = {}
        for r in loss_rows:
            if int(r["layers"]) == n_layers:
                runs.setdefault(r["seed"], []).append((int(r["epoch"]), float(r["train_loss"])))
        if not runs:
            continue
        for points in runs.values():
            points.sort()
            ax.plot([e for e, _ in points], [l for _, l in points], color=color, alpha=0.25, linewidth=1)
        epochs = sorted({e for points in runs.values() for e, _ in points})
        means = [statistics.mean(l for points in runs.values() for e, l in points if e == epoch) for epoch in epochs]
        ax.plot(epochs, means, color=color, linewidth=2, marker="o", markersize=4,
                label=f"{n_layers} layer{'s' if n_layers != 1 else ''}")
        ax.text(epochs[-1] + 0.3, means[-1], f"{means[-1]:.3f}", color=color, fontsize=8.5, va="center")
    prior = 77 / 280
    ax.axhline(-(prior * math.log(prior) + (1 - prior) * math.log(1 - prior)), color=MUTED, linestyle=":",
               linewidth=1.2, label="Class proportion only")
    ax.set_xlabel("Epoch", color=MUTED)
    ax.set_ylabel("Train loss", color=MUTED)
    ax.set_ylim(bottom=0)
    ax.set_xticks(range(0, 21, 5))
    ax.set_title("Training loss per epoch", loc="left", fontsize=10, color=INK, pad=10)
    ax.legend(frameon=False, loc="upper right", fontsize=9)
    ax.spines[["top", "right"]].set_visible(False)
    ax.spines[["left", "bottom"]].set_color(GRID)
    ax.tick_params(colors=MUTED, labelsize=9)
    ax.grid(axis="y", color=GRID, linewidth=0.8)
    ax.set_axisbelow(True)
    fig.tight_layout()
    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    fig.savefig(out, dpi=300)
    plt.close(fig)


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
    parser.add_argument("--loss-out", default="results/model_size_loss.csv")
    parser.add_argument("--figure-scores", default="figures/figure5_model_size_scores.png")
    parser.add_argument("--figure-cost", default="figures/figure6_model_size_cost.png")
    parser.add_argument("--figure-loss", default="figures/figure7_model_size_loss.png")
    parser.add_argument("--loss-layers", type=int, nargs="*", default=[12, 4, 1],
                        help="layer counts drawn in the loss figure")
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
                result, loss_rows = run(seed, n_layers, args)
                # Hand the finished model's GPU memory back before the next run
                gc.collect()
                torch.cuda.empty_cache()
                new_file = not os.path.exists(args.out)
                with open(args.out, "a", newline="", encoding="utf-8") as f:
                    writer = csv.DictWriter(f, fieldnames=RESULT_COLUMNS)
                    if new_file:
                        writer.writeheader()
                    writer.writerow(result)
                new_file = not os.path.exists(args.loss_out)
                with open(args.loss_out, "a", newline="", encoding="utf-8") as f:
                    writer = csv.DictWriter(f, fieldnames=LOSS_COLUMNS)
                    if new_file:
                        writer.writeheader()
                    writer.writerows(loss_rows)
                log(f"seed {seed}, {n_layers:>2} layers, {result['params_total'] / 1e6:6.1f}M params: "
                    f"acc {result['accuracy']:.3f}, F1 {result['f1']:.3f}, {result['train_seconds']}s")

    results = [r for r in read_results(args.out) if same_config(r, args)]
    if results:
        plot(results, args.figure_scores, args.figure_cost)
        print(f"-> {args.out}, {args.figure_scores}, {args.figure_cost}")
    loss_rows = [r for r in read_results(args.loss_out) if r["representation"] == args.representation]
    if loss_rows:
        plot_loss(loss_rows, args.loss_layers, args.figure_loss)
        print(f"-> {args.figure_loss}")


if __name__ == "__main__":
    main()
