"""Build one Monte Carlo dataset CSV per seed.

Each CSV holds every original method that fits UniXcoder's input (excluded
projects left out) and, per seed:
  - a train/test split (20% test) stratified by label, project and token
    length; identical methods stay on the same side
  - the oversampling selection on the train side: every smelly method and
    25% of the clean ones (selected = 1). SMOTE and the augmentation both
    add one sample per selected method, so the scenarios differ only in how
    the extra sample is made; class weighting uses the same proportions
  - one augmentation variant per selected method, drawn from the operators
    whose variant is valid in augmented_pool.csv
  - the extra smelly samples that raise the train ratio further to 1:1. They
    nest on top of the ones above: each smelly method gets one more distinct
    variant in random order, round after round, until smelly matches clean
    (every smelly method ends with 2 or 3 extra samples); a method with no
    variant left passes its turn to the next one

Column ratio says from which train smelly:clean ratio on a row is used: 0.38
for the original train methods (no balancing), 0.61 for the first extra sample
and 1.0 for the further ones, so the train set at ratio r is every train row
with ratio <= r. Test rows have no ratio. Each extra row stands for one extra
sample of its source method whatever the balancing method: augmentation trains
on the variant's code, SMOTE makes one synthetic point from the source method,
and class weighting counts the rows per class.

The test side holds original code only. A file is found at
ds_comment/<is_augmented>/<filename> or ds_nocomment/<is_augmented>/<filename>.

Usage:
    python scripts/2_dataset_preparation.py                 # seeds 0-9 and 42-44 -> datasets/
    python scripts/2_dataset_preparation.py --seeds 0 1 2
"""
import argparse
import csv
import importlib
import os
import random
import statistics
from collections import Counter, defaultdict

# The module name starts with a digit, so a plain import statement cannot load it
analysis = importlib.import_module("0_dataset_analysis")

COLUMNS = ["seed", "sample_id", "group_id", "project", "filename", "label", "is_augmented",
           "split", "selected", "ratio", "length_bin", "tok_with_comments", "tok_no_comments"]


def load_originals(tokens_path, fit_col, exclude_projects, code_dir):
    with open(tokens_path, newline="", encoding="utf-8") as f:
        rows = [r for r in csv.DictReader(f) if r[fit_col] == "1" and r["project"] not in exclude_projects]

    # Identical methods (copy-pasted classes) form one group so a split never separates them
    by_code = defaultdict(list)
    for r in rows:
        with open(os.path.join(code_dir, r["filename"]), encoding="utf-8") as f:
            by_code[analysis.normalize_whitespace(analysis.strip_java_comments(f.read()))].append(r)
    for members in by_code.values():
        group_id = min(m["sample_id"] for m in members)
        for m in members:
            m["group_id"] = group_id

    # Short/long relative to the median of the method's own class, since smelly methods are far longer
    for label in ("0", "1"):
        median = statistics.median(int(r["tok_with_comments"]) for r in rows if r["label"] == label)
        for r in rows:
            if r["label"] == label:
                r["length_bin"] = "short" if int(r["tok_with_comments"]) <= median else "long"
    return rows


def load_variants(pool_path):
    variants = defaultdict(list)
    with open(pool_path, newline="", encoding="utf-8") as f:
        for r in csv.DictReader(f):
            if r["status"] == "ok":
                variants[r["sample_id"]].append(r)
    return variants


def split_test(rows, test_frac, rng):
    """Pick test groups so every label x project x length stratum is as close to test_frac as possible."""
    groups = defaultdict(list)
    for r in rows:
        groups[r["group_id"]].append(r)
    strata = defaultdict(list)
    for gid, members in groups.items():
        m = members[0]
        strata[(m["label"], m["project"], m["length_bin"])].append(gid)

    # Each label gets exactly round(test_frac * n) test methods. Within a label, floor each
    # stratum's share and hand the leftover slots to strata drawn with probability
    # proportional to their remainder, so over many seeds every stratum, small ones
    # included, averages test_frac
    sizes = {s: sum(len(groups[g]) for g in gids) for s, gids in strata.items()}
    quota = {s: int(test_frac * n) for s, n in sizes.items()}
    remainder = {s: test_frac * sizes[s] - quota[s] for s in strata}
    for label in sorted({s[0] for s in strata}):
        in_label = [s for s in sorted(strata) if s[0] == label]
        target = round(test_frac * sum(sizes[s] for s in in_label))
        candidates = sorted((s for s in in_label if remainder[s] > 0),
                            key=lambda s: rng.random() ** (1 / remainder[s]), reverse=True)
        for s in candidates[:target - sum(quota[s] for s in in_label)]:
            quota[s] += 1

    test = set()
    for s in sorted(strata):
        gids = sorted(strata[s])
        rng.shuffle(gids)
        taken = 0
        for gid in gids:
            if taken + len(groups[gid]) <= quota[s]:
                test.add(gid)
                taken += len(groups[gid])
    return test


def build(seed, originals, variants, test_frac, clean_frac, top_ratio):
    # Separate streams, so changing one step does not reshuffle the others
    split_rng = random.Random(f"{seed}/split")
    select_rng = random.Random(f"{seed}/select")
    variant_rng = random.Random(f"{seed}/variant")
    top_rng = random.Random(f"{seed}/top")

    test_groups = split_test(originals, test_frac, split_rng)
    train = [r for r in originals if r["group_id"] not in test_groups]

    # Every smelly method plus a share of the clean ones; a selected method needs a valid variant
    smelly = [r for r in train if r["label"] == "1" and variants[r["sample_id"]]]
    clean = sorted((r for r in train if r["label"] == "0" and variants[r["sample_id"]]),
                   key=lambda r: r["sample_id"])
    n_clean = round(clean_frac * sum(r["label"] == "0" for r in train))
    selected = {r["sample_id"] for r in smelly} | {r["sample_id"] for r in select_rng.sample(clean, n_clean)}
    first = {}
    for r in sorted(originals, key=lambda r: r["sample_id"]):
        if r["sample_id"] in selected:
            first[r["sample_id"]] = variant_rng.choice(sorted(variants[r["sample_id"]], key=lambda v: int(v["op_id"])))

    # Labels of the three train ratios, e.g. 0.38, 0.61 and 1.0
    n_clean_train = sum(r["label"] == "0" for r in train) + sum(r["label"] == "0" for r in train
                                                                 if r["sample_id"] in selected)
    n_smell_train = sum(r["label"] == "1" for r in train)
    base_ratio = round(n_smell_train / sum(r["label"] == "0" for r in train), 2)
    mid_ratio = round((n_smell_train + len(smelly)) / n_clean_train, 2)

    # Further distinct variants for the smelly methods, one per method per round in a random
    # order, until smelly matches top_ratio x clean; the clean side stays as at mid_ratio
    spare = {}
    for r in sorted(smelly, key=lambda r: r["sample_id"]):
        rest = sorted((v for v in variants[r["sample_id"]] if v["op_id"] != first[r["sample_id"]]["op_id"]),
                      key=lambda v: int(v["op_id"]))
        top_rng.shuffle(rest)
        spare[r["sample_id"]] = rest
    needed = round(top_ratio * n_clean_train) - n_smell_train - len(smelly)
    order = sorted(spare)
    top_rng.shuffle(order)
    extra = defaultdict(list)
    while needed > 0:
        takers = [sid for sid in order if spare[sid]]
        if not takers:
            raise SystemExit(f"seed {seed}: not enough valid variants to reach ratio {top_ratio}")
        for sid in takers[:needed]:
            extra[sid].append(spare[sid].pop())
        needed -= len(takers[:needed])

    out = []
    for r in sorted(originals, key=lambda r: r["sample_id"]):
        base = {"seed": seed, "sample_id": r["sample_id"], "group_id": r["group_id"], "project": r["project"],
                "filename": r["filename"], "label": r["label"], "length_bin": r["length_bin"]}
        is_train = r["group_id"] not in test_groups
        out.append({**base, "is_augmented": 0, "split": "train" if is_train else "test",
                    "selected": int(r["sample_id"] in selected), "ratio": base_ratio if is_train else "",
                    "tok_with_comments": r["tok_with_comments"], "tok_no_comments": r["tok_no_comments"]})
        if r["sample_id"] in selected:
            vs = [(first[r["sample_id"]], mid_ratio)] + [(v, top_ratio) for v in extra[r["sample_id"]]]
            for v, ratio in vs:
                out.append({**base, "is_augmented": int(v["op_id"]), "split": "train", "selected": 0,
                            "ratio": ratio,
                            "tok_with_comments": v["tok_with_comments"], "tok_no_comments": v["tok_no_comments"]})
    return out


def summarize(seed, rows):
    ratios = sorted({r["ratio"] for r in rows if r["ratio"] != ""})

    def count(split, label, augmented, up_to=ratios[1]):
        return sum(r["split"] == split and r["label"] == label and (r["is_augmented"] > 0) == augmented
                   and (split == "test" or r["ratio"] <= up_to) for r in rows)
    train_clean, train_smell = count("train", "0", False), count("train", "1", False)
    aug_clean, aug_smell = count("train", "0", True), count("train", "1", True)
    top_clean, top_smell = count("train", "0", True, ratios[2]), count("train", "1", True, ratios[2])
    return {
        "seed": seed,
        "test_clean": count("test", "0", False), "test_smell": count("test", "1", False),
        "train_clean": train_clean, "train_smell": train_smell,
        "added_clean": aug_clean, "added_smell": aug_smell,
        "ratio_before": round(train_smell / train_clean, 3),
        "ratio_after": round((train_smell + aug_smell) / (train_clean + aug_clean), 3),
        "added_clean_top": top_clean, "added_smell_top": top_smell,
        "ratio_top": round((train_smell + top_smell) / (train_clean + top_clean), 3),
        "operators_used": len({r["is_augmented"] for r in rows if r["is_augmented"] > 0}),
    }


def write_csv(path, rows):
    os.makedirs(os.path.dirname(path) or ".", exist_ok=True)
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--tokens", default="token_counts.csv")
    parser.add_argument("--pool", default="augmented_pool.csv")
    parser.add_argument("--code-dir", default="ds_comment/0")
    parser.add_argument("--fit-col", default="fits_with_comments")
    parser.add_argument("--exclude-projects", nargs="*", default=["trove-2.1.0"])
    parser.add_argument("--seeds", type=int, nargs="*", default=list(range(10)) + [42, 43, 44])
    parser.add_argument("--test-frac", type=float, default=0.2)
    parser.add_argument("--clean-frac", type=float, default=0.25,
                        help="share of the clean train methods that get an extra sample")
    parser.add_argument("--top-ratio", type=float, default=1.0,
                        help="smelly:clean train ratio reached by the further smelly samples")
    parser.add_argument("--out-dir", default="datasets")
    args = parser.parse_args()

    originals = load_originals(args.tokens, args.fit_col, args.exclude_projects, args.code_dir)
    variants = load_variants(args.pool)
    print(f"{len(originals)} original methods "
          f"({sum(r['label'] == '1' for r in originals)} smelly), "
          f"{len({r['group_id'] for r in originals})} groups")

    summaries, strata, tested = [], Counter(), Counter()
    for seed in args.seeds:
        rows = build(seed, originals, variants, args.test_frac, args.clean_frac, args.top_ratio)
        write_csv(os.path.join(args.out_dir, f"dataset_seed{seed}.csv"), [{c: r[c] for c in COLUMNS} for r in rows])
        summaries.append(summarize(seed, rows))
        for r in rows:
            if r["is_augmented"] == 0:
                strata[(r["label"], r["project"], r["length_bin"], r["split"])] += 1
                tested[r["sample_id"]] += r["split"] == "test"

    write_csv(os.path.join(args.out_dir, "datasets_summary.csv"), summaries)
    cols = list(summaries[0])
    print(" ".join(f"{c:>14}" for c in cols))
    for s in summaries:
        print(" ".join(f"{s[c]:>14}" for c in cols))

    # Test share per stratum, pooled over seeds, to check the stratification
    check = []
    for label, project, length_bin in sorted({k[:3] for k in strata}):
        test = strata[(label, project, length_bin, "test")]
        total = test + strata[(label, project, length_bin, "train")]
        check.append({"label": label, "project": project, "length_bin": length_bin,
                      "methods": total // len(args.seeds), "test_share": round(test / total, 3)})
    write_csv(os.path.join(args.out_dir, "split_check.csv"), check)
    shares = [c["test_share"] for c in check]
    print(f"test share per stratum over {len(check)} strata: min {min(shares)}, max {max(shares)}")
    never = [r["sample_id"] for r in originals if tested[r["sample_id"]] == 0]
    print(f"methods never in a test split over {len(args.seeds)} seeds: {len(never)}")
    print(f"-> {args.out_dir}/dataset_seed<seed>.csv, datasets_summary.csv, split_check.csv")


if __name__ == "__main__":
    main()
