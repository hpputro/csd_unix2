"""Describe the filtered dataset per project and draw its distribution.

Reads token_counts.csv (from count_tokens.py), keeps the samples that fit
UniXcoder's input, and writes:
  - dataset_stats.csv: per-project counts, smell share and token sizes
  - figures/dataset_distribution.png: class balance per project and token
    length per class

Usage:
    pip install matplotlib
    python scripts/dataset_stats.py                       # filter on fits_with_comments
    python scripts/dataset_stats.py --fit-col fits_no_comments
"""
import argparse
import csv
import os
import random
import re
import statistics
from collections import defaultdict

from count_tokens import strip_java_comments

CLEAN_COLOR = "#2a78d6"
SMELL_COLOR = "#eb6834"
INK = "#0b0b0b"
MUTED = "#52514e"
GRID = "#e4e3df"


def count_constructs(path):
    with open(path, encoding="utf-8", errors="replace") as f:
        code = strip_java_comments(f.read())
    return {
        "switch": len(re.findall(r"\bswitch\s*\(", code)),
        "else_if": len(re.findall(r"\belse\s+if\b", code)),
        "instanceof": len(re.findall(r"\binstanceof\b", code)),
    }


def summarize(name, rows, tok_col):
    smell = [r for r in rows if r["label"] == "1"]
    clean = [r for r in rows if r["label"] == "0"]
    smell_tok = [int(r[tok_col]) for r in smell]
    clean_tok = [int(r[tok_col]) for r in clean]
    return {
        "project": name,
        "total": len(rows),
        "clean": len(clean),
        "smell": len(smell),
        "smell_pct": round(100 * len(smell) / len(rows), 1) if rows else 0,
        "smell_max_tok": max(smell_tok) if smell_tok else "",
        "smell_median_tok": statistics.median(smell_tok) if smell_tok else "",
        "clean_max_tok": max(clean_tok) if clean_tok else "",
        "clean_median_tok": statistics.median(clean_tok) if clean_tok else "",
        "smell_with_switch": sum(r["switch"] > 0 for r in smell),
        "smell_with_else_if": sum(r["else_if"] > 0 for r in smell),
    }


def plot(stats, rows, tok_col, budget, out):
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    # DejaVu Sans ships with matplotlib, so the figure is the same on every machine
    plt.rcParams.update({"font.family": "sans-serif", "font.sans-serif": ["DejaVu Sans"]})

    projects = sorted(stats[:-1], key=lambda s: s["total"])
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(11, 4.6), gridspec_kw={"width_ratios": [1.25, 1]})

    # (a) class balance per project, stacked because clean + smell = project total
    names = [p["project"] for p in projects]
    clean = [p["clean"] for p in projects]
    smell = [p["smell"] for p in projects]
    ax1.barh(names, clean, color=CLEAN_COLOR, height=0.62, label="Clean", edgecolor="white", linewidth=1.5)
    ax1.barh(names, smell, left=clean, color=SMELL_COLOR, height=0.62, label="Smell", edgecolor="white", linewidth=1.5)
    for i, p in enumerate(projects):
        ax1.text(p["total"] + 1, i, f"{p['smell_pct']:.0f}% smell", va="center", fontsize=8.5, color=MUTED)
    ax1.set_xlim(0, max(p["total"] for p in projects) * 1.3)
    ax1.set_xlabel("Methods", color=MUTED)
    total = stats[-1]
    ax1.set_title(f"(a) Class balance per project (n = {total['total']}, {total['smell_pct']:.1f}% smell)",
                  loc="left", fontsize=10.5, color=INK)
    ax1.legend(frameon=False, loc="lower right", fontsize=9)

    # (b) token length per class, every method as one dot
    rng = random.Random(0)
    for y, (label, color, name) in enumerate([("0", CLEAN_COLOR, "Clean"), ("1", SMELL_COLOR, "Smell")]):
        toks = [int(r[tok_col]) for r in rows if r["label"] == label]
        ax2.scatter(toks, [y + rng.uniform(-0.22, 0.22) for _ in toks], s=12, color=color, alpha=0.7,
                    edgecolors="white", linewidths=0.4)
        med = statistics.median(toks)
        ax2.plot([med, med], [y - 0.32, y + 0.32], color=INK, linewidth=2)
        ax2.text(med, y + 0.38, f"median {med:.0f}", ha="center", fontsize=8.5, color=INK)
        ax2.text(max(toks), y - 0.42, f"max {max(toks)}", ha="right", fontsize=8.5, color=MUTED)
    ax2.axvline(budget, color=MUTED, linestyle="--", linewidth=1)
    ax2.text(budget, 1.62, f"limit {budget}", ha="right", fontsize=8.5, color=MUTED)
    ax2.set_yticks([0, 1], ["Clean", "Smell"])
    ax2.set_ylim(-0.6, 1.75)
    ax2.set_xlim(0, budget * 1.04)
    ax2.set_xlabel("UniXcoder tokens per method", color=MUTED)
    ax2.set_title("(b) Method length by class", loc="left", fontsize=10.5, color=INK)

    for ax in (ax1, ax2):
        ax.spines[["top", "right"]].set_visible(False)
        ax.spines[["left", "bottom"]].set_color(GRID)
        ax.tick_params(colors=MUTED, labelsize=9)
        ax.grid(axis="x", color=GRID, linewidth=0.8)
        ax.set_axisbelow(True)

    fig.tight_layout()
    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)
    fig.savefig(out, dpi=300)


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--tokens", default="token_counts.csv")
    parser.add_argument("--code-dir", default="1_rawmethod")
    parser.add_argument("--fit-col", default="fits_with_comments")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--out", default="dataset_stats.csv")
    parser.add_argument("--figure", default="figures/dataset_distribution.png")
    args = parser.parse_args()
    tok_col = "tok_with_comments" if args.fit_col == "fits_with_comments" else "tok_no_comments"

    with open(args.tokens, newline="", encoding="utf-8") as f:
        rows = [r for r in csv.DictReader(f) if r[args.fit_col] == "1"]
    for r in rows:
        r.update(count_constructs(os.path.join(args.code_dir, r["filename"])))

    by_project = defaultdict(list)
    for r in rows:
        by_project[r["project"]].append(r)
    stats = [summarize(p, by_project[p], tok_col) for p in sorted(by_project)]
    stats.append(summarize("Total", rows, tok_col))

    with open(args.out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(stats[0]))
        writer.writeheader()
        writer.writerows(stats)

    cols = ["project", "total", "clean", "smell", "smell_pct", "smell_max_tok", "smell_median_tok"]
    print(" | ".join(cols))
    for s in stats:
        print(" | ".join(str(s[c]) for c in cols))

    plot(stats, rows, tok_col, args.max_len - 4, args.figure)
    print(f"-> {args.out}, {args.figure}")


if __name__ == "__main__":
    main()
