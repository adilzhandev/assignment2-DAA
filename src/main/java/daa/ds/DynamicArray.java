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
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public int remove(int index) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public int get(int index) {
        checkIndex(index, size);
        metrics.addSteps(1);
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        throw new UnsupportedOperationException("not implemented yet");
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
