"""Software and hardware versions for the paper, written to results/versions.txt.

Run it on the machine (and in the Python environment) that ran the experiments:
Python, the packages the scripts import, CUDA, cuDNN, the GPU with its driver,
the CPU, and the commit of the UniXcoder checkpoint in the Hugging Face cache.
A package that is not installed is listed as such.

Usage:
    python scripts/8_versions.py
"""
import argparse
import importlib.metadata
import os
import platform
import subprocess
import sys

# Distribution names as pip knows them, in the order the paper would cite them
PACKAGES = ["torch", "transformers", "tokenizers", "huggingface-hub", "codecarbon", "numpy", "scipy",
            "matplotlib", "openpyxl", "javalang", "nltk", "wordninja"]
MODEL = "microsoft/unixcoder-base"


def package_versions():
    out = []
    for name in PACKAGES:
        try:
            out.append((name, importlib.metadata.version(name)))
        except importlib.metadata.PackageNotFoundError:
            out.append((name, "not installed"))
    return out


def gpu_info():
    lines = []
    try:
        import torch
        lines.append(("CUDA (torch build)", torch.version.cuda or "none"))
        lines.append(("cuDNN", str(torch.backends.cudnn.version()) if torch.backends.cudnn.is_available() else "none"))
        if torch.cuda.is_available():
            for i in range(torch.cuda.device_count()):
                props = torch.cuda.get_device_properties(i)
                lines.append((f"GPU {i}", f"{props.name}, {props.total_memory / 1024 ** 3:.1f} GB"))
        else:
            lines.append(("GPU", "none visible to torch"))
    except ImportError:
        lines.append(("torch", "not installed"))
    try:
        driver = subprocess.run(["nvidia-smi", "--query-gpu=driver_version", "--format=csv,noheader"],
                                capture_output=True, text=True, timeout=20).stdout.strip()
        lines.append(("NVIDIA driver", driver.splitlines()[0] if driver else "unknown"))
    except (OSError, subprocess.SubprocessError):
        lines.append(("NVIDIA driver", "nvidia-smi not found"))
    return lines


def cpu_name():
    if sys.platform.startswith("linux"):
        try:
            with open("/proc/cpuinfo", encoding="utf-8") as f:
                for line in f:
                    if line.startswith("model name"):
                        return line.split(":", 1)[1].strip()
        except OSError:
            pass
    return platform.processor() or "unknown"


def model_commit():
    """Commit of the cached checkpoint, so the exact weights can be cited."""
    try:
        from huggingface_hub import scan_cache_dir
        for repo in scan_cache_dir().repos:
            if repo.repo_id == MODEL:
                return ", ".join(sorted(r.commit_hash for r in repo.revisions))
        return "not in the Hugging Face cache"
    except Exception as error:  # the cache layout or the package may differ
        return f"unknown ({type(error).__name__})"


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--out", default="results/versions.txt")
    args = parser.parse_args()

    rows = [("Python", platform.python_version()), ("OS", platform.platform()), ("CPU", cpu_name())]
    rows += gpu_info()
    rows += package_versions()
    rows.append((f"{MODEL} commit", model_commit()))
    width = max(len(k) for k, _ in rows)
    text = "\n".join(f"{k:<{width}}  {v}" for k, v in rows)
    print(text)
    os.makedirs(os.path.dirname(args.out) or ".", exist_ok=True)
    with open(args.out, "w", encoding="utf-8") as f:
        f.write(text + "\n")
    print(f"-> {args.out}")


if __name__ == "__main__":
    main()
