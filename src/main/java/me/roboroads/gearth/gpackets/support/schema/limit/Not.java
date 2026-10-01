package me.roboroads.gearth.gpackets.support.schema.limit;

import me.roboroads.gearth.gpackets.support.schema.IntEnum;
import me.roboroads.gearth.gpackets.support.schema.StringEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** A value that is none of {@link #values()}. Enum constants are compared by their wire value. */
public final class Not extends Limit {
    private final List<Object> values = new ArrayList<>();
    private final List<String> names = new ArrayList<>();

    Not(Object... values) {
        for (Object value : values) {
            this.values.add(wire(value));
            names.add(name(value));
        }
    }

    /** The excluded values as they appear on the wire. */
    public List<Object> values() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public Target target() {
        return Target.VALUE;
    }

    @Override
    public String describe() {
        return names.size() == 1 ? "not " + names.get(0) : "none of " + String.join(", ", names);
    }

    @Override
    public String problem(Object value) {
        for (int i = 0; i < values.size(); i++) {
            if (same(values.get(i), value)) {
                return "must not be " + names.get(i);
            }
        }
        return null;
    }

    private static Object wire(Object value) {
        if (value instanceof IntEnum) {
            return ((IntEnum) value).value();
        }
        if (value instanceof StringEnum) {
            return ((StringEnum) value).code();
        }
        return value;
    }

    private static String name(Object value) {
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return String.valueOf(value);
    }

    private static boolean same(Object excluded, Object value) {
        if (excluded instanceof Number && value instanceof Number) {
            return ((Number) excluded).longValue() == ((Number) value).longValue();
        }
        return Objects.equals(excluded, value);
    }
}
