package daa.metrics;

public final class Metrics {

    private long steps;
    private long moves;
    private long comparisons;

    public void addSteps(long k) {
        steps += k;
    }

    public void addMoves(long k) {
        moves += k;
    }

    public void addComparisons(long k) {
        comparisons += k;
    }

    public long steps() {
        return steps;
    }

    public long moves() {
        return moves;
    }

    public long comparisons() {
        return comparisons;
    }

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }

    @Override
    public String toString() {
        return "steps=" + steps + ", moves=" + moves + ", comparisons=" + comparisons;
    }
}
