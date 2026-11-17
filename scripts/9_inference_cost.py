"""Inference cost of the pruned and the full UniXcoder: latency and energy per method.

Scoring the 70 test methods takes a fraction of a second, too short for
CodeCarbon to measure, so the 350 original methods (code with comments, the
input of the experiments) are scored again and again, --repeats times, and
the time and energy are divided by the number of methods scored. The weights
do not change the cost, so the classifier is the pretrained encoder cut to k
layers under a fresh [CLS] head, as before fine-tuning. Two settings, each with
mixed precision as in training:

  batch      methods in batches of --batch-size (16), the cost of scanning a
             code base; CodeCarbon measures the energy (GPU, CPU and RAM parts)
  latency    one method at a time, the wait for a single method in an editor or
             code review; the median and 95th percentile per method

Each setting starts with --warmup passes that are not counted. One row per
(layers, setting) goes to results/inference_cost.csv.

Usage:
    pip install torch transformers codecarbon
    python scripts/9_inference_cost.py                  # 4 and 12 layers
    python scripts/9_inference_cost.py --layers 4 12 2 --repeats 50
"""
import argparse
import csv
import importlib
import os
import statistics
import time

model_size = importlib.import_module("3_model_size")
main_experiment = importlib.import_module("4_main_experiment")
log = model_size.log

COLUMNS = ["layers", "setting", "batch_size", "methods", "repeats", "seconds", "ms_per_method",
           "ms_per_method_p95", "energy_kwh", "gpu_energy_kwh", "cpu_energy_kwh", "ram_energy_kwh",
           "mwh_per_method", "cpu_mode", "country", "peak_gpu_mb", "params_total"]


def originals(dataset_csv, data_dir):
    with open(dataset_csv, newline="", encoding="utf-8") as f:
        rows = [r for r in csv.DictReader(f) if r["is_augmented"] == "0"]
    return rows, main_experiment.read_code(rows, data_dir)


def measure(n_layers, examples, args, device, pad_id, base):
    import torch
    model = model_size.build_classifier(base, n_layers, pad_id).to(device).eval()
    use_amp = device.type == "cuda"
    sync = torch.cuda.synchronize if device.type == "cuda" else (lambda: None)

    def run_batches(batch_size):
        with torch.no_grad():
            for ids, _ in model_size.batches(examples, batch_size, pad_id, False, None):
                with torch.autocast(device.type, enabled=use_amp):
                    model(ids.to(device))
        sync()

    rows = []
    # Batch: whole passes over the methods, energy measured by CodeCarbon
    for _ in range(args.warmup):
        run_batches(args.batch_size)
    if device.type == "cuda":
        torch.cuda.reset_peak_memory_stats()
    tracker = main_experiment.start_tracker(args)
    start = time.time()
    for _ in range(args.repeats):
        run_batches(args.batch_size)
    seconds = time.time() - start
    energy = main_experiment.stop_tracker(tracker)
    scored = len(examples) * args.repeats
    part = lambda name: round(getattr(energy, name), 8) if energy else ""
    rows.append({
        "layers": n_layers, "setting": "batch", "batch_size": args.batch_size, "methods": len(examples),
        "repeats": args.repeats, "seconds": round(seconds, 2), "ms_per_method": round(1000 * seconds / scored, 3),
        "ms_per_method_p95": "", "energy_kwh": part("energy_consumed"), "gpu_energy_kwh": part("gpu_energy"),
        "cpu_energy_kwh": part("cpu_energy"), "ram_energy_kwh": part("ram_energy"),
        # kWh to mWh: x 1e6
        "mwh_per_method": round(1e6 * energy.energy_consumed / scored, 4) if energy else "",
        "cpu_mode": getattr(energy, "cpu_mode", "") if energy else "", "country": args.country if energy else "",
        "peak_gpu_mb": round(torch.cuda.max_memory_allocated() / 2**20) if device.type == "cuda" else "",
        "params_total": sum(p.numel() for p in model.parameters()),
    })
    log(f"{n_layers} layers, batch {args.batch_size}: {rows[-1]['ms_per_method']} ms and "
        f"{rows[-1]['mwh_per_method']} mWh per method")

    # Latency: one method at a time, timed one by one
    for _ in range(args.warmup):
        run_batches(1)
    times = []
    with torch.no_grad():
        for _ in range(args.latency_repeats):
            for ids, _ in model_size.batches(examples, 1, pad_id, False, None):
                start = time.perf_counter()
                with torch.autocast(device.type, enabled=use_amp):
                    model(ids.to(device))
                sync()
                times.append(1000 * (time.perf_counter() - start))
    times.sort()
    rows.append({
        "layers": n_layers, "setting": "latency", "batch_size": 1, "methods": len(examples),
        "repeats": args.latency_repeats, "seconds": round(sum(times) / 1000, 2),
        "ms_per_method": round(statistics.median(times), 3),
        "ms_per_method_p95": round(times[int(0.95 * (len(times) - 1))], 3),
        "energy_kwh": "", "gpu_energy_kwh": "", "cpu_energy_kwh": "", "ram_energy_kwh": "", "mwh_per_method": "",
        "cpu_mode": "", "country": "", "peak_gpu_mb": "", "params_total": rows[0]["params_total"],
    })
    log(f"{n_layers} layers, one at a time: median {rows[-1]['ms_per_method']} ms, "
        f"95th percentile {rows[-1]['ms_per_method_p95']} ms")
    del model
    return rows


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--layers", type=int, nargs="*", default=[4, 12])
    parser.add_argument("--dataset", default="datasets/dataset_seed0.csv",
                        help="its original methods are scored (every seed holds the same 350)")
    parser.add_argument("--comment-dir", default="ds_comment")
    parser.add_argument("--model", default="microsoft/unixcoder-base")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--batch-size", type=int, default=16)
    parser.add_argument("--repeats", type=int, default=50, help="passes over the 350 methods in the batch setting")
    parser.add_argument("--latency-repeats", type=int, default=3, help="passes, one method at a time")
    parser.add_argument("--warmup", type=int, default=2, help="uncounted passes before each setting")
    parser.add_argument("--codecarbon", action=argparse.BooleanOptionalAction, default=True)
    parser.add_argument("--country", default="JPN")
    parser.add_argument("--measure-power-secs", type=float, default=1.0)
    parser.add_argument("--out", default="results/inference_cost.csv")
    parser.add_argument("--allow-cpu", action="store_true")
    args = parser.parse_args()

    import torch
    if not torch.cuda.is_available() and not args.allow_cpu:
        raise SystemExit("No usable GPU found; pass --allow-cpu to measure on the CPU anyway.")
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    _, codes = originals(args.dataset, args.comment_dir)
    rows = []
    for n_layers in args.layers:
        # A fresh copy each time: build_classifier cuts the encoder of the model it is given
        base, tokenizer = model_size.load_model_and_tokenizer(args.model)
        examples = [(model_size.encode(tokenizer, c, args.max_len), 0) for c in codes]
        rows += measure(n_layers, examples, args, device, tokenizer.pad_token_id, base)
        del base
        if device.type == "cuda":
            torch.cuda.empty_cache()
    os.makedirs(os.path.dirname(args.out) or ".", exist_ok=True)
    with open(args.out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=COLUMNS)
        writer.writeheader()
        writer.writerows(rows)
    print(f"-> {args.out}")


if __name__ == "__main__":
    main()
