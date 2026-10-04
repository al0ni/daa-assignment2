package kz.edu.daa;

public class MinHeap {
    private int[] data = new int[4];
    private int size;
    private final Metrics metrics = new Metrics();

    public void insert(int value) {
        if (size == data.length) {
            int[] larger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                larger[i] = data[i];
                metrics.steps++;
                metrics.moves++;
            }
            data = larger;
        }
        int index = size;
        data[index] = value;
        size++;
        while (index > 0) {
            int parent = (index - 1) / 2;
            metrics.steps += 2;
            metrics.comparisons++;
            if (data[parent] <= data[index]) {
                break;
            }
            swap(parent, index);
            index = parent;
        }
    }

    public int peekMin() {
        checkEmpty();
        metrics.steps++;
        return data[0];
    }

    public int extractMin() {
        checkEmpty();
        int minimum = data[0];
        metrics.steps++;
        size--;
        if (size == 0) {
            return minimum;
        }
        data[0] = data[size];
        metrics.steps++;
        metrics.moves++;
        int index = 0;
        while (2 * index + 1 < size) {
            int child = 2 * index + 1;
            int right = child + 1;
            if (right < size) {
                metrics.steps += 2;
                metrics.comparisons++;
                if (data[right] < data[child]) {
                    child = right;
                }
            }
            metrics.steps += 2;
            metrics.comparisons++;
            if (data[index] <= data[child]) {
                break;
            }
            swap(index, child);
            index = child;
        }
        return minimum;
    }

    public int size() {
        return size;
    }

    public Metrics metrics() {
        return metrics;
    }

    int[] snapshot() {
        return data.clone();
    }

    private void swap(int first, int second) {
        int temporary = data[first];
        data[first] = data[second];
        data[second] = temporary;
        metrics.steps += 2;
        metrics.moves += 2;
    }

    private void checkEmpty() {
        if (size == 0) {
            throw new IllegalStateException();
        }
    }
}
