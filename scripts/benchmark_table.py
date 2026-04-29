#!/usr/bin/env python3
"""
Pivot JMH-style benchmark text into a markdown table.

Columns are sizes and rows are benchmarks.

Usage:
    python scripts/benchmark_table.py input.txt
    python scripts/benchmark_table.py < input.txt
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass


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


@dataclass(frozen=True)
class BenchmarkRow:
    benchmark: str
    size: int
    mode: str
    count: int
    score: float
    error: float
    units: str


def short_benchmark_name(full_name: str) -> str:
    if "." in full_name:
        return full_name.split(".")[-1]
    return full_name


def parse_rows(text: str) -> list[BenchmarkRow]:
    rows: list[BenchmarkRow] = []

    for raw_line in text.splitlines():
        line = raw_line.strip()
        if not line or line.startswith("Benchmark"):
            continue

        match = LINE_PATTERN.match(line)
        if not match:
            print(f"Warning: could not parse line:\n  {raw_line}", file=sys.stderr)
            continue

        rows.append(
            BenchmarkRow(
                benchmark=match.group("benchmark"),
                size=int(match.group("size")),
                mode=match.group("mode"),
                count=int(match.group("cnt")),
                score=float(match.group("score")),
                error=float(match.group("error")),
                units=match.group("units"),
            )
        )

    return rows


def build_labels(rows: list[BenchmarkRow]) -> dict[str, str]:
    short_names: dict[str, list[str]] = {}
    for row in rows:
        short_names.setdefault(short_benchmark_name(row.benchmark), []).append(row.benchmark)

    labels: dict[str, str] = {}
    for row in rows:
        short_name = short_benchmark_name(row.benchmark)
        if len(set(short_names[short_name])) == 1:
            labels[row.benchmark] = short_name
        else:
            labels[row.benchmark] = row.benchmark

    return labels


def format_cell(row: BenchmarkRow, include_error: bool) -> str:
    if include_error:
        return f"{row.score:.3f} ± {row.error:.3f}"
    return f"{row.score:.3f}"


def make_markdown_table(rows: list[BenchmarkRow], include_error: bool) -> str:
    if not rows:
        return "No benchmark rows parsed."

    sizes = sorted({row.size for row in rows})
    benchmarks = sorted({row.benchmark for row in rows})
    labels = build_labels(rows)

    lookup: dict[tuple[str, int], BenchmarkRow] = {}
    for row in rows:
        key = (row.benchmark, row.size)
        if key in lookup:
            raise ValueError(f"Duplicate benchmark-size pair found: {row.benchmark} @ {row.size}")
        lookup[key] = row

    header = ["Benchmark", *[str(size) for size in sizes]]
    separator = ["---", *["---:" for _ in sizes]]

    lines = [
        "| " + " | ".join(header) + " |",
        "| " + " | ".join(separator) + " |",
    ]

    for benchmark in benchmarks:
        cells = [labels[benchmark]]
        for size in sizes:
            row = lookup.get((benchmark, size))
            cells.append(format_cell(row, include_error) if row else "")
        lines.append("| " + " | ".join(cells) + " |")

    units = sorted({row.units for row in rows})
    if len(units) == 1:
        lines.append("")
        lines.append(f"Units: `{units[0]}`")

    return "\n".join(lines)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("input", nargs="?", help="Benchmark text file. Reads stdin when omitted.")
    parser.add_argument(
        "--score-only",
        action="store_true",
        help="Print only the score in each cell instead of 'score ± error'.",
    )
    return parser.parse_args()


def read_input(input_path: str | None) -> str:
    if input_path:
        with open(input_path, "r", encoding="utf-8") as handle:
            return handle.read()
    return sys.stdin.read()


def main() -> None:
    args = parse_args()
    rows = parse_rows(read_input(args.input))
    print(make_markdown_table(rows, include_error=not args.score_only))


if __name__ == "__main__":
    main()
