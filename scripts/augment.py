"""Augment the smelly methods with the Java refactoring toolkit.

Each smelly sample gets `--copies` variants (1 = smell x2). Operators are
assigned in round-robin order over the samples sorted by sample_id; when an
operator leaves the code unchanged, breaks the syntax or pushes it past the
UniXcoder budget, the next operator is tried. Random choices inside each
operator are seeded by the sample id, so every run produces the same
variants and a sample keeps the same variant in every fold.

The toolkit in tools/java_refactor is used as published; invalid outputs are
discarded rather than patched.

Usage:
    pip install transformers nltk javalang
    pip install --use-pep517 wordninja
    python -m nltk.downloader wordnet
    python scripts/augment.py                      # smell x2 -> augmented/, augmented.csv
    python scripts/augment.py --copies 2           # smell x3
"""
import argparse
import contextlib
import csv
import io
import os
import random
import sys
import zlib
from collections import Counter

from count_tokens import SPECIAL_TOKENS, normalize_whitespace, strip_java_comments

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(REPO, "tools", "java_refactor"))

# Toolkit order; enhance_for_loop only handles for-each over a List and never
# applies to this dataset, so it is left out of the rotation by default.
OPERATORS = [
    "add_argumemts", "add_local_variable", "add_print", "apply_plus_zero_math",
    "enhance_filed", "enhance_for_loop", "rename_api", "rename_argument",
    "rename_local_variable", "rename_method_name", "return_optimal",
    "dead_branch_while", "dead_branch_for", "duplication",
]


def parses(code):
    import javalang
    try:
        javalang.parse.parse("class D {" + code + "\n}")
        return True
    except Exception:
        return False


def sample_seed(base_seed, sample_id, copy):
    # str hash() is salted per process, crc32 is stable across runs and machines
    return zlib.crc32(f"{base_seed}/{sample_id}/{copy}".encode())


def augment_sample(code, ops, start, seed, count_tokens, budget, seen):
    """Try operators from `start` in rotation; return (variant, operator, attempts) or None."""
    for attempt in range(len(ops)):
        name, op = ops[(start + attempt) % len(ops)]
        random.seed(seed)
        try:
            with contextlib.redirect_stdout(io.StringIO()):
                variant = op(code)
        except Exception:
            continue
        flat = normalize_whitespace(variant)
        if flat in seen or not parses(variant) or count_tokens(variant) > budget:
            continue
        return variant, name, attempt + 1
    return None


def run(rows, code_dir, out_dir, ops, copies, base_seed, count_tokens, budget):
    smelly = sorted((r for r in rows if r["label"] == "1"), key=lambda r: r["sample_id"])
    records = []
    for i, row in enumerate(smelly):
        with open(os.path.join(code_dir, row["filename"]), encoding="utf-8", errors="replace") as f:
            code = f.read()
        seen = {normalize_whitespace(code)}
        for copy in range(1, copies + 1):
            start = (i * copies + copy - 1) % len(ops)
            result = augment_sample(code, ops, start, sample_seed(base_seed, row["sample_id"], copy),
                                    count_tokens, budget, seen)
            if result is None:
                print(f"[no valid variant] {row['sample_id']} copy {copy}")
                continue
            variant, name, attempts = result
            seen.add(normalize_whitespace(variant))
            aug_file = row["filename"][:-len(".java")] + f"_aug{copy}.java"
            path = os.path.join(out_dir, aug_file)
            os.makedirs(os.path.dirname(path), exist_ok=True)
            with open(path, "w", encoding="utf-8") as f:
                f.write(variant)
            records.append({
                "sample_id": row["sample_id"],  # the source sample; variants share its group
                "copy": copy,
                "project": row["project"],
                "label": row["label"],
                "source_filename": row["filename"],
                "aug_filename": aug_file,
                "operator": name,
                "attempts": attempts,
                "tok_with_comments": count_tokens(variant),
                "tok_no_comments": count_tokens(strip_java_comments(variant)),
            })
    return smelly, records


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--tokens", default="token_counts.csv")
    parser.add_argument("--code-dir", default="1_rawmethod")
    parser.add_argument("--fit-col", default="fits_with_comments")
    parser.add_argument("--exclude-projects", nargs="*", default=["trove-2.1.0"])
    parser.add_argument("--skip-operators", nargs="*", default=["enhance_for_loop"])
    parser.add_argument("--copies", type=int, default=1, help="variants per smelly sample (1 = smell x2)")
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--model", default="microsoft/unixcoder-base")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--out-dir", default="augmented")
    parser.add_argument("--out", default="augmented.csv")
    args = parser.parse_args()

    import refactoring_methods
    from transformers import AutoTokenizer
    tokenizer = AutoTokenizer.from_pretrained(args.model)

    def count_tokens(code):
        return len(tokenizer.tokenize(normalize_whitespace(code)))

    ops = [(n, getattr(refactoring_methods, n)) for n in OPERATORS if n not in args.skip_operators]
    with open(args.tokens, newline="", encoding="utf-8") as f:
        rows = [r for r in csv.DictReader(f)
                if r[args.fit_col] == "1" and r["project"] not in args.exclude_projects]

    smelly, records = run(rows, args.code_dir, args.out_dir, ops, args.copies, args.seed,
                          count_tokens, args.max_len - SPECIAL_TOKENS)

    with open(args.out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(records[0]))
        writer.writeheader()
        writer.writerows(records)

    clean = sum(r["label"] == "0" for r in rows)
    print(f"{len(smelly)} smelly samples -> {len(records)} variants in {args.out_dir}/, {args.out}")
    print(f"smell {len(smelly)} -> {len(smelly) + len(records)}, clean {clean}, "
          f"ratio {(len(smelly) + len(records)) / clean:.3f}")
    print("operator use:")
    for name, n in Counter(r["operator"] for r in records).most_common():
        print(f"  {name:22} {n}")


if __name__ == "__main__":
    main()
