"""Count augmentation outcomes per operator from augmented_pool.csv.

Writes one row per operator with the number of attempts in each status,
plus a total row, so the table can go straight into the paper.

Usage:
    python scripts/pool_summary.py                          # all 404 methods
    python scripts/pool_summary.py --only-fit --label 1     # the 96 smelly methods in the experiment
"""
import argparse
import csv
from collections import Counter

STATUSES = ["ok", "unchanged", "syntax", "over_budget", "error", "source_syntax"]


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--pool", default="augmented_pool.csv")
    parser.add_argument("--tokens", default="token_counts.csv")
    parser.add_argument("--only-fit", action="store_true",
                        help="keep the methods that fit UniXcoder's input, as in the experiment")
    parser.add_argument("--fit-col", default="fits_with_comments")
    parser.add_argument("--exclude-projects", nargs="*", default=["trove-2.1.0"])
    parser.add_argument("--label", choices=["0", "1"], help="keep one class only")
    parser.add_argument("--out", default="augmented_pool_summary.csv")
    args = parser.parse_args()

    with open(args.pool, newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    if args.only_fit:
        with open(args.tokens, newline="", encoding="utf-8") as f:
            fit = {r["sample_id"] for r in csv.DictReader(f) if r[args.fit_col] == "1"}
        rows = [r for r in rows if r["sample_id"] in fit and r["project"] not in args.exclude_projects]
    if args.label:
        rows = [r for r in rows if r["label"] == args.label]

    operators = sorted({(int(r["op_id"]), r["operator"]) for r in rows})
    table = []
    for op_id, name in operators:
        counts = Counter(r["status"] for r in rows if int(r["op_id"]) == op_id)
        table.append({"op_id": op_id, "operator": name, **{s: counts[s] for s in STATUSES},
                      "total": sum(counts.values())})
    totals = Counter(r["status"] for r in rows)
    table.append({"op_id": "", "operator": "Total", **{s: totals[s] for s in STATUSES}, "total": len(rows)})

    with open(args.out, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(table[0]))
        writer.writeheader()
        writer.writerows(table)

    methods = len({r["sample_id"] for r in rows})
    print(f"{methods} methods x {len(operators)} operators -> {args.out}")
    print(f"{'op':>2} {'operator':22} " + " ".join(f"{s:>13}" for s in STATUSES + ["total"]))
    for t in table:
        print(f"{t['op_id']:>2} {t['operator']:22} " + " ".join(f"{t[s]:>13}" for s in STATUSES + ["total"]))


if __name__ == "__main__":
    main()
