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
        checkIndex(index, size + 1);
        if (index == size) {
            add(x);
            return;
        }
        Node node = new Node(x);
        if (index == 0) {
            node.next = head;
            head = node;
        } else {
            Node prev = nodeAt(index - 1);
            node.next = prev.next;
            prev.next = node;
        }
        metrics.addMoves(2);
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index, size);
        Node removed;
        if (index == 0) {
            removed = head;
            head = head.next;
            metrics.addMoves(1);
            if (head == null) {
                tail = null;
                metrics.addMoves(1);
            }
        } else {
            Node prev = nodeAt(index - 1);
            removed = prev.next;
            prev.next = removed.next;
            metrics.addMoves(1);
            if (removed == tail) {
                tail = prev;
                metrics.addMoves(1);
            }
        }
        metrics.addSteps(1);
        size--;
        return removed.value;
    }

    @Override
    public int get(int index) {
        checkIndex(index, size);
        Node node = nodeAt(index);
        metrics.addSteps(1);
        return node.value;
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

    private Node nodeAt(int index) {
        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        metrics.addSteps(index);
        return cur;
    }

    private static void checkIndex(int index, int bound) {
        if (index < 0 || index >= bound) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + bound);
        }
    }
}
