package kz.edu.daa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {
    @Test
    void randomOperationsMatchArrayList() {
        DynamicArrayTest.checkRandom(new MyLinkedList());
    }

    @Test
    void edgeCases() {
        DynamicArrayTest.checkEdges(new MyLinkedList());
    }

    @Test
    void countsTraversalAndLinkUpdates() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.metrics().reset();
        assertEquals(30, list.get(2));
        assertEquals(2, list.metrics().steps);
        list.metrics().reset();
        list.add(0, 5);
        assertEquals(0, list.metrics().steps);
        assertEquals(2, list.metrics().moves);
        list.metrics().reset();
        assertEquals(5, list.remove(0));
        assertEquals(1, list.metrics().steps);
        assertEquals(1, list.metrics().moves);
        list.metrics().reset();
        assertFalse(list.contains(99));
        assertEquals(3, list.metrics().steps);
        assertEquals(3, list.metrics().comparisons);
        list.metrics().reset();
        assertEquals(30, list.remove(2));
        assertEquals(2, list.metrics().steps);
        assertEquals(2, list.metrics().moves);
        list.add(40);
        assertEquals(40, list.get(2));
    }
}
