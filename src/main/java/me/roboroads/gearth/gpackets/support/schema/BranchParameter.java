package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parameters chosen by the value of an earlier {@link ValueParameter}, the discriminator. The chosen
 * case's values sit in the same map as the values around the branch.
 */
public final class BranchParameter extends Parameter {
    private final ValueParameter discriminator;
    private final Integer mask;
    private final boolean exhaustive;
    private final boolean negated;
    private final Map<Object, Case> cases;

    BranchParameter(ValueParameter discriminator, Integer mask, boolean exhaustive, boolean negated, Map<Object, Case> cases) {
        super(null);
        this.discriminator = discriminator;
        this.mask = mask;
        this.exhaustive = exhaustive;
        this.negated = negated;
        this.cases = Collections.unmodifiableMap(new LinkedHashMap<>(cases));
        if (negated && (exhaustive || this.cases.size() != 1)) {
            throw new IllegalArgumentException(discriminator.name() + ": only a when can be negated");
        }
        if (mask != null) {
            String where = discriminator.name();
            if (discriminator.enumType() != null || !isWhole(discriminator.wireType())) {
                throw new IllegalArgumentException(where + ": only a plain byte, short, int or long can be masked");
            }
            for (Object value : this.cases.keySet()) {
                if ((((Number) value).longValue() & ~mask.longValue()) != 0) {
                    throw new IllegalArgumentException(where + ": case " + value + " has bits outside the mask " + mask);
                }
            }
        }
    }

    /** The name of the earlier value whose wire value picks the case. */
    public String on() {
        return discriminator.name();
    }

    /**
     * The bits of the discriminator that pick the case, or null when the whole value does. With a
     * mask, a case matches when {@code value & mask} equals its value, and the other bits are ignored.
     */
    public Integer mask() {
        return mask;
    }

    /**
     * True for {@code branch(...)}: a value without a case is an error. False for
     * {@code when(...)} and {@code whenOneOf(...)}: a value without a case adds no parameters. The
     * cases of a {@code whenOneOf(...)} share one schema.
     */
    public boolean exhaustive() {
        return exhaustive;
    }

    /**
     * True for {@code whenNot(...)}: its one case's parameters follow when the discriminator is
     * anything but the case's value.
     */
    public boolean negated() {
        return negated;
    }

    /** The cases by wire value, in declaration order. */
    public Map<Object, Case> cases() {
        return cases;
    }

    /** The case for a discriminator value, or null when a non-exhaustive branch has none. */
    Case caseFor(Object value, String path) {
        String where = path + "." + discriminator.name();
        if (value == null && exhaustive) {
            throw new IllegalArgumentException(where + ": is null");
        }
        Object key = discriminator.coerce(value, where);
        if (mask != null) {
            key = discriminator.coerce(((Number) key).longValue() & mask, where);
        }
        if (negated) {
            Case only = cases.values().iterator().next();
            return only.value().equals(key) ? null : only;
        }
        Case match = cases.get(key);
        if (match == null && exhaustive) {
            throw new IllegalArgumentException(where + ": no case for value " + value + (mask == null ? "" : " (" + key + " after the mask)"));
        }
        return match;
    }

    private static boolean isWhole(WireType wireType) {
        return wireType == WireType.BYTE || wireType == WireType.SHORT || wireType == WireType.INT || wireType == WireType.LONG;
    }

    @Override
    void read(HPacket packet, Map<String, Object> values, String path) {
        Case match = caseFor(values.get(discriminator.name()), path);
        if (match != null) {
            match.schema().readInto(packet, values, path);
        }
    }

    @Override
    void write(Map<String, Object> values, HPacket packet, String path) {
        Case match = caseFor(values.get(discriminator.name()), path);
        if (match != null) {
            match.schema().writeFrom(values, packet, path);
        }
    }

    @Override
    void check(Map<String, Object> values, String path, List<Violation> out) {
        Case match = caseFor(values.get(discriminator.name()), path);
        if (match != null) {
            match.schema().checkInto(values, path, out);
        }
    }

    /** One case: the wire value that selects it, the subclass it means, and the parameters that follow. */
    public static final class Case {
        private final Object value;
        private final Class<?> subclass;
        private final Schema<?> schema;

        Case(Object value, Class<?> subclass, Schema<?> schema) {
            this.value = value;
            this.subclass = subclass;
            this.schema = schema;
        }

        /** The discriminator's wire value for this case. */
        public Object value() {
            return value;
        }

        /** The class the object is in this case, or null for a {@code when(...)} case. */
        public Class<?> subclass() {
            return subclass;
        }

        /** The parameters that follow in this case. */
        public Schema<?> schema() {
            return schema;
        }
    }
}
