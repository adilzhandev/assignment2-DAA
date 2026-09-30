package daa.metrics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvWriter {

    private CsvWriter() {
    }

    public static void write(Path file, List<Result> results) throws IOException {
        List<String> lines = new ArrayList<>(results.size() + 1);
        lines.add(Result.HEADER);
        for (Result r : results) {
            lines.add(r.toCsvRow());
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}
