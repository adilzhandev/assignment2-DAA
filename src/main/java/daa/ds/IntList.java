package daa.ds;

import daa.metrics.Metrics;

public interface IntList {

    void add(int x);

    void add(int index, int x);

    int remove(int index);

    int get(int index);

    boolean contains(int x);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    Metrics metrics();
}
