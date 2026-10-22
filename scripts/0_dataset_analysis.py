"""Count UniXcoder tokens per method and describe the filtered dataset.

Step 1 tokenizes every method in switch_statements.csv with and without
comments and writes token_counts.csv, so the 1,024-token filter can be applied
to either input representation. UniXcoder wraps the code as
<s> <encoder-only> </s> code </s>, so the code budget is max_len - 4.

Step 2 keeps the samples that fit UniXcoder's input and writes:
  - dataset_stats.csv: per-project counts, smell share and token sizes
  - figures/dataset_distribution.png: class balance per project and token
    length per class

Usage:
    pip install transformers matplotlib
    python scripts/0_dataset_analysis.py                  # both steps
    python scripts/0_dataset_analysis.py --skip-tokens    # step 2 from the existing token_counts.csv
    python scripts/0_dataset_analysis.py --fit-col fits_no_comments
"""
import argparse
import csv
import os
import random
import re
import statistics
from collections import Counter, defaultdict

SPECIAL_TOKENS = 4  # <s>, <encoder-only>, </s> before the code, </s> after it

CLEAN_COLOR = "#2a78d6"
SMELL_COLOR = "#eb6834"
INK = "#0b0b0b"
MUTED = "#52514e"
GRID = "#e4e3df"


def strip_java_comments(code):
    """Remove // and /* */ comments, leaving string and char literals intact."""
    out = []
    i, n = 0, len(code)
    while i < n:
        c = code[i]
        nxt = code[i + 1] if i + 1 < n else ""
        if c == "/" and nxt == "/":
            while i < n and code[i] != "\n":
                i += 1
        elif c == "/" and nxt == "*":
            end = code.find("*/", i + 2)
            i = n if end == -1 else end + 2
            out.append(" ")
        elif c in ('"', "'"):
            quote = c
            out.append(c)
            i += 1
            while i < n and code[i] != quote and code[i] != "\n":
                if code[i] == "\\" and i + 1 < n:
                    out.append(code[i])
                    i += 1
                out.append(code[i])
                i += 1
            if i < n:
                out.append(code[i])
                i += 1
        else:
            out.append(c)
            i += 1
    return "".join(out)


def normalize_whitespace(code):
    return " ".join(code.split())


def count_tokens(rows, code_dir, model, budget, keep_whitespace):
    from transformers import AutoTokenizer
    tokenizer = AutoTokenizer.from_pretrained(model)
    prep = (lambda s: s) if keep_whitespace else normalize_whitespace

    results = []
    for row in rows:
        with open(os.path.join(code_dir, row["filename"]), encoding="utf-8", errors="replace") as f:
            code = f.read()
        tok_comments = len(tokenizer.tokenize(prep(code)))
        tok_no_comments = len(tokenizer.tokenize(prep(strip_java_comments(code))))
        results.append({
            # IDMethod is unique only within a project, so the key includes it
            "sample_id": f"{row['project']}/{row['id']}",
            "project": row["project"],
            "filename": row["filename"],
            "label": row["label"],
            "length": row["length"],
            "tok_with_comments": tok_comments,
            "tok_no_comments": tok_no_comments,
            "fits_with_comments": int(tok_comments <= budget),
            "fits_no_comments": int(tok_no_comments <= budget),
        })
    return results


def report_tokens(results, budget, max_len):
    print(f"{len(results)} samples, code budget {budget} tokens")
    for col in ("fits_with_comments", "fits_no_comments"):
        kept = Counter(r["label"] for r in results if r[col])
        print(f"{col}: {sum(kept.values())} kept (label 0: {kept['0']}, label 1: {kept['1']})")
    old = Counter(r["label"] for r in results if int(r["length"]) < max_len)
    print(f"existing length < {max_len}: {sum(old.values())} kept (label 0: {old['0']}, label 1: {old['1']})")
    same = sum(int(r["length"]) == r["tok_no_comments"] for r in results)
    print(f"length == tok_no_comments for {same}/{len(results)} samples")


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


def describe(rows, code_dir, fit_col, exclude_projects, budget, out, figure):
    tok_col = "tok_with_comments" if fit_col == "fits_with_comments" else "tok_no_comments"
    rows = [dict(r) for r in rows if str(r[fit_col]) == "1" and r["project"] not in exclude_projects]
    for r in rows:
        r.update(count_constructs(os.path.join(code_dir, r["filename"])))

    by_project = defaultdict(list)
    for r in rows:
        by_project[r["project"]].append(r)
    stats = [summarize(p, by_project[p], tok_col) for p in sorted(by_project)]
    stats.append(summarize("Total", rows, tok_col))

    with open(out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(stats[0]))
        writer.writeheader()
        writer.writerows(stats)

    cols = ["project", "total", "clean", "smell", "smell_pct", "smell_max_tok", "smell_median_tok"]
    print(" | ".join(cols))
    for s in stats:
        print(" | ".join(str(s[c]) for c in cols))

    plot(stats, rows, tok_col, budget, figure)
    print(f"-> {out}, {figure}")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--csv", default="switch_statements.csv")
    parser.add_argument("--code-dir", default="ds_comment/0")
    parser.add_argument("--model", default="microsoft/unixcoder-base")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--keep-whitespace", action="store_true",
                        help="tokenize the source as is instead of collapsing whitespace")
    parser.add_argument("--skip-tokens", action="store_true",
                        help="reuse the existing token counts instead of running the tokenizer")
    parser.add_argument("--tokens", default="token_counts.csv")
    parser.add_argument("--fit-col", default="fits_with_comments")
    parser.add_argument("--exclude-projects", nargs="*", default=[])
    parser.add_argument("--out", default="dataset_stats.csv")
    parser.add_argument("--figure", default="figures/dataset_distribution.png")
    args = parser.parse_args()
    budget = args.max_len - SPECIAL_TOKENS

    if args.skip_tokens:
        with open(args.tokens, newline="", encoding="utf-8") as f:
            results = list(csv.DictReader(f))
    else:
        with open(args.csv, newline="", encoding="utf-8") as f:
            rows = list(csv.DictReader(f))
        results = count_tokens(rows, args.code_dir, args.model, budget, args.keep_whitespace)
        with open(args.tokens, "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=list(results[0]))
            writer.writeheader()
            writer.writerows(results)
        report_tokens(results, budget, args.max_len)
        print(f"-> {args.tokens}")

    describe(results, args.code_dir, args.fit_col, args.exclude_projects, budget, args.out, args.figure)


if __name__ == "__main__":
    main()
