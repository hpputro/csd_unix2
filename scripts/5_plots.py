"""Figures of the main experiment, drawn from its result files only (no training).

Reads results/main_experiment.csv and results/main_loss.csv written by
4_main_experiment.py and draws, for one representation (comment by default):

  figures/figure6a_main_precision_by_share.png    precision against the smelly share of the train side
  figures/figure6b_main_f1_by_share.png           F1-score against the smelly share of the train side
  figures/figure7a_main_precision_scenarios.png   precision of every scenario, one dot per seed
  figures/figure7b_main_f1_scenarios.png          F1-score of every scenario, one dot per seed
  figures/figure9_main_cost.png                   F1-score against training time per scenario
  figures/extra_main_loss.png                     train loss per epoch of the 4- and 12-layer none runs

The number is that of the figure in the paper draft (extra_ when the draft does
not number it yet); main_ after it marks a figure of this script, as dataset_
and model_size_ mark those of 0_dataset_analysis.py and 3_model_size.py.

Each figure carries a short title; what it shows in detail is written in the
docstring of the function that draws it. Precision, recall and F1 are those of
the smelly class.

Usage:
    pip install matplotlib
    python scripts/5_plots.py
    python scripts/5_plots.py --representation nocomment --suffix _nocomment
"""
import argparse
import csv
import os
import statistics

# Categorical slots 1-3 of the chart palette, in fixed order, and a gray for the
# shared reference (no balancing); the same blue, orange and green as the other figures
METHODS = [("weight", "Class weighting", "#2a78d6", "o"),
           ("augment", "Augmentation", "#eb6834", "s"),
           ("smote", "SMOTE", "#1baf7a", "^")]
REFERENCE = "#8a8984"
INK = "#0b0b0b"
MUTED = "#52514e"
GRID = "#e4e3df"
SHARES = ["27.5", "37.7", "50.0"]
# Scenario rows of the per-scenario figures, top to bottom
SCENARIO_ROWS = [("none", "27.5", "No balancing", REFERENCE),
                 ("head", "27.5", "Head retrained (SMOTE control)", REFERENCE),
                 ("weight", "37.7", "Class weighting 37.7%", METHODS[0][2]),
                 ("weight", "50.0", "Class weighting 50%", METHODS[0][2]),
                 ("augment", "37.7", "Augmentation 37.7%", METHODS[1][2]),
                 ("augment", "50.0", "Augmentation 50%", METHODS[1][2]),
                 ("smote", "37.7", "SMOTE 37.7%", METHODS[2][2]),
                 ("smote", "50.0", "SMOTE 50%", METHODS[2][2])]


def read(path):
    with open(path, newline="", encoding="utf-8") as f:
        return list(csv.DictReader(f))


def setup():
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    # DejaVu Sans ships with matplotlib, so the figure is the same on every machine
    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})
    return plt


def style(ax, title, grid_axis="y"):
    ax.set_title(title, loc="left", fontsize=10, color=INK, pad=10)
    ax.spines[["top", "right"]].set_visible(False)
    ax.spines[["left", "bottom"]].set_color(GRID)
    ax.tick_params(colors=MUTED, labelsize=9)
    ax.grid(axis=grid_axis, color=GRID, linewidth=0.8)
    ax.set_axisbelow(True)


def save(plt, fig, out):
    fig.tight_layout()
    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    fig.savefig(out, dpi=300)
    plt.close(fig)
    print(f"-> {out}")


def values(rows, scenario, share, metric, layers="4"):
    return [float(r[metric]) for r in rows
            if r["scenario"] == scenario and r["smell_pct"] == share and r["layers"] == layers]


def plot_by_share(rows, metric, label, out):
    """Mean of `metric` over the seeds (0-9) against the smelly share of the train side, one line
    per balancing method; every line starts at the no-balancing run (27.5%, the natural share,
    gray point) and the whiskers span one standard deviation across seeds. Methods are nudged
    apart along x so the whiskers do not overlap; the legend names the lines (marker shape and
    color). The test side always holds 27.1% smelly."""
    plt = setup()
    fig, ax = plt.subplots(figsize=(6.4, 4.2))
    base = values(rows, "none", "27.5", metric)
    x = [float(s) for s in SHARES]
    for i, (scenario, name, color, marker) in enumerate(METHODS):
        shift = (i - 1) * 0.6
        series = [base] + [values(rows, scenario, s, metric) for s in SHARES[1:]]
        means = [statistics.mean(v) for v in series]
        sds = [statistics.stdev(v) for v in series]
        # Every line leaves the shared no-balancing point itself; only its own points are nudged
        xs = [x[0]] + [v + shift for v in x[1:]]
        ax.errorbar(xs[1:], means[1:], yerr=sds[1:], fmt="none", ecolor=color, elinewidth=1, capsize=0, alpha=0.5)
        ax.plot(xs, means, color=color, linewidth=2, zorder=3)
        ax.plot(xs[1:], means[1:], linestyle="none", marker=marker, markersize=8, color=color,
                markeredgecolor="white", markeredgewidth=1.5, zorder=4, label=name)
    mean, sd = statistics.mean(base), statistics.stdev(base)
    ax.errorbar([x[0]], [mean], yerr=[sd], fmt="none", ecolor=REFERENCE, elinewidth=1, alpha=0.6)
    ax.plot([x[0]], [mean], marker="o", markersize=8, color=REFERENCE, markeredgecolor="white",
            markeredgewidth=1.5, linestyle="none", zorder=5, label="No balancing")
    ax.set_xticks(x, [f"{s}%" for s in SHARES])
    ax.set_xlim(24, 53)
    ax.set_xlabel("Smelly share of the train side", color=MUTED)
    ax.set_ylabel(label, color=MUTED)
    # Below the axis, where no whisker can run through it
    ax.legend(frameon=False, fontsize=9, loc="upper center", bbox_to_anchor=(0.5, -0.17), ncol=4,
              handletextpad=0.3, columnspacing=1.2)
    style(ax, f"{label} by smell share")
    save(plt, fig, out)


def plot_scenarios(rows, metric, label, out):
    """`metric` of every scenario on the 4-layer model: one dot per seed (0-9), a black tick at the
    mean, and a dashed line at the no-balancing mean. Rows of the same method share its color;
    the head-retrained row is the control for SMOTE (fresh head on the frozen no-balancing
    encoder, no synthetic points)."""
    plt = setup()
    fig, ax = plt.subplots(figsize=(6.8, 4.4))
    labels = []
    for y, (scenario, share, name, color) in enumerate(reversed(SCENARIO_ROWS)):
        v = values(rows, scenario, share, metric)
        # Seeds with the same score are spread vertically so every dot shows
        counts = {}
        offsets = []
        for value in v:
            n = counts.get(value, 0)
            counts[value] = n + 1
            offsets.append((n % 5 - 2) * 0.07 if n else 0.0)
        ax.scatter(v, [y + o for o in offsets], s=26, color=color, alpha=0.75, edgecolors="white",
                   linewidths=0.6, zorder=3)
        mean = statistics.mean(v)
        ax.plot([mean, mean], [y - 0.3, y + 0.3], color=INK, linewidth=2, zorder=4)
        ax.text(1.005, y, f"{mean:.3f}", transform=ax.get_yaxis_transform(), color=MUTED, fontsize=8.5,
                va="center")
        labels.append(name)
    ax.axvline(statistics.mean(values(rows, "none", "27.5", metric)), color=MUTED, linestyle="--",
               linewidth=1, zorder=1)
    ax.set_yticks(range(len(labels)), labels)
    ax.set_xlabel(f"{label} on the test split", color=MUTED)
    style(ax, f"{label} per scenario", grid_axis="x")
    save(plt, fig, out)


def plot_cost(rows, out):
    """Mean F1-score over the seeds against mean training time per run (seconds, GPU) for every
    scenario on the 4-layer model. SMOTE and the head control are charged the time of the
    no-balancing run of the same seed plus their head stage, since they reuse its encoder.
    Energy was not measured in this run of the experiment; in the model-size ablation it grew in
    proportion to training time (near-constant GPU power)."""
    plt = setup()
    fig, ax = plt.subplots(figsize=(6.4, 4.2))
    base_time = {r["seed"]: float(r["train_seconds"]) for r in rows
                 if r["scenario"] == "none" and r["layers"] == "4"}
    colors = {"none": REFERENCE, "head": REFERENCE, **{m: c for m, _, c, _ in METHODS}}
    markers = {"none": "o", "head": "D", **{m: k for m, _, _, k in METHODS}}
    for scenario, share, name, _ in SCENARIO_ROWS:
        runs = [r for r in rows if r["scenario"] == scenario and r["smell_pct"] == share and r["layers"] == "4"]
        times = [float(r["train_seconds"]) + (base_time[r["seed"]] if r["stage"] == "head" else 0.0) for r in runs]
        t, f1 = statistics.mean(times), statistics.mean(float(r["f1"]) for r in runs)
        ax.plot([t], [f1], marker=markers[scenario], markersize=9, color=colors[scenario], linestyle="none",
                markeredgecolor="white", markeredgewidth=1.5, zorder=3)
        short = name.replace(" (SMOTE control)", "").replace("Class weighting", "Weighting")
        # Runs that share the no-balancing time sit in one column: weighting and no balancing are
        # labelled on the left, the head-stage runs (SMOTE, head retrained) on the right
        if t < 40 and scenario in ("none", "weight"):
            ax.text(t - 1.2, f1, short, ha="right", va="center", fontsize=8.5, color=INK)
        else:
            ax.text(t + 1.5, f1, short, ha="left", va="center", fontsize=8.5, color=INK)
    ax.set_xlim(0, 95)
    ax.set_xlabel("Training time per run (s)", color=MUTED)
    ax.set_ylabel("F1-score on the test split", color=MUTED)
    style(ax, "F1-score versus training time", grid_axis="both")
    save(plt, fig, out)


def plot_loss(loss_rows, out):
    """Mean train loss per epoch over the seeds (0-9) of the no-balancing runs with 4 and 12 encoder
    layers, each seed as a faint line, the mean at epoch 20 written at the line end. The dotted
    line is the loss of a model that learns only the class proportion of the train split (77
    smelly of 280, 0.589 nats)."""
    import math
    plt = setup()
    fig, ax = plt.subplots(figsize=(6.4, 4.0))
    for layers, color in (("12", "#eb6834"), ("4", "#2a78d6")):
        runs = {}
        for r in loss_rows:
            if r["scenario"] == "none" and r["layers"] == layers:
                runs.setdefault(r["seed"], []).append((int(r["epoch"]), float(r["train_loss"])))
        for points in runs.values():
            points.sort()
            ax.plot([e for e, _ in points], [l for _, l in points], color=color, alpha=0.2, linewidth=1)
        epochs = sorted({e for points in runs.values() for e, _ in points})
        means = [statistics.mean(l for points in runs.values() for e, l in points if e == epoch) for epoch in epochs]
        ax.plot(epochs, means, color=color, linewidth=2, marker="o", markersize=4, label=f"{layers} layers")
        ax.text(epochs[-1] + 0.3, means[-1], f"{means[-1]:.3f}", color=color, fontsize=8.5, va="center")
    prior = 77 / 280
    ax.axhline(-(prior * math.log(prior) + (1 - prior) * math.log(1 - prior)), color=MUTED, linestyle=":",
               linewidth=1.2, label="Class proportion only")
    ax.set_ylim(bottom=0)
    ax.set_xticks(range(0, 21, 5))
    ax.set_xlabel("Epoch", color=MUTED)
    ax.set_ylabel("Train loss", color=MUTED)
    ax.legend(frameon=False, fontsize=9, loc="upper right")
    style(ax, "Training loss per epoch")
    save(plt, fig, out)


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--results", default="results/main_experiment.csv")
    parser.add_argument("--loss", default="results/main_loss.csv")
    parser.add_argument("--representation", choices=["comment", "nocomment"], default="comment")
    parser.add_argument("--figures-dir", default="figures")
    parser.add_argument("--suffix", default="", help="added to every file name, e.g. _nocomment")
    args = parser.parse_args()

    rows = [r for r in read(args.results) if r["representation"] == args.representation]
    loss_rows = [r for r in read(args.loss) if r["representation"] == args.representation]
    out = lambda name: os.path.join(args.figures_dir, f"{name}{args.suffix}.png")
    plot_by_share(rows, "precision", "Precision", out("figure6a_main_precision_by_share"))
    plot_by_share(rows, "f1", "F1-score", out("figure6b_main_f1_by_share"))
    plot_scenarios(rows, "precision", "Precision", out("figure7a_main_precision_scenarios"))
    plot_scenarios(rows, "f1", "F1-score", out("figure7b_main_f1_scenarios"))
    plot_cost(rows, out("figure9_main_cost"))
    plot_loss(loss_rows, out("extra_main_loss"))


if __name__ == "__main__":
    main()
