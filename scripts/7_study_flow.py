"""Figure 1 of the paper: the study at a glance, with the counts taken from the data files.

Draws figures/figure1_study_flow.png from switch_statements.csv (the methods
of Fontana's dataset that have a body), token_counts.csv (their token counts)
and datasets/dataset_seed0.csv (one Monte Carlo split; every seed has the same
counts): dataset and filter, Monte Carlo split into 3 ablation seeds and 10
main seeds, the model-size ablation that chose 4 layers, balancing and training
of the training split at the three smell proportions, and the evaluation on
the test split (original code), with training and inference energy measured
besides the detection metrics, as the title of the paper promises.

Usage:
    pip install matplotlib
    python scripts/7_study_flow.py
"""
import argparse
import csv
import os

INK = "#0b0b0b"
MUTED = "#52514e"
GRID = "#e4e3df"
FILL = "#f6f5f2"
# Fontana's Switch Statements file lists 420 methods; the data files of this repository start
# from the 404 that have a method body (the 16 without one are all clean)
FONTANA_METHODS = 420
METHODS = [("Class weighting", "#2a78d6"), ("Augmentation", "#eb6834"), ("SMOTE", "#1baf7a")]
# Light tints of the method colors, from the editor's cell palette, for the method boxes
TINTS = {"Class weighting": "#e7f1fb", "Augmentation": "#f9ece7", "SMOTE": "#e5f4e4"}


def read(path):
    with open(path, newline="", encoding="utf-8") as f:
        return list(csv.DictReader(f))


def counts(source_path, tokens_path, dataset_path, fit_col, exclude):
    tokens = read(tokens_path)
    with_body = len(read(source_path))
    fit = [r for r in tokens if r[fit_col] == "1"]
    kept = [r for r in fit if r["project"] not in exclude]
    rows = read(dataset_path)
    originals = [r for r in rows if r["is_augmented"] == "0"]
    test = [r for r in originals if r["split"] == "test"]
    train = [r for r in rows if r["split"] == "train"]
    shares = {}
    for pct in sorted({float(r["smell_pct"]) for r in train}):
        used = [r for r in train if float(r["smell_pct"]) <= pct]
        shares[pct] = (sum(r["label"] == "0" for r in used), sum(r["label"] == "1" for r in used))
    return {
        "source": FONTANA_METHODS, "excluded": ", ".join(exclude), "with_body": with_body, "fit": len(fit), "kept": len(kept),
        "kept_clean": sum(r["label"] == "0" for r in kept), "kept_smell": sum(r["label"] == "1" for r in kept),
        "projects": len({r["project"] for r in kept}),
        "train": len([r for r in originals if r["split"] == "train"]),
        "train_smell": sum(r["label"] == "1" for r in originals if r["split"] == "train"),
        "test": len(test), "test_smell": sum(r["label"] == "1" for r in test),
        "shares": shares,
    }


def draw(c, out):
    """Layout of the study (the paper's Figure 1): dataset and filter -> Monte Carlo split, whose
    3 ablation seeds go to the model-size ablation and whose 10 main seeds send the training
    split to balancing and training (fed by the 4 layers the ablation chose) and the test split,
    original code, straight to the evaluation. The evaluation lists the detection metrics and
    the measured cost: training and inference energy (CodeCarbon) besides time, as in the title."""
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    from matplotlib.patches import FancyBboxPatch

    # Serif to match the body text of the paper; the first family found is used
    plt.rcParams.update({"font.family": "serif",
                         "font.serif": ["Times New Roman", "Liberation Serif", "Nimbus Roman", "DejaVu Serif"]})
    fig = plt.figure(figsize=(9.4, 3.7))
    ax = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 100)
    ax.set_ylim(0, 39)
    ax.axis("off")
    edge = "#8a8984"

    def frame(x, y, w, h, fill=FILL, color=edge, width=1.2):
        ax.add_patch(FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0,rounding_size=1.4",
                                    facecolor=fill, edgecolor=color, linewidth=width))

    def label(x, y, s, size=8.5, bold=False, color=INK):
        ax.text(x, y, s, ha="center", va="center", fontsize=size, color=color,
                fontweight="bold" if bold else "normal", linespacing=1.25)

    def arrow(points, text=None, at=None, align="left"):
        xs, ys = zip(*points)
        ax.plot(xs[:-1] + (xs[-1],), ys, color=INK, linewidth=1.6, solid_capstyle="butt")
        (x0, y0), (x1, y1) = points[-2], points[-1]
        ax.annotate("", xy=(x1, y1), xytext=(x0, y0),
                    arrowprops=dict(arrowstyle="-|>", color=INK, linewidth=1.6, shrinkA=0, shrinkB=0,
                                    mutation_scale=12))
        if text:
            ax.text(*at, text, ha=align, va="center", fontsize=8, color=INK, linespacing=1.2)

    # Dataset and filter
    frame(1, 26, 26, 12)
    label(14, 34.6, "Dataset and filter", 11, True)
    label(14, 30.0, f"Fontana et al., {c['source']} methods\n"
                    f"{c['kept']} kept ({c['kept_clean']} clean + {c['kept_smell']} smell)")

    # Monte Carlo split with its two seed groups
    frame(33, 23, 36, 15.5)
    label(51, 36.2, "Monte Carlo split", 11, True)
    label(51, 32.9, f"Stratified {c['train']} train + {c['test']} test")
    frame(35, 24.3, 15, 6.4, fill="#fbf1d9", color="#c9a227")
    label(42.5, 27.5, "3 seeds (42\u201344)", 9)
    frame(52, 24.3, 15, 6.4, fill="#ece6f5", color="#7d5ba6")
    label(59.5, 27.5, "10 seeds (0\u20139)", 9)

    # Model-size ablation
    frame(1, 9.5, 26, 10.5)
    label(14, 17.3, "Model-size ablation", 11, True)
    label(14, 13.0, "UniXcoder, 0\u201312 encoder layers\nscores, time and energy\n4 layers chosen", 8.3)

    # Balancing and training, with the three methods
    frame(29, 0.8, 40, 19.5)
    label(49, 18.0, "Balancing and training", 11, True)
    shares = sorted(c["shares"])
    label(49, 13.9, "UniXcoder, 4 layers\nSmell proportion of the training split:", 8.5)
    ax.text(49, 10.9, f"{shares[0]:g}% (no balancing) vs {shares[1]:g}% (partial) vs {shares[2]:g}% (balanced)",
            ha="center", va="center", fontsize=8.5, color=INK, fontweight="bold")
    details = {"Class weighting": "loss weights", "Augmentation": "14 refactoring\noperators",
               "SMOTE": "on embeddings"}
    for i, (name, color) in enumerate(METHODS):
        x = 30.5 + i * 12.6
        frame(x, 1.8, 11.8, 7.4, fill=TINTS[name], color=color, width=1.4)
        ax.text(x + 5.9, 7.3, name, ha="center", va="center", fontsize=8.6, fontweight="bold", color=INK)
        label(x + 5.9, 4.4, details[name], 7.8)

    # Evaluation on the test split, with the measured cost
    frame(73, 2.5, 26, 13.5)
    label(86, 13.6, "Evaluation on test split", 11, True)
    label(86, 8.2, "Accuracy, precision, recall,\nF1-score, MCC\ntraining time and energy,\n"
                   "inference energy (CodeCarbon)", 8.3)

    # Flow
    arrow([(27, 32), (33, 32)])
    arrow([(42.5, 24.3), (42.5, 22.3), (14, 22.3), (14, 20)])
    arrow([(59.5, 24.3), (59.5, 20.3)], "training\nsplit", (60.4, 22.2))
    arrow([(67, 27.5), (93, 27.5), (93, 16)], "test split\n(original code)", (92, 21.5), align="right")
    arrow([(14, 9.5), (14, 5.5), (29, 5.5)])
    arrow([(69, 8.5), (73, 8.5)], "model", (69.4, 10.2))

    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    fig.savefig(out, dpi=300)
    plt.close(fig)
    print(f"-> {out}")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--source", default="switch_statements.csv")
    parser.add_argument("--tokens", default="token_counts.csv")
    parser.add_argument("--dataset", default="datasets/dataset_seed0.csv")
    parser.add_argument("--fit-col", default="fits_with_comments")
    parser.add_argument("--exclude-projects", nargs="*", default=["trove-2.1.0"])
    parser.add_argument("--out", default="figures/figure1_study_flow.png")
    args = parser.parse_args()
    draw(counts(args.source, args.tokens, args.dataset, args.fit_col, args.exclude_projects), args.out)


if __name__ == "__main__":
    main()
