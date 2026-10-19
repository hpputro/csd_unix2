"""Count UniXcoder tokens for each method in switch_statements.csv.

For every sample this reports the token count with comments and without
comments, so the 1,024-token filter can be applied to either input
representation. UniXcoder wraps the code as
<s> <encoder-only> </s> code </s>, so the code budget is max_len - 4.

Usage:
    pip install transformers
    python scripts/count_tokens.py                      # downloads microsoft/unixcoder-base
    python scripts/count_tokens.py --model /path/to/unixcoder-base --out token_counts.csv
"""
import argparse
import csv
import os
from collections import Counter

SPECIAL_TOKENS = 4  # <s>, <encoder-only>, </s> before the code, </s> after it


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


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--csv", default="switch_statements.csv")
    parser.add_argument("--code-dir", default="1_rawmethod")
    parser.add_argument("--model", default="microsoft/unixcoder-base")
    parser.add_argument("--max-len", type=int, default=1024)
    parser.add_argument("--keep-whitespace", action="store_true",
                        help="tokenize the source as is instead of collapsing whitespace")
    parser.add_argument("--out", default="token_counts.csv")
    args = parser.parse_args()

    from transformers import AutoTokenizer
    tokenizer = AutoTokenizer.from_pretrained(args.model)
    budget = args.max_len - SPECIAL_TOKENS
    prep = (lambda s: s) if args.keep_whitespace else normalize_whitespace

    with open(args.csv, newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))

    results = []
    for row in rows:
        with open(os.path.join(args.code_dir, row["filename"]), encoding="utf-8", errors="replace") as f:
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

    with open(args.out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(results[0]))
        writer.writeheader()
        writer.writerows(results)

    print(f"{len(results)} samples, code budget {budget} tokens -> {args.out}")
    for col in ("fits_with_comments", "fits_no_comments"):
        kept = Counter(r["label"] for r in results if r[col])
        print(f"{col}: {sum(kept.values())} kept (label 0: {kept['0']}, label 1: {kept['1']})")
    old = Counter(r["label"] for r in results if int(r["length"]) < args.max_len)
    print(f"existing length < {args.max_len}: {sum(old.values())} kept (label 0: {old['0']}, label 1: {old['1']})")
    same = sum(int(r["length"]) == r["tok_no_comments"] for r in results)
    print(f"length == tok_no_comments for {same}/{len(results)} samples")


if __name__ == "__main__":
    main()
