package me.roboroads.gearth.gpackets.support.schema.limit;

import java.util.List;

/** A list of at most {@link #max()} items. */
public final class MaxSize extends Limit {
    private final int max;

    MaxSize(int max) {
        this.max = max;
    }

    public int max() {
        return max;
    }

    @Override
    public Target target() {
        return Target.LIST;
    }

    @Override
    public String describe() {
        return "at most " + max + " items";
    }

    @Override
    public String problem(Object value) {
        int size = ((List<?>) value).size();
        return size <= max ? null : describe() + ", got " + size;
    }
}
