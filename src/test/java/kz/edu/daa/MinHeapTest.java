package kz.edu.daa;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    @Test
    void randomOperationsMatchPriorityQueue() {
        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);
        for (int i = 0; i < 5000; i++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(1000) - 500;
                heap.insert(value);
                expected.add(value);
            } else {
                assertEquals(expected.remove().intValue(), heap.extractMin());
            }
            checkProperty(heap);
            assertEquals(expected.size(), heap.size());
            if (!expected.isEmpty()) {
                assertEquals(expected.peek().intValue(), heap.peekMin());
            }
        }
    }

    @Test
    void extractionMatchesSortedArray() {
        MinHeap heap = new MinHeap();
        int[] values = new int[2000];
        Random random = new Random(42);
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
            heap.insert(values[i]);
            checkProperty(heap);
        }
        Arrays.sort(values);
        for (int value : values) {
            assertEquals(value, heap.extractMin());
            checkProperty(heap);
        }
        assertEquals(0, heap.size());
    }

    @Test
    void emptySingleDuplicateAndExtremeValues() {
        MinHeap heap = new MinHeap();
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
        heap.insert(7);
        checkProperty(heap);
        assertEquals(7, heap.peekMin());
        assertEquals(7, heap.extractMin());
        checkProperty(heap);
        int[] values = {7, 7, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for (int value : values) {
            heap.insert(value);
            checkProperty(heap);
        }
        Arrays.sort(values);
        for (int value : values) {
            assertEquals(value, heap.extractMin());
            checkProperty(heap);
        }
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void countsReadsComparisonsAndSwaps() {
        MinHeap heap = new MinHeap();
        heap.insert(3);
        heap.metrics().reset();
        heap.insert(1);
        assertEquals(7, heap.metrics().steps);
        assertEquals(2, heap.metrics().moves);
        assertEquals(1, heap.metrics().comparisons);
        heap.metrics().reset();
        assertEquals(1, heap.peekMin());
        assertEquals(1, heap.metrics().steps);
        heap.metrics().reset();
        assertEquals(1, heap.extractMin());
        assertEquals(3, heap.metrics().steps);
        assertEquals(1, heap.metrics().moves);
        assertEquals(0, heap.metrics().comparisons);
    }

    @Test
    void bottomUpBuildMatchesSortingAndOwnsItsStorage() {
        Random random = new Random(42);
        MinHeap heap = new MinHeap();
        for (int n : new int[]{0, 1, 2, 3, 4, 17, 1000}) {
            int[] input = new int[n];
            for (int i = 0; i < n; i++) {
                input[i] = random.nextInt(20) - 10;
            }
            int[] expected = input.clone();
            heap.buildHeap(input);
            assertArrayEquals(expected, input);
            Arrays.sort(expected);
            if (n > 0) {
                input[0] = Integer.MAX_VALUE;
            }
            checkProperty(heap);
            for (int value : expected) {
                assertEquals(value, heap.extractMin());
                checkProperty(heap);
            }
            heap.insert(5);
            assertEquals(5, heap.extractMin());
        }
        heap.buildHeap(new int[]{Integer.MAX_VALUE, 0, Integer.MIN_VALUE});
        assertEquals(Integer.MIN_VALUE, heap.extractMin());
        assertThrows(IllegalArgumentException.class, () -> heap.buildHeap(null));
        assertEquals(2, heap.size());
    }

    @Test
    void bottomUpBuildUsesLinearComparisons() {
        for (int n : new int[]{10, 100, 1000, 10000}) {
            int[] input = new int[n];
            for (int i = 0; i < n; i++) {
                input[i] = n - i;
            }
            MinHeap heap = new MinHeap();
            heap.buildHeap(input);
            checkProperty(heap);
            assertTrue(heap.metrics().comparisons < 2L * n);
        }
    }

    @Test
    void countsBuildReadsWritesAndKeyComparisons() {
        MinHeap heap = new MinHeap();
        heap.buildHeap(new int[]{3, 1, 2});
        assertEquals(14, heap.metrics().steps);
        assertEquals(5, heap.metrics().moves);
        assertEquals(2, heap.metrics().comparisons);
        heap.metrics().reset();
        assertEquals(1, heap.extractMin());
        assertEquals(5, heap.metrics().steps);
        assertEquals(1, heap.metrics().moves);
        assertEquals(1, heap.metrics().comparisons);
    }

    private void checkProperty(MinHeap heap) {
        int[] values = heap.snapshot();
        for (int i = 1; i < heap.size(); i++) {
            assertTrue(values[(i - 1) / 2] <= values[i]);
        }
    }
}
