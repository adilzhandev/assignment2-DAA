# DAA Assignment 2 — Data Structures

In-memory workload engine: `DynamicArray`, `MyLinkedList` and `MinHeap` written from scratch
(primitive `int` storage, no `java.util` collections inside the structures), with honest operation
counters, a reproducible benchmark (`new Random(42)`) and JUnit 5 tests.

## Requirements

- JDK 17 or newer
- Maven is **not** required: the project ships the Maven Wrapper (`./mvnw`, `mvnw.cmd` on Windows)

## Build and test

```bash
./mvnw clean test
```

## Run the benchmark (one command)

```bash
./mvnw -q compile exec:java
```

Produces `results/results.csv` (W1–W4), `results/build_heap.csv` (bonus B: n × insert vs Floyd buildHeap)
and `results/memory.csv` (bonus A: JOL memory footprint).
A different output path can be passed as an argument: `./mvnw -q compile exec:java -Dexec.args="out/results.csv"`.
You can also run `daa.Main` directly from the IDE. JOL may print `WARNING` lines about
dynamic attach / `sun.misc.Unsafe` on new JDKs — they are harmless.

## Plots

PNG charts are in `results/plots/`: `time_vs_n.png` with all workloads on one chart, and for every workload
a time chart, an operations chart and a two-panel figure used in REPORT.md (log-log axes: `n (input size)` vs
`Time (ms)` or operation count).

## Project layout

```
src/main/java/daa/
  ds/        IntList, DynamicArray, MyLinkedList, MinHeap
  metrics/   Metrics (steps/moves/comparisons), Result, CsvWriter
  bench/     Benchmark (W1-W5), MemoryBenchmark (JOL)
  Main.java  runs everything and writes the CSV files
src/test/java/daa/   JUnit 5 tests
results/results.csv, results/build_heap.csv, results/memory.csv, results/plots/*.png
REPORT.md
```

## What is counted

| counter       | meaning                                                                  |
|---------------|--------------------------------------------------------------------------|
| `steps`       | one read of an array cell or one hop to the next node                    |
| `moves`       | one element shifted/copied inside an array or one link (pointer) update |
| `comparisons` | one comparison of two elements                                           |

Counters are incremented inside the operations themselves. The benchmark resets them after the
structure is filled, so each CSV row shows only the cost of the workload.
`time_ms` is a `double` (median of 5 runs after an adaptive warm-up, printed with 4 decimals).

## Git workflow

`main` contains only working code (tag `v1.0`); features were developed on
`feature/metrics`, `feature/array`, `feature/list`, `feature/heap` and the report on `docs/report`,
each merged with `--no-ff`.
