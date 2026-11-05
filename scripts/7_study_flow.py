"""Figure 1 of the paper: the study at a glance, with the counts taken from the data files.

Draws figures/figure1_study_flow.png from switch_statements.csv (the methods
of Fontana's dataset that have a body), token_counts.csv (their token counts)
and datasets/dataset_seed0.csv (one Monte Carlo split; every seed has the same
counts). Top row: dataset and filter, Monte Carlo split, input representation.
The training split then goes through balancing (no balancing at the natural
27.5% smelly; class weighting, augmentation and SMOTE at 37.7% and 50%, with
the train set size of each share, which replaces a table of the scenarios)
into UniXcoder kept to 4 encoder layers; the test split goes to the model as
original code (dashed line). The model-size ablation (seeds 42-44) feeds the
choice of 4 layers. Energy is not listed for the model: only the ablation
measured it.

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
    """Three steps on top lead into the balancing of the training split (solid lines) and,
    for the test split, straight to the model (dashed: original code, never balanced); the
    model-size ablation feeds the choice of four encoder layers."""
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    from matplotlib.patches import FancyBboxPatch

    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})
    fig = plt.figure(figsize=(7.2, 5.0))
    ax = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 100)
    ax.set_ylim(0, 70)
    ax.axis("off")

    def box(x, y, w, h, title, lines, fill=FILL, edge=GRID, size=9):
        ax.add_patch(FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0,rounding_size=1.2",
                                    facecolor=fill, edgecolor=edge, linewidth=1))
        top = y + h / 2 + 1.9 * len(lines)
        ax.text(x + w / 2, top, title, ha="center", va="center", fontsize=size, fontweight="bold", color=INK)
        for i, line in enumerate(lines, start=1):
            ax.text(x + w / 2, top - 3.9 * i, line, ha="center", va="center", fontsize=7.5, color=MUTED)

    def line(points, dashed=False, head=True):
        xs, ys = zip(*points)
        ax.plot(xs[:-1] + (xs[-1],), ys, color=MUTED, linewidth=1.1, linestyle=(0, (4, 3)) if dashed else "-")
        if head:
            # Only the last stretch carries the arrowhead, so a dashed line stays dashed
            (x0, y0), (x1, y1) = points[-2], points[-1]
            length = ((x1 - x0) ** 2 + (y1 - y0) ** 2) ** 0.5
            ax.annotate("", xy=(x1, y1), xytext=(x1 - (x1 - x0) * 1.5 / length, y1 - (y1 - y0) * 1.5 / length),
                        arrowprops=dict(arrowstyle="-|>", color=MUTED, linewidth=1.1, shrinkA=0, shrinkB=0))

    # Top row: data, split, input
    top, h = 55, 13
    box(2, top, 30, h, "Dataset and filter", [
        f"Fontana et al., {c['source']} methods",
        f"{c['kept']} kept ({c['kept_clean']} clean + {c['kept_smell']} smell)",
    ])
    box(35, top, 30, h, "Monte Carlo split", [
        "10 seeds, stratified",
        f"{c['train']} train + {c['test']} test",
    ])
    box(68, top, 30, h, "Input representation", [
        "code with comments",
        "without comments (ablation)",
    ])
    line([(32, top + h / 2), (35, top + h / 2)])
    line([(65, top + h / 2), (68, top + h / 2)])

    # Balancing of the training split, with the three methods
    bx, by, bw, bh = 2, 15, 63, 33
    ax.add_patch(FancyBboxPatch((bx, by), bw, bh, boxstyle="round,pad=0,rounding_size=1.2",
                                facecolor=FILL, edgecolor=GRID, linewidth=1))
    shares = sorted(c["shares"].items())
    ax.text(bx + bw / 2, by + bh - 4, "Balancing (training split only)", ha="center", va="center",
            fontsize=9, fontweight="bold", color=INK)
    # One line per smell share of the training split; the first is the unbalanced baseline
    for i, (pct, (clean, smell)) in enumerate(shares):
        note = ", no balancing" if i == 0 else ""
        ax.text(bx + bw / 2, by + bh - 8.3 - 3.3 * i, f"{pct:g}% smell: {clean} clean + {smell} smell{note}",
                ha="center", va="center", fontsize=7.5, color=MUTED)
    # Class weighting reaches the share through its loss weights, not through extra samples
    details = {"Class weighting": ["loss weights", "(effective share)"], "Augmentation": ["14 refactoring", "operators"],
               "SMOTE": ["on embeddings"]}
    for i, (name, color) in enumerate(METHODS):
        x = bx + 2 + i * 20
        box(x, by + 2, 19, 12.5, name, details[name], fill=TINTS[name], edge=color, size=8.5)

    # Model, fed by the balanced training split and by the original test split
    mx, my, mw, mh = 68, 15, 30, 33
    box(mx, my, mw, mh, "UniXcoder, 4 layers", [
        "[CLS] head",
        "precision, recall,",
        "F1-score, MCC",
        "training time",
    ])
    line([(83, top), (83, 50.5), (33.5, 50.5), (33.5, by + bh)])
    line([(93, top), (93, my + mh)], dashed=True)
    line([(bx + bw, by + bh / 2), (mx, by + bh / 2)])

    # Model-size ablation, which chose the four layers
    box(68, 1, 30, 11.5, "Model-size ablation", ["seeds 42\u201344, 0\u201312 layers", "4 layers chosen"], size=8.5)
    line([(83, 12.5), (83, my)])

    # Legend
    ax.plot([2, 7], [6.5, 6.5], color=MUTED, linewidth=1.1)
    ax.text(8.5, 6.5, "training split", va="center", fontsize=7.5, color=MUTED)
    ax.plot([2, 7], [2.5, 2.5], color=MUTED, linewidth=1.1, linestyle=(0, (4, 3)))
    ax.text(8.5, 2.5, "test split (original code, never balanced)", va="center", fontsize=7.5, color=MUTED)

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
