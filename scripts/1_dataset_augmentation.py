"""Build the variant pool: every method x every refactoring operator.

Reads the methods listed in switch_statements.csv from ds_comment/0 and writes
  - ds_nocomment/0/<project>/<file>: the same methods without comments
  - ds_comment/<k>/<project>/<file>: variant made by operator k (1-14)
  - ds_nocomment/<k>/<project>/<file>: that variant without comments
  - augmented_pool.csv: one row per method x operator with its status
  - augmented_pool_summary.csv: status counts per operator for three subsets:
    all methods, and the clean and smelly methods used in the experiment
    (fit UniXcoder's input, excluded projects left out)

Operator numbers follow the toolkit order (a-n in demo_refactor_java.py).
A variant is written only when its status is "ok": the operator changed the
code, the result parses, and both representations fit UniXcoder's input.
Random choices inside an operator are seeded by the sample id and operator,
so the pool is identical on every run and machine. The toolkit in
tools/java_refactor is used as published.

Usage:
    pip install transformers nltk javalang
    pip install --use-pep517 wordninja
    python -m nltk.downloader wordnet
    python scripts/1_dataset_augmentation.py
    python scripts/1_dataset_augmentation.py --summary-only   # summaries from the existing pool
"""
import argparse
import contextlib
import csv
import importlib
import io
import os
import random
import sys
import zlib
from collections import Counter

# The module name starts with a digit, so a plain import statement cannot load it
analysis = importlib.import_module("0_dataset_analysis")
SPECIAL_TOKENS = analysis.SPECIAL_TOKENS
normalize_whitespace = analysis.normalize_whitespace
strip_java_comments = analysis.strip_java_comments

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(REPO, "tools", "java_refactor"))

# Folder k holds the variants of OPERATORS[k - 1]
OPERATORS = [
    "add_argumemts", "add_local_variable", "add_print", "apply_plus_zero_math",
    "enhance_filed", "enhance_for_loop", "rename_api", "rename_argument",
    "rename_local_variable", "rename_method_name", "return_optimal",
    "dead_branch_while", "dead_branch_for", "duplication",
]
STATUSES = ["ok", "unchanged", "syntax", "over_budget", "error", "source_syntax"]


def parses(code):
    import javalang
    try:
        javalang.parse.parse("class D {" + code + "\n}")
        return True
    except Exception:
        return False


def operator_seed(base_seed, sample_id, op_id):
    # str hash() is salted per process, crc32 is stable across runs and machines
    return zlib.crc32(f"{base_seed}/{sample_id}/{op_id}".encode())


def read(path):
    with open(path, encoding="utf-8", newline="") as f:
        return f.read()


def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="") as f:
        f.write(text)


def apply_operator(op, code, seed, count_tokens, budget):
    """Return (status, variant, tokens with comments, tokens without comments)."""
    random.seed(seed)
    try:
        with contextlib.redirect_stdout(io.StringIO()):
            variant = op(code)
    except Exception:
        return "error", None, "", ""
    if normalize_whitespace(variant) == normalize_whitespace(code):
        return "unchanged", None, "", ""
    if not parses(variant):
        return "syntax", None, "", ""
    tok_comments = count_tokens(variant)
    tok_no_comments = count_tokens(strip_java_comments(variant))
    if tok_comments > budget or tok_no_comments > budget:
        return "over_budget", None, tok_comments, tok_no_comments
    return "ok", variant, tok_comments, tok_no_comments


def build_pool(rows, comment_dir, nocomment_dir, operators, base_seed, count_tokens, budget):
    records = []
    for row in rows:
        sample_id = f"{row['project']}/{row['id']}"  # IDMethod repeats across projects
        code = read(os.path.join(comment_dir, "0", row["filename"]))
        write(os.path.join(nocomment_dir, "0", row["filename"]), strip_java_comments(code))
        source_parses = parses(code)
        for op_id, (name, op) in enumerate(operators, start=1):
            status, variant, tok_c, tok_nc = apply_operator(
                op, code, operator_seed(base_seed, sample_id, op_id), count_tokens, budget)
            if status == "syntax" and not source_parses:
                status = "source_syntax"
            if status == "ok":
                write(os.path.join(comment_dir, str(op_id), row["filename"]), variant)
                write(os.path.join(nocomment_dir, str(op_id), row["filename"]), strip_java_comments(variant))
            records.append({
                "sample_id": sample_id,
                "project": row["project"],
                "filename": row["filename"],
                "label": row["label"],
                "op_id": op_id,
                "operator": name,
                "status": status,
                "tok_with_comments": tok_c,
                "tok_no_comments": tok_nc,
            })
    return records


def summarize(subset, rows):
    """Return one row per operator with its status counts, plus a total row."""
    operators = sorted({(int(r["op_id"]), r["operator"]) for r in rows})
    table = []
    for op_id, name in operators:
        counts = Counter(r["status"] for r in rows if int(r["op_id"]) == op_id)
        table.append({"subset": subset, "methods": len({r["sample_id"] for r in rows}), "op_id": op_id,
                      "operator": name, **{s: counts[s] for s in STATUSES}, "total": sum(counts.values())})
    totals = Counter(r["status"] for r in rows)
    table.append({"subset": subset, "methods": len({r["sample_id"] for r in rows}), "op_id": "",
                  "operator": "Total", **{s: totals[s] for s in STATUSES}, "total": len(rows)})
    return table


def save_and_print(tables, out):
    with open(out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(tables[0][0]))
        writer.writeheader()
        for table in tables:
            writer.writerows(table)
    cols = STATUSES + ["total"]
    for table in tables:
        print(f"{table[0]['subset']} ({table[0]['methods']} methods):")
        print(f"{'op':>2} {'operator':22} " + " ".join(f"{c:>13}" for c in cols))
        for t in table:
            print(f"{t['op_id']:>2} {t['operator']:22} " + " ".join(f"{t[c]:>13}" for c in cols))
    print(f"-> {out}")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--csv", default="switch_statements.csv")
    parser.add_argument("--comment-dir", default="ds_comment")
    parser.add_argument("--nocomment-dir", default="ds_nocomment")
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--model", default="microsoft/unixcoder-base")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--out", default="augmented_pool.csv")
    parser.add_argument("--summary", default="augmented_pool_summary.csv")
    parser.add_argument("--tokens", default="token_counts.csv")
    parser.add_argument("--fit-col", default="fits_with_comments")
    parser.add_argument("--exclude-projects", nargs="*", default=["trove-2.1.0"])
    parser.add_argument("--summary-only", action="store_true",
                        help="rebuild the summaries from the existing pool without augmenting")
    args = parser.parse_args()

    if args.summary_only:
        with open(args.out, newline="", encoding="utf-8") as f:
            records = list(csv.DictReader(f))
    else:
        records = augment(args)

    with open(args.tokens, newline="", encoding="utf-8") as f:
        fit = {r["sample_id"] for r in csv.DictReader(f) if r[args.fit_col] == "1"}
    experiment = [r for r in records if r["sample_id"] in fit and r["project"] not in args.exclude_projects]
    save_and_print([
        summarize("all", records),
        summarize("experiment_clean", [r for r in experiment if str(r["label"]) == "0"]),
        summarize("experiment_smell", [r for r in experiment if str(r["label"]) == "1"]),
    ], args.summary)


def augment(args):
    import refactoring_methods
    from transformers import AutoTokenizer
    tokenizer = AutoTokenizer.from_pretrained(args.model)

    def count_tokens(code):
        return len(tokenizer.tokenize(normalize_whitespace(code)))

    operators = [(name, getattr(refactoring_methods, name)) for name in OPERATORS]
    with open(args.csv, newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))

    records = build_pool(rows, args.comment_dir, args.nocomment_dir, operators, args.seed,
                         count_tokens, args.max_len - SPECIAL_TOKENS)

    with open(args.out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(records[0]))
        writer.writeheader()
        writer.writerows(records)

    print(f"{len(rows)} methods x {len(operators)} operators -> {args.out}")
    return records


if __name__ == "__main__":
    main()
