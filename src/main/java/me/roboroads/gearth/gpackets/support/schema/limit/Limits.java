package me.roboroads.gearth.gpackets.support.schema.limit;

/**
 * Factories for the limits a schema parameter can carry, meant for a static import:
 * {@code .string("name", maxLength(60))}.
 */
public final class Limits {

    private Limits() {
    }

    /** A string of at most {@code max} characters. */
    public static MaxLength maxLength(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("maxLength must not be negative, got " + max);
        }
        return new MaxLength(max);
    }

    /** A string with at least one character. */
    public static NotEmpty notEmpty() {
        return new NotEmpty();
    }

    /** A number from {@code min} to {@code max}, both included. Add {@link Range#orZero()} to allow 0 too. */
    public static Range range(long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException("range needs min <= max, got " + min + " to " + max);
        }
        return new Range(min, max, false);
    }

    /** A list of at most {@code max} items. */
    public static MaxSize maxSize(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("maxSize must not be negative, got " + max);
        }
        return new MaxSize(max);
    }

    /** A list of values where every item keeps {@code limit}, a limit for a single value. */
    public static Each each(Limit limit) {
        Limit.Target target = limit.target();
        if (target != Limit.Target.STRING && target != Limit.Target.NUMBER && target != Limit.Target.VALUE) {
            throw new IllegalArgumentException("each needs a limit for a single value, got " + limit.describe());
        }
        return new Each(limit);
    }

    /** A value that is none of {@code values}: wire values or enum constants. */
    public static Not not(Object... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("not needs at least one value");
        }
        return new Not(values);
    }

    /** A setting the client only lets VIP users change. Described only, never checked. */
    public static RequiresVip requiresVip() {
        return new RequiresVip();
    }
}
