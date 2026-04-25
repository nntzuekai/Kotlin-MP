#!/usr/bin/env python3
"""
Convert JMH-style benchmark text into markdown tables grouped by size,
and plot benchmark performance vs matrix size.

Usage:
    python benchmark_to_markdown_and_plot.py input.txt

Or:
    python benchmark_to_markdown_and_plot.py < input.txt

Outputs:
    - Markdown tables printed to stdout
    - A plot saved as benchmark_plot.png
"""

from __future__ import annotations

import re
import sys
from collections import defaultdict
from typing import Dict, List, Any

import matplotlib.pyplot as plt


LINE_PATTERN = re.compile(
    r"""
    ^(?P<benchmark>\S+)
    \s+
    (?P<size>\d+)
    \s+
    (?P<mode>\S+)
    \s+
    (?P<cnt>\d+)
    \s+
    (?P<score>\d+(?:\.\d+)?)
    \s+±\s+
    (?P<error>\d+(?:\.\d+)?)
    \s+
    (?P<units>\S+)
    \s*$
    """,
    re.VERBOSE,
)


def short_benchmark_name(full_name: str) -> str:
    """Extract a shorter benchmark label from the full JMH benchmark name."""
    if "." in full_name:
        return full_name.split(".")[-1]
    return full_name


def parse_benchmark_text(text: str) -> tuple[Dict[int, List[dict]], Dict[str, List[dict]]]:
    """
    Returns:
        grouped_by_size: {size: [rows]}
        grouped_by_benchmark: {benchmark_name: [rows]}
    """
    grouped_by_size: Dict[int, List[dict]] = defaultdict(list)
    grouped_by_benchmark: Dict[str, List[dict]] = defaultdict(list)

    for raw_line in text.splitlines():
        line = raw_line.strip()
        if not line:
            continue

        if line.startswith("Benchmark"):
            continue

        match = LINE_PATTERN.match(line)
        if not match:
            print(f"Warning: could not parse line:\n  {raw_line}", file=sys.stderr)
            continue

        benchmark = match.group("benchmark")
        row = {
            "Benchmark": benchmark,
            "ShortBenchmark": short_benchmark_name(benchmark),
            "Size": int(match.group("size")),
            "Mode": match.group("mode"),
            "Cnt": int(match.group("cnt")),
            "Score": float(match.group("score")),
            "Error": float(match.group("error")),
            "Units": match.group("units"),
        }

        grouped_by_size[row["Size"]].append(row)
        grouped_by_benchmark[benchmark].append(row)

    grouped_by_size = dict(sorted(grouped_by_size.items()))
    for benchmark in grouped_by_benchmark:
        grouped_by_benchmark[benchmark].sort(key=lambda r: r["Size"])

    return grouped_by_size, grouped_by_benchmark


def make_markdown_table(rows: List[dict]) -> str:
    lines = [
        "| Benchmark | Mode | Cnt | Score | Error | Units |",
        "|---|---:|---:|---:|---:|---|",
    ]

    for row in rows:
        lines.append(
            f"| {row['Benchmark']} | {row['Mode']} | {row['Cnt']} | "
            f"{row['Score']:.3f} | {row['Error']:.3f} | {row['Units']} |"
        )

    return "\n".join(lines)


def convert_to_markdown(grouped_by_size: Dict[int, List[dict]]) -> str:
    if not grouped_by_size:
        return "No benchmark rows parsed."

    sections = []
    for size, rows in grouped_by_size.items():
        sections.append(f"### Size {size}\n")
        sections.append(make_markdown_table(rows))
        sections.append("")

    return "\n".join(sections).rstrip()


def plot_benchmarks(grouped_by_benchmark: Dict[str, List[dict]], output_file: str = "benchmark_plot.png") -> None:
    """
    Plot score vs size for each benchmark and save to a PNG file.
    """
    if not grouped_by_benchmark:
        print("No data to plot.", file=sys.stderr)
        return

    plt.figure(figsize=(10, 6))

    for benchmark, rows in grouped_by_benchmark.items():
        sizes = [row["Size"] for row in rows]
        scores = [row["Score"] for row in rows]
        errors = [row["Error"] for row in rows]
        label = rows[0]["ShortBenchmark"]

        plt.errorbar(sizes, scores, yerr=errors, marker="o", capsize=4, label=label)

    # plt.xscale("log")
    # plt.yscale("log")

    plt.xlabel("Matrix Size")
    plt.ylabel("Time (ms/op)")
    plt.title("Matrix Multiplication Benchmark Performance")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(output_file, dpi=200)
    plt.close()


def read_input() -> str:
    if len(sys.argv) > 1:
        with open(sys.argv[1], "r", encoding="utf-8") as f:
            return f.read()
    return sys.stdin.read()


def main() -> None:
    text = read_input()
    grouped_by_size, grouped_by_benchmark = parse_benchmark_text(text)

    markdown_output = convert_to_markdown(grouped_by_size)
    print(markdown_output)

    plot_file = "benchmark_plot.png"
    plot_benchmarks(grouped_by_benchmark, plot_file)
    print(f"\nPlot saved to: {plot_file}", file=sys.stderr)


if __name__ == "__main__":
    main()