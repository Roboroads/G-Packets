package me.roboroads.gearth.gpackets.support.schema.limit;

/** A number from {@link #min()} to {@link #max()}, both included, and also 0 when {@link #allowsZero()}. */
public final class Range extends Limit {
    private final long min;
    private final long max;
    private final boolean allowsZero;

    Range(long min, long max, boolean allowsZero) {
        this.min = min;
        this.max = max;
        this.allowsZero = allowsZero;
    }

    public long min() {
        return min;
    }

    public long max() {
        return max;
    }

    /** Whether 0 is allowed too, for a setting the client sends as 0 when it's switched off. */
    public boolean allowsZero() {
        return allowsZero;
    }

    /** The same range that also allows 0. */
    public Range orZero() {
        return new Range(min, max, true);
    }

    @Override
    public Target target() {
        return Target.NUMBER;
    }

    @Override
    public String describe() {
        return min + " to " + max + (allowsZero ? ", or 0" : "");
    }

    @Override
    public String problem(Object value) {
        long number = ((Number) value).longValue();
        boolean fits = (number >= min && number <= max) || (allowsZero && number == 0);
        return fits ? null : "must be " + describe() + ", got " + number;
    }
}
