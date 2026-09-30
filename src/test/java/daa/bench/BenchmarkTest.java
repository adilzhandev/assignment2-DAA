package daa.bench;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BenchmarkTest {

    @Test
    void medianOfOddAndEvenCount() {
        assertEquals(3.0, Benchmark.median(new double[]{5, 1, 3, 9, 2}));
        assertEquals(2.5, Benchmark.median(new double[]{4, 1, 2, 3}));
    }

    @Test
    void dataIsReproducibleWithSeed42() {
        assertArrayEquals(Benchmark.randomData(1000), Benchmark.randomData(1000));
    }
}
