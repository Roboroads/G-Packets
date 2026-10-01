package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** One primitive value on the wire, optionally mapped to an enum or an {@link OpenIntEnum}. */
public final class ValueParameter extends Parameter {
    private final WireType wireType;
    private final Class<?> enumType;

    ValueParameter(String name, WireType wireType, Class<?> enumType) {
        super(name);
        this.wireType = wireType;
        this.enumType = enumType;
    }

    /** How the value is encoded on the wire. */
    public WireType wireType() {
        return wireType;
    }

    /** The enum or {@link OpenIntEnum} the wire value maps to, or null for a plain value. */
    public Class<?> enumType() {
        return enumType;
    }

    /** True when {@link #enumType()} is an {@link OpenIntEnum}, so ids it doesn't name pass through. */
    public boolean openEnum() {
        return enumType != null && OpenIntEnum.class.isAssignableFrom(enumType);
    }

    /**
     * Each named constant's name with its wire value: an enum's in declaration order, an open
     * enum's sorted by value. Empty for a plain value.
     */
    public Map<String, Object> enumOptions() {
        Map<String, Object> options = new LinkedHashMap<>();
        for (Map.Entry<String, Object> constant : constants().entrySet()) {
            options.put(constant.getKey(), wireValue(constant.getValue()));
        }
        return Collections.unmodifiableMap(options);
    }

    /**
     * The named constants the current client ignores, by name, with the reason from their
     * {@link Unused}, in {@link #enumOptions()} order. Empty for a plain value or when the client
     * uses every constant.
     */
    public Map<String, String> unusedOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        for (String name : constants().keySet()) {
            Unused unused;
            try {
                unused = enumType.getField(name).getAnnotation(Unused.class);
            } catch (NoSuchFieldException e) {
                throw new AssertionError(name + " is a constant of " + enumType.getName(), e);
            }
            if (unused != null) {
                options.put(name, unused.value());
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
     * Converts a value to its wire form: an enum or open-enum constant to its wire value, null to
     * the default, any number to this parameter's numeric type.
     *
     * @param where the parameter's path, for the error message
     */
    Object coerce(Object value, String where) {
        if (value instanceof Enum || value instanceof OpenIntEnum) {
            if (enumType == null || !enumType.isInstance(value)) {
                throw new IllegalArgumentException(where + ": expected " + wireType + ", got " + value.getClass().getName());
            }
            value = wireValue(value);
        }
        try {
            return wireType.coerce(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(where + ": " + e.getMessage());
        }
    }

    /**
     * The constant with this wire value. A real enum gives null when no constant has it (string
     * codes match ignoring case); an open enum keeps any id.
     */
    Object toEnum(Object raw) {
        if (openEnum()) {
            return raw instanceof Number ? OpenIntEnum.fromWire(enumType, ((Number) raw).intValue()) : null;
        }
        for (Object constant : enumType.getEnumConstants()) {
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

    /** An {@link IntEnum}'s value or a {@link StringEnum}'s code. */
    static Object wireValue(Object constant) {
        if (constant instanceof IntEnum) {
            return ((IntEnum) constant).value();
        }
        return ((StringEnum) constant).code();
    }

    /** The named constants by name. Empty for a plain value. */
    private Map<String, Object> constants() {
        Map<String, Object> constants = new LinkedHashMap<>();
        if (openEnum()) {
            constants.putAll(OpenIntEnum.constants(enumType));
        } else if (enumType != null) {
            for (Object constant : enumType.getEnumConstants()) {
                constants.put(((Enum<?>) constant).name(), constant);
            }
        }
        return constants;
    }
}
