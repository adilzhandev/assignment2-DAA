package daa.ds;

import daa.metrics.Metrics;

public final class MyLinkedList implements IntList {

    private static final class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics = new Metrics();

    @Override
    public void add(int x) {
        Node node = new Node(x);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        metrics.addMoves(2);
        size++;
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
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public boolean contains(int x) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }
}
