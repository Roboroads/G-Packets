package me.roboroads.gearth.gpackets.support.schema.limit;

/** A string of at most {@link #max()} characters. */
public final class MaxLength extends Limit {
    private final int max;

    MaxLength(int max) {
        this.max = max;
    }

    public int max() {
        return max;
    }

    @Override
    public Target target() {
        return Target.STRING;
    }

    @Override
    public String describe() {
        return "at most " + max + " characters";
    }

    @Override
    public String problem(Object value) {
        int length = ((String) value).length();
        return length <= max ? null : describe() + ", got " + length;
    }
}
