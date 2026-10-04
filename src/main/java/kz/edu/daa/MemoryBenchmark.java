package kz.edu.daa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

public class MemoryBenchmark {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Files.createDirectories(Path.of("results"));
        String layouts = VM.current().details()
                + ClassLayout.parseClass(Class.forName("kz.edu.daa.MyLinkedList$Node")).toPrintable()
                + ClassLayout.parseClass(DynamicArray.class).toPrintable()
                + ClassLayout.parseClass(MyLinkedList.class).toPrintable()
                + ClassLayout.parseClass(MinHeap.class).toPrintable()
                + ClassLayout.parseClass(Metrics.class).toPrintable();
        Files.writeString(Path.of("results/memory_layout.txt"), layouts);
        try (BufferedWriter out = Files.newBufferedWriter(Path.of("results/memory.csv"))) {
            out.write("structure,n,bytes,objects\n");
            for (int n : new int[]{100, 1000, 10000, 100000}) {
                DynamicArray array = new DynamicArray();
                MyLinkedList list = new MyLinkedList();
                MinHeap heap = new MinHeap();
                for (int i = 0; i < n; i++) {
                    array.add(i);
                    list.add(i);
                    heap.insert(i);
                }
                write(out, "DynamicArray", n, array);
                write(out, "MyLinkedList", n, list);
                write(out, "MinHeap", n, heap);
            }
        }
        System.out.println("Saved memory measurements and object layouts");
    }

    private static void write(BufferedWriter out, String name, int n, Object structure) throws IOException {
        GraphLayout graph = GraphLayout.parseInstance(structure);
        out.write(name + "," + n + "," + graph.totalSize() + "," + graph.totalCount() + "\n");
    }
}
