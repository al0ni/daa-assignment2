package kz.edu.daa;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {
    @Test
    void randomOperationsMatchArrayList() {
        checkRandom(new DynamicArray());
    }

    @Test
    void edgeCases() {
        checkEdges(new DynamicArray());
    }

    @Test
    void countsReadsShiftsAndResizing() {
        DynamicArray list = new DynamicArray();
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        list.metrics().reset();
        list.add(9);
        assertEquals(4, list.metrics().steps);
        assertEquals(4, list.metrics().moves);
        list.metrics().reset();
        assertEquals(2, list.get(2));
        assertEquals(1, list.metrics().steps);
        list.metrics().reset();
        list.add(0, 8);
        assertEquals(5, list.metrics().steps);
        assertEquals(5, list.metrics().moves);
        list.metrics().reset();
        assertEquals(8, list.remove(0));
        assertEquals(6, list.metrics().steps);
        assertEquals(5, list.metrics().moves);
        list.metrics().reset();
        assertFalse(list.contains(-1));
        assertEquals(5, list.metrics().steps);
        assertEquals(5, list.metrics().comparisons);
    }

    static void checkEdges(IntList list) {
        assertEquals(0, list.size());
        assertFalse(list.contains(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 3));
        list.add(0, 7);
        assertEquals(7, list.get(0));
        assertEquals(7, list.remove(0));
        list.add(Integer.MIN_VALUE);
        list.add(1, Integer.MAX_VALUE);
        list.add(1, 7);
        list.add(7);
        assertEquals(Integer.MIN_VALUE, list.get(0));
        assertEquals(7, list.get(list.size() - 1));
        assertTrue(list.contains(Integer.MAX_VALUE));
        assertTrue(list.contains(7));
        assertEquals(7, list.remove(list.size() - 1));
        assertEquals(Integer.MIN_VALUE, list.remove(0));
        assertEquals(7, list.remove(0));
        assertEquals(Integer.MAX_VALUE, list.remove(0));
        list.add(5);
        assertEquals(5, list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(list.size()));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 4));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(list.size() + 1, 4));
        assertEquals(1, list.size());
    }

    static void checkRandom(IntList actual) {
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);
        for (int i = 0; i < 5000; i++) {
            int value = random.nextInt(100) - 50;
            int operation = random.nextInt(5);
            if (operation == 0 || expected.isEmpty()) {
                actual.add(value);
                expected.add(value);
            } else if (operation == 1) {
                int index = random.nextInt(expected.size() + 1);
                actual.add(index, value);
                expected.add(index, value);
            } else if (operation == 2) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index).intValue(), actual.remove(index));
            } else if (operation == 3) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.get(index).intValue(), actual.get(index));
            } else {
                assertEquals(expected.contains(value), actual.contains(value));
            }
            assertEquals(expected.size(), actual.size());
        }
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), actual.get(i));
        }
    }
}
