package daa.bench;

import daa.ds.DynamicArray;
import daa.ds.MinHeap;
import daa.ds.MyLinkedList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.openjdk.jol.info.GraphLayout;

public final class MemoryBenchmark {

    public static final int[] SIZES = {100, 1_000, 10_000, 100_000, 1_000_000};
    public static final String HEADER = "structure,n,bytes,mb,bytes_per_element";

    private MemoryBenchmark() {
    }

    public static List<String> run() {
        List<String> rows = new ArrayList<>();
        for (int n : SIZES) {
            int[] data = Benchmark.randomData(n);
            DynamicArray array = new DynamicArray();
            MyLinkedList list = new MyLinkedList();
            MinHeap heap = new MinHeap();
            for (int x : data) {
                array.add(x);
                list.add(x);
                heap.insert(x);
            }
            rows.add(row("DynamicArray", n, GraphLayout.parseInstance(array).totalSize()));
            rows.add(row("MyLinkedList", n, GraphLayout.parseInstance(list).totalSize()));
            rows.add(row("MinHeap", n, GraphLayout.parseInstance(heap).totalSize()));
            rows.add(row("int[] (raw)", n, GraphLayout.parseInstance((Object) data).totalSize()));
        }
        return rows;
    }

    private static String row(String structure, int n, long bytes) {
        String line = String.format(Locale.ROOT, "%s,%d,%d,%.6f,%.2f",
                structure, n, bytes, bytes / (1024.0 * 1024.0), (double) bytes / n);
        System.out.println(line);
        return line;
    }

    public static void write(Path file, List<String> rows) throws IOException {
        List<String> lines = new ArrayList<>(rows.size() + 1);
        lines.add(HEADER);
        lines.addAll(rows);
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}
