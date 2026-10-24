"""Figure 1 of the paper: the study in five steps, with the counts taken from the data files.

Draws figures/figure1_study_flow.png from switch_statements.csv (the methods
of Fontana's dataset that have a body), token_counts.csv (methods with a body and their token counts) and
datasets/dataset_seed0.csv (one Monte Carlo
split; every seed has the same counts):

  1 Dataset      Fontana's Switch Statements methods and the filters down to the 350 kept
  2 Split        Monte Carlo cross-validation, train and test sizes
  3 Input        code with comments (the RQ3 ablation removes them), UniXcoder format
  4 Balancing    the smelly share of the train side and the methods applied at each share,
                 with the train set size; this replaces a table of the scenarios
  5 Model        UniXcoder kept to 4 encoder layers, and the reported metrics

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
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    from matplotlib.patches import FancyBboxPatch

    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})
    fig = plt.figure(figsize=(7.2, 4.9))
    ax = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 100)
    ax.set_ylim(0, 68)
    ax.axis("off")

    def box(x, y, w, h, number, title, lines):
        ax.add_patch(FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0,rounding_size=1.2",
                                    facecolor=FILL, edgecolor=GRID, linewidth=1))
        ax.text(x + 2, y + h - 3, f"{number}  {title}", fontsize=9, fontweight="bold", color=INK, va="top")
        for i, line in enumerate(lines):
            ax.text(x + 2, y + h - 8.2 - i * 3.7, line, fontsize=7.6, color=MUTED, va="top")

    def arrow(x0, y0, x1, y1):
        ax.annotate("", xy=(x1, y1), xytext=(x0, y0),
                    arrowprops=dict(arrowstyle="-|>", color=MUTED, linewidth=1.2, shrinkA=0, shrinkB=0))

    # Top row: dataset, split, input
    top, h = 37, 30
    box(1, top, 31, h, 1, "Dataset", [
        f"{c['source']} Java methods, Fontana et al.",
        f"{c['with_body']} with a method body",
        f"{c['fit']} within 1,020 tokens",
        f"{c['kept']} without {c['excluded']}:",
        f"{c['kept_clean']} clean, {c['kept_smell']} smelly",
    ])
    box(35, top, 30, h, 2, "Split", [
        "Monte Carlo, seeds 0-9",
        "stratified by label, project",
        "and length",
        f"train {c['train']} ({c['train_smell']} smelly)",
        f"test {c['test']} ({c['test_smell']} smelly), original code",
    ])
    box(68, top, 31, h, 3, "Input", [
        "code with comments",
        "(RQ3: without comments)",
        "whitespace collapsed,",
        "UniXcoder encoder-only format",
    ])
    arrow(32, top + h / 2, 35, top + h / 2)
    arrow(65, top + h / 2, 68, top + h / 2)

    # Bottom row: balancing (train side only), model and evaluation
    bottom, hb = 1, 32
    ax.add_patch(FancyBboxPatch((1, bottom), 64, hb, boxstyle="round,pad=0,rounding_size=1.2",
                                facecolor=FILL, edgecolor=GRID, linewidth=1))
    ax.text(3, bottom + hb - 3, "4  Balancing (train side only)", fontsize=9, fontweight="bold", color=INK,
            va="top")
    shares = sorted(c["shares"].items())
    for i, (pct, (clean, smell)) in enumerate(shares):
        x = 3 + i * 21
        ax.text(x, bottom + hb - 9, f"{pct:g}% smelly", fontsize=8.5, fontweight="bold", color=INK, va="top")
        ax.text(x, bottom + hb - 13.5, f"{clean} clean + {smell} smelly", fontsize=7.6, color=MUTED, va="top")
        if i == 0:
            ax.text(x, bottom + hb - 18.5, "no balancing", fontsize=7.6, color=INK, va="top")
        else:
            for j, (name, color) in enumerate(METHODS):
                yy = bottom + hb - 18.5 - j * 4
                ax.add_patch(plt.Rectangle((x, yy - 2.3), 1.8, 2.0, color=color))
                ax.text(x + 2.8, yy, name, fontsize=7.6, color=INK, va="top")
    # Under the no-balancing column, the only one with room below its text
    ax.text(3, bottom + 5.6, "test side keeps its", fontsize=7.2, color=MUTED)
    ax.text(3, bottom + 2.2, "natural 27.1% smelly", fontsize=7.2, color=MUTED)
    box(68, bottom, 31, hb, 5, "Model and evaluation", [
        "UniXcoder, bottom 4 of 12",
        "encoder layers, [CLS] head",
        "20 epochs, last epoch scored",
        "precision (main), recall, F1,",
        "MCC of the smelly class",
        "training time per run",
    ])
    arrow(83.5, top, 33, bottom + hb)
    arrow(65, bottom + hb / 2, 68, bottom + hb / 2)

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
