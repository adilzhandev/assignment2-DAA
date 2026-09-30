package daa.ds;

import daa.metrics.Metrics;

public final class DynamicArray implements IntList {

    private static final int DEFAULT_CAPACITY = 8;

    private int[] data;
    private int size;
    private final Metrics metrics = new Metrics();

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + initialCapacity);
        }
        data = new int[initialCapacity];
    }

    @Override
    public void add(int x) {
        if (size == data.length) {
            grow();
        }
        data[size++] = x;
    }

    @Override
    public void add(int index, int x) {
        checkIndex(index, size + 1);
        if (size == data.length) {
            grow();
        }
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        metrics.addMoves(size - index);
        data[index] = x;
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index, size);
        int removed = data[index];
        metrics.addSteps(1);
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        metrics.addMoves(size - 1 - index);
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        checkIndex(index, size);
        metrics.addSteps(1);
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.addSteps(1);
            metrics.addComparisons(1);
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }

    private static void checkIndex(int index, int bound) {
        if (index < 0 || index >= bound) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + bound);
        }
    }

    private void grow() {
        int[] bigger = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[i];
        }
        metrics.addMoves(size);
        data = bigger;
    }
}
