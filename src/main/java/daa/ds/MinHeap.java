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

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }
        int min = heap[0];
        metrics.addSteps(1);
        size--;
        if (size > 0) {
            heap[0] = heap[size];
            metrics.addMoves(1);
            bubbleDown(0);
        }
        return min;
    }

    private void bubbleDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            if (left >= size) {
                return;
            }
            int right = left + 1;
            int smallest = left;
            metrics.addSteps(1);
            if (right < size) {
                metrics.addSteps(1);
                metrics.addComparisons(1);
                if (heap[right] < heap[left]) {
                    smallest = right;
                }
            }
            metrics.addSteps(1);
            metrics.addComparisons(1);
            if (heap[i] <= heap[smallest]) {
                return;
            }
            swap(i, smallest);
            i = smallest;
        }
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

    public void buildHeap(int[] array) {
        heap = new int[Math.max(DEFAULT_CAPACITY, array.length)];
        for (int i = 0; i < array.length; i++) {
            heap[i] = array[i];
        }
        metrics.addMoves(array.length);
        size = array.length;
        for (int i = size / 2 - 1; i >= 0; i--) {
            bubbleDown(i);
        }
    }

    public boolean isValidHeap() {
        for (int child = 1; child < size; child++) {
            if (heap[(child - 1) / 2] > heap[child]) {
                return false;
            }
        }
        return true;
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
