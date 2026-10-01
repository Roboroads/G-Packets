package me.roboroads.gearth.gpackets.support.schema.limit;

/**
 * A limit the Habbo client keeps a parameter within. Declare limits in a schema with the
 * factories in {@link Limits}; writing an outgoing packet checks them.
 */
public abstract class Limit {

    /** What a limit can be declared on. */
    public enum Target {
        /** A string value that isn't an enum. */
        STRING,
        /** An int, short, long or byte value that isn't an enum. */
        NUMBER,
        /** Any value, including enum values. */
        VALUE,
        /** A list. */
        LIST,
        /** A list of values; the wrapped limit applies to every element. */
        EACH,
        /** Any parameter. */
        ANY
    }

    Limit() {
    }

    /** What can carry this limit. */
    public abstract Target target();

    /** The limit in words, for example "at most 60 characters". */
    public abstract String describe();

    /** Whether writing an outgoing packet checks this limit. False for a limit that only describes. */
    public boolean checked() {
        return true;
    }

    /**
     * What's wrong with {@code value}, or null when it fits. {@code value} is what would be
     * written: a String, Integer, Short, Long, Byte or Boolean for a value, a List for a list.
     */
    public abstract String problem(Object value);

    @Override
    public String toString() {
        return describe();
    }
}
