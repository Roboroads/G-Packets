package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One entry in a {@link Schema}. The kinds are {@link ValueParameter}, {@link ListParameter},
 * {@link StructParameter}, {@link BranchParameter} and {@link OptionalParameter}.
 */
public abstract class Parameter {
    private final String name;
    private String unused;
    private List<Limit> limits = Collections.emptyList();

    Parameter(String name) {
        this.name = name;
    }

    /**
     * The key of this parameter's value, or null for a branch or optional, whose parameters sit in
     * the enclosing values.
     */
    public String name() {
        return name;
    }

    /**
     * Why the current client ignores this parameter, from the {@link Unused} on its field, or null
     * when the client uses it. Branches and optionals have no field and return null.
     */
    public String unused() {
        return unused;
    }

    /** Called once by the schema that adds this parameter, before anything else can see it. */
    void markUnused(String reason) {
        this.unused = reason;
    }

    /** The limits the client keeps this parameter within, in the order they were declared. */
    public List<Limit> limits() {
        return limits;
    }

    /** Called once by the schema that adds this parameter, before anything else can see it. */
    void limits(List<Limit> limits) {
        this.limits = Collections.unmodifiableList(new ArrayList<>(limits));
    }

    /** Adds the limits this parameter breaks in {@code values} to {@code out}. {@code path} is relative to the packet. */
    abstract void check(Map<String, Object> values, String path, List<Violation> out);

    /** The path of {@code name} inside {@code path}, which is empty at the packet's root. */
    static String at(String path, String name) {
        return path.isEmpty() ? name : path + "." + name;
    }

    /** Reads this parameter into {@code values}. {@code path} names the enclosing values in errors. */
    abstract void read(HPacket packet, Map<String, Object> values, String path);

    /** Writes this parameter from {@code values}. {@code path} names the enclosing values in errors. */
    abstract void write(Map<String, Object> values, HPacket packet, String path);

    static Object readWire(WireType type, HPacket packet, String path) {
        try {
            return type.read(packet);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(path + ": packet ended before this " + type + " could be read", e);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> asValues(Object value, String path) {
        if (value == null) {
            return new LinkedHashMap<>();
        }
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        throw new IllegalArgumentException(path + ": expected a Map, got " + value.getClass().getName());
    }
}
