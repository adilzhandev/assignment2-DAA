package daa.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DynamicArrayTest extends IntListContractTest {

    @Override
    protected IntList create() {
        return new DynamicArray();
    }

    @Test
    void capacityDoublesWhenFull() {
        DynamicArray a = new DynamicArray(2);
        a.add(1);
        a.add(2);
        assertEquals(2, a.capacity());
        a.add(3);
        assertEquals(4, a.capacity());
        a.add(4);
        a.add(5);
        assertEquals(8, a.capacity());
    }

    @Test
    void getCostsOneStep() {
        DynamicArray a = new DynamicArray();
        for (int i = 0; i < 1000; i++) {
            a.add(i);
        }
        a.metrics().reset();
        a.get(999);
        assertEquals(1, a.metrics().steps());
    }

    @Test
    void insertAtHeadShiftsAllElements() {
        DynamicArray a = new DynamicArray(64);
        for (int i = 0; i < 10; i++) {
            a.add(i);
        }
        a.metrics().reset();
        a.add(0, -1);
        assertEquals(10, a.metrics().moves());
    }

    @Test
    void invalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray(0));
    }
}
