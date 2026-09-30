package daa;

import daa.bench.Benchmark;
import daa.metrics.CsvWriter;
import daa.metrics.Result;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class Main {

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length > 0 ? args[0] : "results/results.csv");
        Path buildHeapOutput = output.resolveSibling("build_heap.csv");

        Benchmark benchmark = new Benchmark();
        List<Result> results = benchmark.run();
        CsvWriter.write(output, results);
        CsvWriter.write(buildHeapOutput, benchmark.buildHeapResults());

        System.out.println();
        System.out.println("Saved " + results.size() + " rows to " + output.toAbsolutePath());
        System.out.println("Saved " + benchmark.buildHeapResults().size() + " rows to " + buildHeapOutput.toAbsolutePath());

    }
}
