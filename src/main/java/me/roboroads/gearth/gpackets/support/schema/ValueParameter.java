package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** One primitive value on the wire, optionally mapped to an enum. */
public final class ValueParameter extends Parameter {
    private final WireType wireType;
    private final Class<? extends Enum<?>> enumType;

    ValueParameter(String name, WireType wireType, Class<? extends Enum<?>> enumType) {
        super(name);
        this.wireType = wireType;
        this.enumType = enumType;
    }

    /** How the value is encoded on the wire. */
    public WireType wireType() {
        return wireType;
    }

    /** The enum the wire value maps to, or null for a plain value. */
    public Class<? extends Enum<?>> enumType() {
        return enumType;
    }

    /** Each enum constant's name with its wire value, in declaration order. Empty for a plain value. */
    public Map<String, Object> enumOptions() {
        Map<String, Object> options = new LinkedHashMap<>();
        if (enumType != null) {
            for (Enum<?> constant : enumType.getEnumConstants()) {
                options.put(constant.name(), wireValue(constant));
            }
        }
        return Collections.unmodifiableMap(options);
    }

    /**
     * The enum constants the current client ignores, by name, with the reason from their
     * {@link Unused}, in declaration order. Empty for a plain value or when the client uses every
     * constant.
     */
    public Map<String, String> unusedOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        if (enumType != null) {
            for (Enum<?> constant : enumType.getEnumConstants()) {
                Unused unused;
                try {
                    unused = enumType.getField(constant.name()).getAnnotation(Unused.class);
                } catch (NoSuchFieldException e) {
                    throw new AssertionError(constant.name() + " is a constant of " + enumType.getName(), e);
                }
                if (unused != null) {
                    options.put(constant.name(), unused.value());
                }
            }
        }
        return Collections.unmodifiableMap(options);
    }

    @Override
    void read(HPacket packet, Map<String, Object> values, String path) {
        values.put(name(), readWire(wireType, packet, path + "." + name()));
    }

    @Override
    void write(Map<String, Object> values, HPacket packet, String path) {
        wireType.write(packet, coerce(values.get(name()), path + "." + name()));
    }

    @Override
    void check(Map<String, Object> values, String path, List<Violation> out) {
        if (limits().isEmpty()) {
            return;
        }
        String here = at(path, name());
        Object value = coerce(values.get(name()), here);
        for (Limit limit : limits()) {
            String problem = limit.checked() ? limit.problem(value) : null;
            if (problem != null) {
                out.add(new Violation(here, problem, limit, null));
            }
        }
    }

    /**
     * Converts a value to its wire form: an enum constant to its wire value, null to the default,
     * any number to this parameter's numeric type.
     *
     * @param where the parameter's path, for the error message
     */
    Object coerce(Object value, String where) {
        if (value instanceof Enum) {
            if (enumType == null || !enumType.isInstance(value)) {
                throw new IllegalArgumentException(where + ": expected " + wireType + ", got " + value.getClass().getName());
            }
            value = wireValue((Enum<?>) value);
        }
        try {
            return wireType.coerce(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(where + ": " + e.getMessage());
        }
    }

    /** The enum constant with this wire value, or null if none has it. String codes match ignoring case. */
    Object toEnum(Object raw) {
        for (Enum<?> constant : enumType.getEnumConstants()) {
            Object value = wireValue(constant);
            boolean match = value instanceof String
                    ? raw instanceof String && ((String) value).equalsIgnoreCase((String) raw)
                    : raw instanceof Number && ((Number) raw).intValue() == (Integer) value;
            if (match) {
                return constant;
            }
        }
        return null;
    }

    static Object wireValue(Enum<?> constant) {
        if (constant instanceof IntEnum) {
            return ((IntEnum) constant).value();
        }
        return ((StringEnum) constant).code();
    }
}
