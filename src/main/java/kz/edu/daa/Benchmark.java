package kz.edu.daa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static volatile long result;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("results"));
        int[] sizes = {100, 1000, 10000, 100000};
        String[] workloads = {"W1", "W2", "W3", "W3", "W4"};
        String[] variants = {"-", "-", "head", "middle", "-"};
        try (BufferedWriter out = Files.newBufferedWriter(Path.of("results/results.csv"))) {
            out.write("workload,variant,structure,n,time_ms,steps,moves,comparisons\n");
            for (int n : sizes) {
                for (int i = 0; i < workloads.length; i++) {
                    if (workloads[i].equals("W4")) {
                        out.write(run("W4", "-", "MinHeap", n));
                    } else {
                        out.write(run(workloads[i], variants[i], "DynamicArray", n));
                        out.write(run(workloads[i], variants[i], "MyLinkedList", n));
                    }
                    out.flush();
                }
            }
        }
        System.out.println("Saved results/results.csv");
    }

    private static String run(String workload, String variant, String structure, int n) {
        Random random = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt(1000000);
        }
        int[] indices = new int[10000];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = random.nextInt(n);
        }
        int[] queries = new int[1000];
        for (int i = 0; i < queries.length; i++) {
            queries[i] = i % 2 == 0 ? data[random.nextInt(n)] : -1 - random.nextInt(1000000);
        }
        long[] times = new long[5];
        long steps = 0;
        long moves = 0;
        long comparisons = 0;
        for (int repeat = -20; repeat < 5; repeat++) {
            Metrics metrics;
            long start;
            long elapsed;
            long sum = 0;
            if (workload.equals("W4")) {
                MinHeap heap = new MinHeap();
                int[] output = new int[n];
                metrics = heap.metrics();
                start = System.nanoTime();
                for (int value : data) {
                    heap.insert(value);
                }
                for (int i = 0; i < n; i++) {
                    output[i] = heap.extractMin();
                }
                elapsed = System.nanoTime() - start;
                for (int i = 1; i < n; i++) {
                    if (output[i - 1] > output[i]) {
                        throw new IllegalStateException("Heap output is not sorted");
                    }
                }
                sum = output[0] + (long) output[n - 1];
            } else {
                IntList list = structure.equals("DynamicArray") ? new DynamicArray() : new MyLinkedList();
                for (int value : data) {
                    list.add(value);
                }
                metrics = list.metrics();
                metrics.reset();
                start = System.nanoTime();
                if (workload.equals("W1")) {
                    for (int index : indices) {
                        sum += list.get(index);
                    }
                } else if (workload.equals("W2")) {
                    for (int query : queries) {
                        if (list.contains(query)) {
                            sum++;
                        }
                    }
                } else {
                    int index = variant.equals("head") ? 0 : n / 2;
                    for (int i = 0; i < 1000; i++) {
                        list.add(index, i);
                    }
                    for (int i = 0; i < 1000; i++) {
                        sum += list.remove(index);
                    }
                }
                elapsed = System.nanoTime() - start;
                if (workload.equals("W2") && sum != 500) {
                    throw new IllegalStateException("Search must find exactly 500 queries");
                }
                if (list.size() != n || (workload.equals("W3") && sum != 499500)) {
                    throw new IllegalStateException("Incorrect insert/remove result");
                }
            }
            result = sum;
            if (repeat >= 0) {
                times[repeat] = elapsed;
                if (repeat > 0 && (steps != metrics.steps || moves != metrics.moves
                        || comparisons != metrics.comparisons)) {
                    throw new IllegalStateException("Counters must be reproducible");
                }
                steps = metrics.steps;
                moves = metrics.moves;
                comparisons = metrics.comparisons;
            }
        }
        for (int i = 1; i < times.length; i++) {
            long value = times[i];
            int j = i - 1;
            while (j >= 0 && times[j] > value) {
                times[j + 1] = times[j];
                j--;
            }
            times[j + 1] = value;
        }
        String row = String.format(Locale.US, "%s,%s,%s,%d,%.6f,%d,%d,%d%n",
                workload, variant, structure, n, times[2] / 1000000.0, steps, moves, comparisons);
        System.out.print(row);
        return row;
    }
}
