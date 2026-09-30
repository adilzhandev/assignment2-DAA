package daa.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

abstract class IntListContractTest {

    protected abstract IntList create();

    private static void assertSameContent(List<Integer> expected, IntList actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), actual.get(i), "index " + i);
        }
    }

    @Test
    void emptyList() {
        IntList list = create();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        assertFalse(list.contains(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    @Test
    void oneElement() {
        IntList list = create();
        list.add(42);
        assertEquals(1, list.size());
        assertEquals(42, list.get(0));
        assertTrue(list.contains(42));
        assertEquals(42, list.remove(0));
        assertTrue(list.isEmpty());
        list.add(0, 7);
        assertEquals(7, list.get(0));
    }

    @Test
    void duplicateValues() {
        IntList list = create();
        for (int i = 0; i < 5; i++) {
            list.add(3);
        }
        list.add(1, 9);
        assertEquals(6, list.size());
        assertTrue(list.contains(3));
        assertEquals(3, list.remove(0));
        assertEquals(9, list.get(0));
        assertEquals(3, list.get(4));
    }

    @Test
    void firstAndLastIndex() {
        IntList list = create();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        assertEquals(0, list.get(0));
        assertEquals(9, list.get(9));
        list.add(0, -1);
        list.add(list.size(), 100);
        assertEquals(-1, list.get(0));
        assertEquals(100, list.get(list.size() - 1));
        assertEquals(100, list.remove(list.size() - 1));
        assertEquals(-1, list.remove(0));
        assertEquals(10, list.size());
        list.add(99);
        assertEquals(99, list.get(10));
    }

    @Test
    void invalidIndexThrows() {
        IntList list = create();
        list.add(1);
        list.add(2);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, 5));
        assertEquals(2, list.size());
    }

    @Test
    void matchesArrayListOnRandomOperations() {
        Random rnd = new Random(42);
        IntList list = create();
        List<Integer> expected = new ArrayList<>();
        for (int op = 0; op < 5_000; op++) {
            int kind = rnd.nextInt(5);
            int value = rnd.nextInt(100);
            if (kind == 0 || expected.isEmpty()) {
                list.add(value);
                expected.add(value);
            } else if (kind == 1) {
                int idx = rnd.nextInt(expected.size() + 1);
                list.add(idx, value);
                expected.add(idx, value);
            } else if (kind == 2) {
                int idx = rnd.nextInt(expected.size());
                assertEquals(expected.remove(idx).intValue(), list.remove(idx));
            } else if (kind == 3) {
                int idx = rnd.nextInt(expected.size());
                assertEquals(expected.get(idx).intValue(), list.get(idx));
            } else {
                assertEquals(expected.contains(value), list.contains(value));
            }
        }
        assertSameContent(expected, list);
    }

    @Test
    void countersAreNotZero() {
        IntList list = create();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        list.metrics().reset();
        list.get(50);
        assertTrue(list.metrics().steps() > 0);
        list.metrics().reset();
        list.add(50, 1);
        list.remove(50);
        assertTrue(list.metrics().moves() > 0, "insert/remove must update moves");
        list.metrics().reset();
        list.contains(-5);
        assertEquals(100, list.metrics().comparisons());
    }
}
