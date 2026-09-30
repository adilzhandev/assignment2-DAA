package daa.metrics;

import java.util.Locale;

public record Result(String workload, String variant, String structure, int n,
                     double timeMs, long steps, long moves, long comparisons) {

    public static final String HEADER = "workload,variant,structure,n,time_ms,steps,moves,comparisons";

    public String toCsvRow() {
        return String.format(Locale.ROOT, "%s,%s,%s,%d,%.4f,%d,%d,%d",
                workload, variant, structure, n, timeMs, steps, moves, comparisons);
    }
}
