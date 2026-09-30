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

## 2. Loop invariant proofs

### 2.1 `DynamicArray.contains(x)`

```java
for (int i = 0; i < size; i++) {
    if (data[i] == x) return true;
}
return false;
```

- **Invariant.** Before the iteration with index *i*, `x` does not occur in `data[0..i−1]`.
- **Initialization.** For *i* = 0 the range `data[0..−1]` is empty, so the statement is true.
- **Maintenance.** If `data[i] == x`, the method returns `true`, which is correct because *i* < size. Otherwise
  `data[i] ≠ x`, so `x` is absent from `data[0..i]`, and after `i++` the invariant holds again.
- **Termination.** *i* grows by 1 and is bounded by `size`. If the loop ends through its condition, *i* = size, and the
  invariant says `x` is not in `data[0..size−1]`, so `return false` is correct.
- **Conclusion.** `true` is returned only with a witness index and `false` only after every stored element was checked,
  so `contains` is correct for every input, including the empty array.

### 2.2 `MinHeap.bubbleDown(i)` (used by `extractMin` and `buildHeap`)

```java
while (true) {
    int left = 2*i + 1;
    if (left >= size) return;
    int smallest = (left + 1 < size && heap[left + 1] < heap[left]) ? left + 1 : left;
    if (heap[i] <= heap[smallest]) return;
    swap(i, smallest);
    i = smallest;
}
```

Let *r* be the index bubble-down starts from (0 in `extractMin`).

- **Invariant.** Before each iteration, inside the subtree of *r*: (a) every pair `heap[parent] ≤ heap[child]` holds
  except possibly the pairs (*i*, child of *i*); (b) if *i* ≠ *r*, the parent of *i* is ≤ every child of *i*.
- **Initialization.** *i* = *r*. The subtrees of the children of *r* are already heaps (untouched in `extractMin`; built
  earlier in `buildHeap`, which goes from `size/2 − 1` down to 0). So only the pairs at *r* can be broken; (b) is
  vacuous.
- **Maintenance.** Let *m* be the smaller child. If `heap[i] ≤ m`, the pairs at *i* are fine, (a) holds with no
  exception and the method returns. Otherwise after the swap node *i* holds *m*, which is ≤ the other child and < the
  old value moved down, so the pairs at *i* are fixed; by (b) the parent of *i* is ≤ *m*. The value *m* was the parent of
  the children of `smallest`, so it is ≤ them, which is (b) for the new *i* = `smallest`. Only the pairs at `smallest`
  can be broken, so (a) holds for the new *i*.
- **Termination.** *i* at least doubles each iteration, so there are ≤ ⌊log₂ size⌋ iterations. The loop stops when *i*
  is a leaf or `heap[i] ≤ heap[smallest]`; in both cases (a) holds with no exception, so the subtree of *r* is a heap.
- **Conclusion.** In `extractMin` the removed root was the minimum, and after bubble-down the remaining values form a
  heap again, so n calls return values in non-decreasing order. In `buildHeap`, applying the result to
  *r* = `size/2−1, …, 0` makes the whole array a heap.

## 3. Results

All charts use log-log axes: slope 1 means linear growth, a flat line means Θ(1). Lines overlap where values are
identical (e.g. steps = comparisons in W2).

![Time vs n](results/plots/time_vs_n.png)

Key numbers at n = 100 000:

| Workload | DynamicArray | MyLinkedList | Operations (array / list) |
|---|---|---|---|
| W1: 10 000 × `get` | 0.062 ms | 686.9 ms | steps: 10 000 / 502 499 208 |
| W2: 1 000 × `contains` | 52.6 ms | 148.7 ms | comparisons: 74 174 335 / 74 174 335 |
| W3 head: 1 000 + 1 000 | 29.6 ms | 0.019 ms | moves: 200 999 000 / 3 000 |
| W3 middle: 1 000 + 1 000 | 14.8 ms | 133.2 ms | array moves 100 999 000 / list steps 99 999 000 |
| W4: n × `insert` + n × `extractMin` | — | — | MinHeap: 13.3 ms, 3 059 125 comparisons |

![W1](results/plots/w1_random_access.png)

**W1.** The array line is flat (1 step per `get`, ≈ 6 ns). The list walks ≈ n/2 nodes per call, so time and steps grow
linearly.

![W2](results/plots/w2_search.png)

**W2.** Both structures do exactly the same number of steps and comparisons (≈ 0.75·n per query), yet the list is
2.7–2.9× slower at every n. This is the "same Big-O, different time" case explained in §4.

![W3](results/plots/w3_insert_remove.png)

**W3.** At the head the list needs 3 link updates per insert/remove pair (3 000 moves for any n) and stays flat, while the
array shifts all elements: ≈ 1 500× slower at n = 100 000. In the middle both do ≈ 10⁸ elementary operations (array
shifts vs list hops), but the array is 9× faster. For n ≤ 1 000 array moves also include resize copies
(e.g. 1 920 extra moves for n = 100).

![W4](results/plots/w4_priority.png)

**W4.** The ratio comparisons / (n log₂ n) is 1.56, 1.73, 1.80 and 1.84 for the four sizes, which confirms
Θ(n log n). Most of the cost is in `extractMin`. The benchmark checks that every extracted value is ≥ the previous one.
