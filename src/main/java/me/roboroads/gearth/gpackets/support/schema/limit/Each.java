package me.roboroads.gearth.gpackets.support.schema.limit;

import java.util.List;

/** A list of values where every item keeps {@link #limit()}. */
public final class Each extends Limit {
    private final Limit limit;

    Each(Limit limit) {
        this.limit = limit;
    }

    /** The limit every item keeps. */
    public Limit limit() {
        return limit;
    }

    @Override
    public Target target() {
        return Target.EACH;
    }

    @Override
    public String describe() {
        return "each " + limit.describe();
    }

    @Override
    public String problem(Object value) {
        int i = 0;
        for (Object item : (List<?>) value) {
            String problem = limit.problem(item);
            if (problem != null) {
                return "item " + i + ": " + problem;
            }
            i++;
        }
        return null;
    }
}
