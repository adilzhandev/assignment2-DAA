# Assignment 2 — Data Structures: Report

`DynamicArray` (`int[]`, 2x growth), `MyLinkedList` (singly linked, head + tail) and `MinHeap` (array-based binary heap)
are written from scratch on primitive `int`. Array and list share the `IntList` interface, so one benchmark runs both.
Setup: OpenJDK 25, seed `new Random(42)`, adaptive warm-up (≥ 3 runs and ≥ 200 ms per case) after a global JIT
warm-up pass, then 5 measured runs; the median is reported. Data: `results/results.csv` (W1–W4),
`results/build_heap.csv` (bonus B), `results/memory.csv` (bonus A).

## 1. Complexity

*n* = number of elements, *i* = index. Θ is used when the bound is tight.

| Structure | Operation | Best | Average | Worst | Justification |
|---|---|---|---|---|---|
| DynamicArray | `get(i)` | Θ(1) | Θ(1) | Θ(1) | address = base + 4·i, one read |
| | `add(x)` | Θ(1) | Θ(1) amortized | Θ(n) | a resize copies n cells, but 1+2+4+…+n < 2n over n appends |
| | `add(i, x)` | Θ(1) | Θ(n) | Θ(n) | shifts n − i cells; best at i = n |
| | `remove(i)` | Θ(1) | Θ(n) | Θ(n) | shifts n − 1 − i cells; best at the last index |
| | `contains(x)` | Θ(1) | Θ(n) | Θ(n) | linear scan; best if x is first, worst if absent |
| MyLinkedList | `get(i)` | Θ(1) | Θ(n) | Θ(n) | i hops from head |
| | `add(x)` | Θ(1) | Θ(1) | Θ(1) | uses `tail`, no traversal |
| | `add(i, x)` | Θ(1) | Θ(n) | Θ(n) | walk to node i − 1, then 2 link updates; i = 0 or i = n is Θ(1) |
| | `remove(i)` | Θ(1) | Θ(n) | Θ(n) | walk to node i − 1; i = 0 is Θ(1), last index is Θ(n) (no `prev`) |
| | `contains(x)` | Θ(1) | Θ(n) | Θ(n) | linear walk |
| MinHeap | `peekMin()` | Θ(1) | Θ(1) | Θ(1) | minimum is always `heap[0]` |
| | `insert(x)` | Θ(1) | O(log n), Θ(1) expected on random data | Θ(log n) | bubble-up climbs ≤ ⌊log₂ n⌋ levels, stops when parent ≤ x |
| | `extractMin()` | Θ(1) | Θ(log n) | Θ(log n) | bubble-down goes ≤ ⌊log₂ n⌋ levels; best if all values are equal |
| | `buildHeap(a)` | Θ(n) | Θ(n) | Θ(n) | Σ h·n/2^{h+1} ≤ n (bonus B) |

Space: all three structures are Θ(n). The array and heap use one `int[]` with capacity < 2·(max size);
the list uses one 24-byte node per element (bonus A). Auxiliary space is Θ(1) per operation
(Θ(n) only while a resize copies the array).
