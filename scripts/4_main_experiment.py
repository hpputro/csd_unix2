"""Main experiment: balancing method x train ratio on a layer-pruned UniXcoder.

For every seed, fine-tunes UniXcoder (bottom 4 encoder layers under the [CLS]
head, as chosen in 3_model_size.py) on datasets/dataset_seed<seed>.csv and
scores it on the test split, which always holds original code at the natural
class proportion. The scenarios:

  none     ratio 0.38   original train methods only (also run with 12 layers to
                        confirm the model size)
  weight   ratio 0.61, 1.0   original train methods, cross-entropy weighted per
                        class by (original + extra rows) / original rows, e.g.
                        smelly 2 and clean 1.25 at 0.61
  augment  ratio 0.61, 1.0   original train methods plus the augmented variants
                        whose ratio column is <= the ratio
  smote    ratio 0.61, 1.0   two stages (Kang et al., 2020): the encoder of the
                        none run of the same seed is frozen, the final-layer
                        [CLS] vectors of the train methods get one SMOTE point
                        (Chawla et al., 2002; k = 5 nearest neighbours of the
                        same class) per extra row of the dataset, and a fresh
                        classification head is trained on real + synthetic
                        vectors
  head     ratio 0.38   control for smote: the same fresh head trained on the
                        real vectors only

Every extra row stands for one extra sample of its source method, so the
three methods add the same number of samples per method at a given ratio. The
head stage uses AdamW lr 1e-3, 20 epochs, batch 16, cosine schedule without
warmup, fixed before any run. The smote and head rows record the head stage
only (column stage = head); their encoder cost is that of the none row of the
same seed, layers and representation.

The fine-tuning setup is that of 3_model_size.py: lr 1e-5, 20 epochs, batch 16
as 4 x 4 gradient accumulation, 50 warmup steps, cosine schedule, no weight
decay, mixed precision; the model of the last epoch is scored. The none run
saves the [CLS] vectors of its train and test methods to
results/features/seed<seed>_<layers>L_<representation>.npz for the smote and
head runs. One row per run goes to results/main_experiment.csv and the mean
train loss of every epoch to results/main_loss.csv; finished runs are skipped
on a rerun.

Usage:
    pip install torch transformers codecarbon numpy
    python scripts/4_main_experiment.py --country JPN        # seeds 0-9, every scenario, plus none with 12 layers
    python scripts/4_main_experiment.py --seeds 0 --scenarios none weight
    python scripts/4_main_experiment.py --confirm-layers     # skip the 12-layer confirmation
    python scripts/4_main_experiment.py --representation nocomment --scenarios none augment --ratios 0.61
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
import zlib

# The module names start with a digit, so a plain import statement cannot load them
model_size = importlib.import_module("3_model_size")
log = model_size.log

SCENARIOS = {"none": ["0.38"], "head": ["0.38"], "weight": ["0.61", "1.0"],
             "augment": ["0.61", "1.0"], "smote": ["0.61", "1.0"]}
# none comes first: smote and head read the vectors its run saves
ORDER = ["none", "head", "weight", "augment", "smote"]

RESULT_COLUMNS = ["seed", "scenario", "ratio", "layers", "representation", "stage",
                  "train_clean", "train_smell", "weight_clean", "weight_smell",
                  "params_total", "params_trainable",
                  "accuracy", "precision", "recall", "f1", "mcc", "tp", "fp", "fn", "tn",
                  "train_seconds", "test_seconds", "peak_gpu_mb", "energy_kwh", "emissions_kg", "country",
                  "head", "epochs", "lr", "batch_size", "grad_accum", "scheduler", "warmup_steps",
                  "weight_decay", "max_len", "head_epochs", "head_lr", "smote_k"]
LOSS_COLUMNS = ["seed", "scenario", "ratio", "layers", "representation", "stage", "epoch", "train_loss"]


def load_rows(dataset_csv, ratio):
    """Train rows used at the given ratio and the test rows of one seed."""
    train, test = [], []
    with open(dataset_csv, newline="", encoding="utf-8") as f:
        for r in csv.DictReader(f):
            if r["split"] == "test":
                test.append(r)
            elif float(r["ratio"]) <= float(ratio):
                train.append(r)
    return train, test


def read_code(rows, data_dir):
    codes = []
    for r in rows:
        with open(os.path.join(data_dir, r["is_augmented"], r["filename"]), encoding="utf-8") as f:
            codes.append(f.read())
    return codes


def class_counts(rows):
    return [sum(r["label"] == c for r in rows) for c in ("0", "1")]


def start_tracker(args):
    if not args.codecarbon:
        return None
    try:
        from codecarbon import OfflineEmissionsTracker
    except ImportError:
        raise SystemExit("CodeCarbon is not installed: pip install codecarbon, or run with --no-codecarbon")
    tracker = OfflineEmissionsTracker(country_iso_code=args.country, log_level="error", save_to_file=False)
    tracker.start()
    return tracker


def stop_tracker(tracker):
    if tracker is None:
        return None
    # CodeCarbon swallows its own errors in stop() and then never sets final_emissions_data
    tracker.stop()
    energy = getattr(tracker, "final_emissions_data", None)
    if energy is None:
        log("CodeCarbon could not report this run; energy and emissions are left empty")
    return energy


def cost_values(args, device, train_seconds, test_seconds, energy):
    import torch
    return {
        "train_seconds": round(train_seconds, 1),
        "test_seconds": round(test_seconds, 2),
        "peak_gpu_mb": round(torch.cuda.max_memory_allocated() / 2**20) if device.type == "cuda" else "",
        "energy_kwh": round(energy.energy_consumed, 6) if energy else "",
        "emissions_kg": round(energy.emissions, 6) if energy else "",
        "country": args.country if energy else "",
    }


def score_values(labels, preds):
    return {k: round(v, 4) if isinstance(v, float) else v for k, v in model_size.scores(labels, preds).items()}


def finetune(seed, scenario, ratio, n_layers, args):
    """End-to-end fine-tuning (none, weight, augment). Returns the result row and the loss rows."""
    import torch
    from transformers import get_cosine_schedule_with_warmup, get_linear_schedule_with_warmup

    model_size.set_seed(seed)
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    log(f"seed {seed}, {scenario} {ratio}, {n_layers} layers: loading {args.model}")
    base, tokenizer = args.load(args.model)
    model = model_size.build_classifier(base, n_layers, tokenizer.pad_token_id).to(device)
    pad_id = tokenizer.pad_token_id

    all_train, test_rows = load_rows(os.path.join(args.dataset_dir, f"dataset_seed{seed}.csv"), ratio)
    originals = [r for r in all_train if r["is_augmented"] == "0"]
    train_rows = all_train if scenario == "augment" else originals
    data_dir = args.comment_dir if args.representation == "comment" else args.nocomment_dir
    train = [(model_size.encode(tokenizer, c, args.max_len), int(r["label"]))
             for c, r in zip(read_code(train_rows, data_dir), train_rows)]
    test = [(model_size.encode(tokenizer, c, args.max_len), int(r["label"]))
            for c, r in zip(read_code(test_rows, data_dir), test_rows)]

    # Class weights give each class the weight of its rows at this ratio, extra rows included
    weights = [1.0, 1.0]
    if scenario == "weight":
        weights = [a / o for a, o in zip(class_counts(all_train), class_counts(originals))]
    n_clean, n_smell = class_counts(train_rows)
    log(f"{len(train)} train ({n_clean} clean, {n_smell} smelly) and {len(test)} test methods; "
        f"class weights {weights[0]:.4f} / {weights[1]:.4f}")

    optimizer = torch.optim.AdamW(model.parameters(), lr=args.lr, weight_decay=args.weight_decay)
    steps = args.epochs * math.ceil(math.ceil(len(train) / args.batch_size) / args.grad_accum)
    schedule = get_cosine_schedule_with_warmup if args.scheduler == "cosine" else get_linear_schedule_with_warmup
    scheduler = schedule(optimizer, args.warmup_steps, steps)
    loss_fn = torch.nn.CrossEntropyLoss(weight=torch.tensor(weights, device=device))
    use_amp = device.type == "cuda"
    scaler = torch.amp.GradScaler("cuda", enabled=use_amp)
    rng = random.Random(seed)

    tracker = start_tracker(args)
    if device.type == "cuda":
        torch.cuda.reset_peak_memory_stats()
    start = time.time()
    losses_per_epoch = []
    model.train()
    for epoch in range(1, args.epochs + 1):
        losses = []
        # The shuffle does not depend on the batch size, so batch 4 with 4 accumulation steps
        # sees the same groups of 16 as batch 16 and makes the same updates with less memory
        micro_batches = list(model_size.batches(train, args.batch_size, pad_id, True, rng))
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
        losses_per_epoch.append(statistics.mean(losses))
        log(f"  epoch {epoch:>2}/{args.epochs}: loss {losses_per_epoch[-1]:.4f}, {time.time() - start:.0f}s elapsed")
    train_seconds = time.time() - start
    energy = stop_tracker(tracker)

    log("scoring the test split")
    model.eval()
    start = time.time()
    preds, labels = [], []
    with torch.no_grad():
        for ids, batch_labels in model_size.batches(test, args.batch_size, pad_id, False, rng):
            with torch.autocast(device.type, enabled=use_amp):
                logits = model(ids.to(device))
            preds += logits.argmax(-1).tolist()
            labels += batch_labels.tolist()
    test_seconds = time.time() - start

    if scenario == "none":
        save_features(model, pad_id, device, use_amp, args, seed, n_layers,
                      train, train_rows, test, test_rows)

    result = {
        "seed": seed, "scenario": scenario, "ratio": ratio, "layers": n_layers, "stage": "end-to-end",
        "train_clean": n_clean, "train_smell": n_smell,
        "weight_clean": round(weights[0], 4), "weight_smell": round(weights[1], 4),
        "params_total": sum(p.numel() for p in model.parameters()),
        "params_trainable": sum(p.numel() for p in model.parameters() if p.requires_grad),
        **score_values(labels, preds), **cost_values(args, device, train_seconds, test_seconds, energy),
        **config_values(args),
    }
    loss_rows = [{"seed": seed, "scenario": scenario, "ratio": ratio, "layers": n_layers,
                  "representation": args.representation, "stage": "end-to-end", "epoch": e,
                  "train_loss": round(l, 6)} for e, l in enumerate(losses_per_epoch, start=1)]
    return result, loss_rows


def features_path(args, seed, n_layers):
    return os.path.join(args.features_dir, f"seed{seed}_{n_layers}L_{args.representation}.npz")


def save_features(model, pad_id, device, use_amp, args, seed, n_layers, train, train_rows, test, test_rows):
    """Save the final-layer [CLS] vector, the input of the classification head, of every method."""
    import numpy as np
    import torch

    def vectors(examples):
        out = []
        with torch.no_grad():
            for ids, _ in model_size.batches(examples, args.batch_size, pad_id, False, None):
                ids = ids.to(device)
                with torch.autocast(device.type, enabled=use_amp):
                    hidden = model.model.base_model(input_ids=ids, attention_mask=ids.ne(pad_id)).last_hidden_state
                out.append(hidden[:, 0, :].float().cpu().numpy())
        return np.concatenate(out)

    path = features_path(args, seed, n_layers)
    os.makedirs(os.path.dirname(path) or ".", exist_ok=True)
    np.savez(path, train_x=vectors(train), train_y=np.array([l for _, l in train]),
             train_ids=np.array([r["sample_id"] for r in train_rows]),
             test_x=vectors(test), test_y=np.array([l for _, l in test]),
             test_ids=np.array([r["sample_id"] for r in test_rows]))
    log(f"[CLS] vectors -> {path}")


def smote_points(x, y, ids, extra_rows, k, seed):
    """One SMOTE point per extra row: its source method moved a random fraction of the way
    to one of its k nearest neighbours of the same class (Chawla et al., 2002)."""
    import numpy as np
    index = {sid: i for i, sid in enumerate(ids)}
    points, labels = [], []
    for r in extra_rows:
        i = index[r["sample_id"]]
        same = np.flatnonzero((y == y[i]) & (np.arange(len(y)) != i))
        distance = np.linalg.norm(x[same] - x[i], axis=1)
        neighbours = same[np.argsort(distance, kind="stable")[:k]]
        # Seeded by the row, so the points made at 0.61 are made again at 1.0
        rng = np.random.default_rng(zlib.crc32(f"{seed}/{r['sample_id']}/{r['is_augmented']}".encode()))
        j = rng.choice(neighbours)
        points.append(x[i] + rng.random() * (x[j] - x[i]))
        labels.append(y[i])
    return np.array(points).reshape(-1, x.shape[1]), np.array(labels, dtype=y.dtype)


def train_head(seed, scenario, ratio, n_layers, args):
    """Head stage of smote and of its control: a fresh [CLS] head on frozen vectors."""
    import numpy as np
    import torch
    from transformers import AutoConfig, get_cosine_schedule_with_warmup
    from transformers.models.roberta.modeling_roberta import RobertaClassificationHead

    data = np.load(features_path(args, seed, n_layers))
    x, y, ids = data["train_x"], data["train_y"], list(data["train_ids"])
    all_train, _ = load_rows(os.path.join(args.dataset_dir, f"dataset_seed{seed}.csv"), ratio)
    extra_rows = [r for r in all_train if r["is_augmented"] != "0"]
    if scenario == "smote":
        sx, sy = smote_points(x, y, ids, extra_rows, args.smote_k, seed)
        x, y = np.concatenate([x, sx]), np.concatenate([y, sy])
    n_clean, n_smell = int((y == 0).sum()), int((y == 1).sum())
    log(f"seed {seed}, {scenario} {ratio}, {n_layers} layers: head on {len(y)} vectors "
        f"({n_clean} clean, {n_smell} smelly)")

    model_size.set_seed(seed)
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    config = AutoConfig.from_pretrained(args.model, num_labels=2) if args.config is None else args.config
    head = RobertaClassificationHead(config)
    # Same initialisation as a new head of AutoModelForSequenceClassification
    for module in (head.dense, head.out_proj):
        module.weight.data.normal_(0.0, config.initializer_range)
        module.bias.data.zero_()
    head.to(device)

    xt = torch.tensor(x, dtype=torch.float32)
    yt = torch.tensor(y, dtype=torch.long)
    optimizer = torch.optim.AdamW(head.parameters(), lr=args.head_lr, weight_decay=args.weight_decay)
    steps = args.head_epochs * math.ceil(len(yt) / (args.batch_size * args.grad_accum))
    scheduler = get_cosine_schedule_with_warmup(optimizer, 0, steps)
    loss_fn = torch.nn.CrossEntropyLoss()
    rng = random.Random(seed)

    tracker = start_tracker(args)
    if device.type == "cuda":
        torch.cuda.reset_peak_memory_stats()
    start = time.time()
    losses_per_epoch = []
    head.train()
    for epoch in range(1, args.head_epochs + 1):
        order = list(range(len(yt)))
        rng.shuffle(order)
        losses = []
        for i in range(0, len(order), args.batch_size * args.grad_accum):
            batch = order[i:i + args.batch_size * args.grad_accum]
            # The head takes the hidden states of a sequence and reads position 0
            logits = head(xt[batch].unsqueeze(1).to(device))
            loss = loss_fn(logits, yt[batch].to(device))
            optimizer.zero_grad()
            loss.backward()
            optimizer.step()
            scheduler.step()
            losses.append(loss.item())
        losses_per_epoch.append(statistics.mean(losses))
    log(f"  head loss {losses_per_epoch[0]:.4f} (epoch 1) -> {losses_per_epoch[-1]:.4f} (epoch {args.head_epochs})")
    train_seconds = time.time() - start
    energy = stop_tracker(tracker)

    head.eval()
    start = time.time()
    with torch.no_grad():
        logits = head(torch.tensor(data["test_x"], dtype=torch.float32).unsqueeze(1).to(device))
    preds = logits.argmax(-1).tolist()
    test_seconds = time.time() - start

    result = {
        "seed": seed, "scenario": scenario, "ratio": ratio, "layers": n_layers, "stage": "head",
        "train_clean": n_clean, "train_smell": n_smell, "weight_clean": 1.0, "weight_smell": 1.0,
        "params_total": sum(p.numel() for p in head.parameters()),
        "params_trainable": sum(p.numel() for p in head.parameters()),
        **score_values(data["test_y"].tolist(), preds),
        **cost_values(args, device, train_seconds, test_seconds, energy),
        **config_values(args),
    }
    loss_rows = [{"seed": seed, "scenario": scenario, "ratio": ratio, "layers": n_layers,
                  "representation": args.representation, "stage": "head", "epoch": e,
                  "train_loss": round(l, 6)} for e, l in enumerate(losses_per_epoch, start=1)]
    return result, loss_rows


def config_values(args):
    return {"representation": args.representation, "head": "cls", "epochs": args.epochs, "lr": args.lr,
            "batch_size": args.batch_size, "grad_accum": args.grad_accum, "scheduler": args.scheduler,
            "warmup_steps": args.warmup_steps, "weight_decay": args.weight_decay, "max_len": args.max_len,
            "head_epochs": args.head_epochs, "head_lr": args.head_lr, "smote_k": args.smote_k}


def same_config(row, args):
    """A finished run is reused only when it was trained with the same settings."""
    # Settings of the head stage do not concern the end-to-end runs
    head_only = set() if row.get("stage") == "head" else {"head_epochs", "head_lr", "smote_k"}
    return all(str(row.get(c, "")) == str(v) for c, v in config_values(args).items() if c not in head_only)


def append_rows(path, columns, rows):
    new_file = not os.path.exists(path)
    os.makedirs(os.path.dirname(path) or ".", exist_ok=True)
    with open(path, "a", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=columns)
        if new_file:
            writer.writeheader()
        writer.writerows(rows)


def plan(args):
    """(scenario, ratio, layers) runs per seed, none first."""
    runs = []
    for scenario in ORDER:
        if scenario not in args.scenarios:
            continue
        for ratio in SCENARIOS[scenario]:
            if args.ratios and ratio not in args.ratios:
                continue
            runs += [(scenario, ratio, n) for n in args.layers]
            if scenario == "none":
                runs += [(scenario, ratio, n) for n in args.confirm_layers if n not in args.layers]
    return runs


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--seeds", type=int, nargs="*", default=list(range(10)))
    parser.add_argument("--scenarios", nargs="*", choices=ORDER, default=ORDER)
    parser.add_argument("--ratios", nargs="*", choices=["0.38", "0.61", "1.0"], default=[],
                        help="keep only these train ratios (default: all)")
    parser.add_argument("--layers", type=int, nargs="*", default=[4], help="encoder layers kept, every scenario")
    parser.add_argument("--confirm-layers", type=int, nargs="*", default=[12],
                        help="extra layer counts run for the none scenario only")
    parser.add_argument("--representation", choices=["comment", "nocomment"], default="comment")
    parser.add_argument("--dataset-dir", default="datasets")
    parser.add_argument("--comment-dir", default="ds_comment")
    parser.add_argument("--nocomment-dir", default="ds_nocomment")
    parser.add_argument("--features-dir", default="results/features")
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
    parser.add_argument("--head-epochs", type=int, default=20, help="epochs of the smote and head stage")
    parser.add_argument("--head-lr", type=float, default=1e-3, help="learning rate of the smote and head stage")
    parser.add_argument("--smote-k", type=int, default=5)
    parser.add_argument("--codecarbon", action=argparse.BooleanOptionalAction, default=True,
                        help="measure training energy and emissions with CodeCarbon (on by default)")
    parser.add_argument("--country", default="IDN",
                        help="ISO 3166-1 alpha-3 code whose grid carbon intensity converts energy to emissions")
    parser.add_argument("--out", default="results/main_experiment.csv")
    parser.add_argument("--loss-out", default="results/main_loss.csv")
    parser.add_argument("--allow-cpu", action="store_true",
                        help="train on the CPU when no GPU is found instead of stopping")
    args = parser.parse_args()
    args.load, args.config = model_size.load_model_and_tokenizer, None

    # Lets the CUDA allocator grow segments instead of fragmenting; must be set before CUDA starts
    os.environ.setdefault("PYTORCH_CUDA_ALLOC_CONF", "expandable_segments:True")
    log("importing torch and transformers")
    import torch
    import transformers  # noqa: F401
    log("imports done")
    if not torch.cuda.is_available() and not args.allow_cpu:
        raise SystemExit("No usable GPU found (see the CUDA warning above, or check nvidia-smi). "
                         "CPU runs take far longer and their time and energy are not comparable; "
                         "pass --allow-cpu to run on the CPU anyway.")
    run_all(args)


def run_all(args):
    import torch
    done = {(r["seed"], r["scenario"], r["ratio"], r["layers"])
            for r in model_size.read_results(args.out) if same_config(r, args)}
    for seed in args.seeds:
        for scenario, ratio, n_layers in plan(args):
            key = (str(seed), scenario, ratio, str(n_layers))
            if key in done:
                log(f"[skip] seed {seed}, {scenario} {ratio}, {n_layers} layers: already in {args.out}")
                continue
            if scenario in ("smote", "head") and not os.path.exists(features_path(args, seed, n_layers)):
                # Train the none model for its vectors; when its row is already recorded (the
                # vectors were deleted), keep that row
                log(f"seed {seed}: no [CLS] vectors at {features_path(args, seed, n_layers)}, "
                    f"training the none model again to make them")
                result, loss_rows = finetune(seed, "none", "0.38", n_layers, args)
                none_key = (str(seed), "none", "0.38", str(n_layers))
                if none_key not in done:
                    append_rows(args.out, RESULT_COLUMNS, [result])
                    append_rows(args.loss_out, LOSS_COLUMNS, loss_rows)
                    done.add(none_key)
            if scenario in ("smote", "head"):
                result, loss_rows = train_head(seed, scenario, ratio, n_layers, args)
            else:
                result, loss_rows = finetune(seed, scenario, ratio, n_layers, args)
            # Hand the finished model's GPU memory back before the next run
            gc.collect()
            if torch.cuda.is_available():
                torch.cuda.empty_cache()
            append_rows(args.out, RESULT_COLUMNS, [result])
            append_rows(args.loss_out, LOSS_COLUMNS, loss_rows)
            done.add(key)
            log(f"seed {seed}, {scenario} {ratio}, {n_layers} layers: precision {result['precision']:.3f}, "
                f"F1 {result['f1']:.3f}, MCC {result['mcc']:.3f}, {result['train_seconds']}s")
    print(f"-> {args.out}, {args.loss_out}")


if __name__ == "__main__":
    main()
