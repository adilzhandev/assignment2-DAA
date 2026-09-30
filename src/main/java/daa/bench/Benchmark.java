package daa.bench;

import daa.ds.DynamicArray;
import daa.ds.IntList;
import daa.ds.MinHeap;
import daa.ds.MyLinkedList;
import daa.metrics.Metrics;
import daa.metrics.Result;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public final class Benchmark {

    public static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    public static final long SEED = 42;
    static final int WARMUP = 3;
    static final int MAX_WARMUP = 200;
    static final long WARMUP_BUDGET_NANOS = 200_000_000L;
    static final int RUNS = 5;

    static final int GET_QUERIES = 10_000;
    static final int SEARCH_QUERIES = 1_000;
    static final int INSERT_REMOVE_OPS = 1_000;
    static final int VALUE_BOUND = 1_000_000;

    private static volatile long sink;

    private interface Trial {
        void run();

        Metrics metrics();
    }

    static final int[] WARMUP_SIZES = {100, 1_000, 10_000};

    private final List<Result> results = new ArrayList<>();
    private final List<Result> buildHeapResults = new ArrayList<>();
    private boolean recording;

    public List<Result> run() {
        recording = false;
        runSizes(WARMUP_SIZES);
        recording = true;
        runSizes(SIZES);
        return results;
    }

    public List<Result> buildHeapResults() {
        return buildHeapResults;
    }

    private void runSizes(int[] sizes) {
        for (int n : sizes) {
            int[] data = randomData(n);
            w1RandomAccess(n, data);
            w2Search(n, data);
            w3InsertRemove(n, data, "head");
            w3InsertRemove(n, data, "middle");
            w4Priority(n, data);
            w5BuildHeap(n, data);
        }
    }

    static int[] randomData(int n) {
        Random rnd = new Random(SEED);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rnd.nextInt(VALUE_BOUND);
        }
        return data;
    }

    private static List<Supplier<IntList>> listFactories() {
        return List.of(DynamicArray::new, MyLinkedList::new);
    }

    private static IntList filled(Supplier<IntList> factory, int[] data) {
        IntList list = factory.get();
        for (int x : data) {
            list.add(x);
        }
        return list;
    }

    private void w1RandomAccess(int n, int[] data) {
        Random rnd = new Random(SEED);
        int[] indexes = new int[GET_QUERIES];
        for (int i = 0; i < GET_QUERIES; i++) {
            indexes[i] = rnd.nextInt(n);
        }
        for (Supplier<IntList> factory : listFactories()) {
            measure("W1_random_access", "-", n, () -> {
                IntList list = filled(factory, data);
                return trial(list.metrics(), () -> {
                    long sum = 0;
                    for (int idx : indexes) {
                        sum += list.get(idx);
                    }
                    sink += sum;
                });
            }, factory.get().getClass().getSimpleName());
        }
    }

    private void w2Search(int n, int[] data) {
        Random rnd = new Random(SEED);
        int[] queries = new int[SEARCH_QUERIES];
        for (int i = 0; i < SEARCH_QUERIES; i++) {
            queries[i] = (i % 2 == 0) ? data[rnd.nextInt(n)] : VALUE_BOUND + rnd.nextInt(VALUE_BOUND);
        }
        for (Supplier<IntList> factory : listFactories()) {
            measure("W2_search", "-", n, () -> {
                IntList list = filled(factory, data);
                return trial(list.metrics(), () -> {
                    int found = 0;
                    for (int q : queries) {
                        if (list.contains(q)) {
                            found++;
                        }
                    }
                    if (found < SEARCH_QUERIES / 2) {
                        throw new IllegalStateException("present values were not found: " + found);
                    }
                    sink += found;
                });
            }, factory.get().getClass().getSimpleName());
        }
    }

    private void w3InsertRemove(int n, int[] data, String variant) {
        int position = variant.equals("head") ? 0 : n / 2;
        for (Supplier<IntList> factory : listFactories()) {
            measure("W3_insert_remove", variant, n, () -> {
                IntList list = filled(factory, data);
                return trial(list.metrics(), () -> {
                    for (int i = 0; i < INSERT_REMOVE_OPS; i++) {
                        list.add(position, data[i % n]);
                    }
                    long sum = 0;
                    for (int i = 0; i < INSERT_REMOVE_OPS; i++) {
                        sum += list.remove(position);
                    }
                    sink += sum;
                });
            }, factory.get().getClass().getSimpleName());
        }
    }

    private void w4Priority(int n, int[] data) {
        measure("W4_priority", "-", n, () -> {
            MinHeap heap = new MinHeap();
            return trial(heap.metrics(), () -> {
                for (int x : data) {
                    heap.insert(x);
                }
                int prev = Integer.MIN_VALUE;
                for (int i = 0; i < n; i++) {
                    int cur = heap.extractMin();
                    if (cur < prev) {
                        throw new IllegalStateException("extractMin order broken at " + i);
                    }
                    prev = cur;
                }
            });
        }, "MinHeap");
    }

    private void w5BuildHeap(int n, int[] data) {
        int[] descending = data.clone();
        Arrays.sort(descending);
        for (int i = 0, j = n - 1; i < j; i++, j--) {
            int tmp = descending[i];
            descending[i] = descending[j];
            descending[j] = tmp;
        }
        buildHeapCase("insert", "floyd", n, data);
        buildHeapCase("insert_desc", "floyd_desc", n, descending);
    }

    private void buildHeapCase(String insertVariant, String floydVariant, int n, int[] input) {
        measure("W5_build_heap", insertVariant, n, () -> {
            MinHeap heap = new MinHeap();
            return trial(heap.metrics(), () -> {
                for (int x : input) {
                    heap.insert(x);
                }
            });
        }, "MinHeap");
        measure("W5_build_heap", floydVariant, n, () -> {
            MinHeap heap = new MinHeap();
            return trial(heap.metrics(), () -> heap.buildHeap(input));
        }, "MinHeap");
    }

    private static Trial trial(Metrics metrics, Runnable body) {
        return new Trial() {
            @Override
            public void run() {
                body.run();
            }

            @Override
            public Metrics metrics() {
                return metrics;
            }
        };
    }

    private void measure(String workload, String variant, int n, Supplier<Trial> setup, String structure) {
        long warmupNanos = 0;
        for (int w = 0; w < MAX_WARMUP && (w < WARMUP || warmupNanos < WARMUP_BUDGET_NANOS); w++) {
            warmupNanos += timeOnce(setup.get());
        }
        if (!recording) {
            return;
        }
        double[] times = new double[RUNS];
        Metrics last = null;
        for (int r = 0; r < RUNS; r++) {
            Trial trial = setup.get();
            times[r] = timeOnce(trial) / 1_000_000.0;
            last = trial.metrics();
        }
        double median = median(times);
        Result result = new Result(workload, variant, structure, n, median,
                last.steps(), last.moves(), last.comparisons());
        (workload.startsWith("W5") ? buildHeapResults : results).add(result);
        System.out.printf("%-17s %-11s %-13s n=%-7d %10.4f ms  %s%n",
                workload, variant, structure, n, median, last);
    }

    private static long timeOnce(Trial trial) {
        trial.metrics().reset();
        long start = System.nanoTime();
        trial.run();
        return System.nanoTime() - start;
    }

    static double median(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return sorted.length % 2 == 1 ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2.0;
    }
}
