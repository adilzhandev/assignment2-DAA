package daa.ds;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MyLinkedListTest extends IntListContractTest {

    @Override
    protected IntList create() {
        return new MyLinkedList();
    }

    @Test
    void getWalksIndexSteps() {
        MyLinkedList list = new MyLinkedList();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        list.metrics().reset();
        assertEquals(500, list.get(500));
        assertEquals(501, list.metrics().steps());
    }

    @Test
    void headInsertAndRemoveCostConstantMoves() {
        MyLinkedList list = new MyLinkedList();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        list.metrics().reset();
        list.add(0, -1);
        list.remove(0);
        assertEquals(3, list.metrics().moves());
        assertEquals(1, list.metrics().steps());
    }

    @Test
    void tailIsUpdatedAfterRemovingLast() {
        MyLinkedList list = new MyLinkedList();
        list.add(1);
        list.add(2);
        list.remove(1);
        list.add(3);
        assertEquals(3, list.get(1));
        list.remove(0);
        list.remove(0);
        list.add(4);
        assertEquals(4, list.get(0));
        assertEquals(1, list.size());
    }
}
