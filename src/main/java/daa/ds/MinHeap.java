package daa.ds;

import daa.metrics.Metrics;

public final class MinHeap {

    private static final int DEFAULT_CAPACITY = 8;

    private int[] heap;
    private int size;
    private final Metrics metrics = new Metrics();

    public MinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public MinHeap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + initialCapacity);
        }
        heap = new int[initialCapacity];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Metrics metrics() {
        return metrics;
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }
        metrics.addSteps(1);
        return heap[0];
    }

    public void insert(int x) {
        if (size == heap.length) {
            grow();
        }
        heap[size] = x;
        metrics.addMoves(1);
        bubbleUp(size);
        size++;
    }

    private void bubbleUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            metrics.addSteps(2);
            metrics.addComparisons(1);
            if (heap[parent] <= heap[i]) {
                return;
            }
            swap(parent, i);
            i = parent;
        }
    }

    private void swap(int i, int j) {
        int tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
        metrics.addMoves(2);
    }

    private void grow() {
        int[] bigger = new int[heap.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = heap[i];
        }
        metrics.addMoves(size);
        heap = bigger;
    }
}
