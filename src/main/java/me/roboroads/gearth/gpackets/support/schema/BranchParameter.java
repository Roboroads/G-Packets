package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HPacket;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Parameters chosen by the value of an earlier {@link ValueParameter}, the discriminator. The chosen
 * case's values sit in the same map as the values around the branch.
 */
public final class BranchParameter extends Parameter {
    private final ValueParameter discriminator;
    private final boolean exhaustive;
    private final Map<Object, Case> cases;

    BranchParameter(ValueParameter discriminator, boolean exhaustive, Map<Object, Case> cases) {
        super(null);
        this.discriminator = discriminator;
        this.exhaustive = exhaustive;
        this.cases = Collections.unmodifiableMap(new LinkedHashMap<>(cases));
    }

    /** The name of the earlier value whose wire value picks the case. */
    public String on() {
        return discriminator.name();
    }

    /**
     * True for {@code branch(...)}: a value without a case is an error. False for
     * {@code when(...)}: a value without a case adds no parameters.
     */
    public boolean exhaustive() {
        return exhaustive;
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
        Case match = cases.get(discriminator.coerce(value, where));
        if (match == null && exhaustive) {
            throw new IllegalArgumentException(where + ": no case for value " + value);
        }
        return match;
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
