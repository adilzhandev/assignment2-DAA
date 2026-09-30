package daa.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MetricsTest {

    @Test
    void countersStartAtZeroAndAccumulate() {
        Metrics m = new Metrics();
        assertEquals(0, m.steps());
        m.addSteps(3);
        m.addSteps(2);
        m.addMoves(7);
        m.addComparisons(1);
        assertEquals(5, m.steps());
        assertEquals(7, m.moves());
        assertEquals(1, m.comparisons());
    }

    @Test
    void resetClearsAllCounters() {
        Metrics m = new Metrics();
        m.addSteps(1);
        m.addMoves(1);
        m.addComparisons(1);
        m.reset();
        assertEquals(0, m.steps());
        assertEquals(0, m.moves());
        assertEquals(0, m.comparisons());
    }

    @Test
    void csvRowKeepsFractionalMilliseconds() {
        Result r = new Result("W1_random_access", "-", "DynamicArray", 100, 0.04251, 10, 0, 0);
        assertEquals("W1_random_access,-,DynamicArray,100,0.0425,10,0,0", r.toCsvRow());
    }

    @Test
    void csvWriterWritesHeaderAndRows(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("sub/results.csv");
        CsvWriter.write(file, List.of(new Result("W2_search", "-", "MyLinkedList", 1000, 1.5, 1, 2, 3)));
        List<String> lines = Files.readAllLines(file);
        assertEquals(Result.HEADER, lines.get(0));
        assertTrue(lines.get(1).startsWith("W2_search,-,MyLinkedList,1000,1.5000,"));
    }
}
