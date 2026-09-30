package daa.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.PriorityQueue;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MinHeapTest {

    @Test
    void emptyHeapThrows() {
        MinHeap h = new MinHeap();
        assertTrue(h.isEmpty());
        assertThrows(IllegalStateException.class, h::peekMin);
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void oneElement() {
        MinHeap h = new MinHeap();
        h.insert(5);
        assertEquals(5, h.peekMin());
        assertEquals(1, h.size());
        assertEquals(5, h.extractMin());
        assertTrue(h.isEmpty());
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void duplicateValues() {
        MinHeap h = new MinHeap(2);
        int[] values = {4, 1, 4, 1, 1, 9, 4};
        for (int v : values) {
            h.insert(v);
        }
        int[] expected = {1, 1, 1, 4, 4, 4, 9};
        for (int e : expected) {
            assertEquals(e, h.extractMin());
        }
    }

    @Test
    void heapPropertyHoldsAfterEveryOperation() {
        Random rnd = new Random(42);
        MinHeap h = new MinHeap();
        for (int i = 0; i < 2_000; i++) {
            h.insert(rnd.nextInt(500) - 250);
            assertTrue(h.isValidHeap(), "after insert #" + i);
        }
        while (!h.isEmpty()) {
            h.extractMin();
            assertTrue(h.isValidHeap(), "after extractMin, size " + h.size());
        }
    }

    @Test
    void extractMinReturnsNonDecreasingOrder() {
        Random rnd = new Random(7);
        int n = 10_000;
        MinHeap h = new MinHeap();
        for (int i = 0; i < n; i++) {
            h.insert(rnd.nextInt());
        }
        int prev = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int cur = h.extractMin();
            assertTrue(prev <= cur, "order broken at " + i);
            prev = cur;
        }
    }

    @Test
    void matchesPriorityQueueOnMixedOperations() {
        Random rnd = new Random(42);
        MinHeap h = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        for (int op = 0; op < 5_000; op++) {
            if (expected.isEmpty() || rnd.nextInt(3) != 0) {
                int v = rnd.nextInt(1000);
                h.insert(v);
                expected.add(v);
            } else {
                assertEquals(expected.peek().intValue(), h.peekMin());
                assertEquals(expected.poll().intValue(), h.extractMin());
            }
            assertEquals(expected.size(), h.size());
        }
    }

    @Test
    void countersCountComparisons() {
        MinHeap h = new MinHeap();
        for (int i = 100; i > 0; i--) {
            h.insert(i);
        }
        assertTrue(h.metrics().comparisons() > 0);
        assertTrue(h.metrics().moves() > 0);
    }

    @Test
    void buildHeapProducesValidHeap() {
        Random rnd = new Random(42);
        int[] data = new int[5_000];
        for (int i = 0; i < data.length; i++) {
            data[i] = rnd.nextInt(1000);
        }
        MinHeap h = new MinHeap();
        h.buildHeap(data);
        assertTrue(h.isValidHeap());
        assertEquals(data.length, h.size());
        int prev = Integer.MIN_VALUE;
        while (!h.isEmpty()) {
            int cur = h.extractMin();
            assertTrue(prev <= cur);
            prev = cur;
        }
    }

    @Test
    void buildHeapUsesFewerComparisonsThanInserts() {
        int n = 100_000;
        Random rnd = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rnd.nextInt();
        }
        MinHeap floyd = new MinHeap();
        floyd.buildHeap(data);
        assertTrue(floyd.metrics().comparisons() < 2L * n, "Floyd must be linear");

        MinHeap byInsert = new MinHeap();
        java.util.Arrays.sort(data);
        for (int i = n - 1; i >= 0; i--) {
            byInsert.insert(data[i]);
        }
        assertTrue(byInsert.metrics().comparisons() > floyd.metrics().comparisons());
    }

    @Test
    void buildHeapOnEmptyArray() {
        MinHeap h = new MinHeap();
        h.buildHeap(new int[0]);
        assertTrue(h.isEmpty());
        h.insert(3);
        assertEquals(3, h.peekMin());
    }
}
