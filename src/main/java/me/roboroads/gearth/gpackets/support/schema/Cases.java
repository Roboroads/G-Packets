package me.roboroads.gearth.gpackets.support.schema;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

/** The cases of a {@link Schema#branch} call, added with {@link #on}. */
public final class Cases {
    private final ValueParameter discriminator;
    private final Schema<?> parent;
    private final Class<?> base;
    private final Map<Object, BranchParameter.Case> cases = new LinkedHashMap<>();

    Cases(ValueParameter discriminator, Schema<?> parent) {
        this.discriminator = discriminator;
        this.parent = parent;
        this.base = parent.type();
    }

    /**
     * Adds a case: when the discriminator is {@code value} (an enum constant or its wire value), the
     * object is a {@code subclass} and the parameters {@code body} adds follow.
     */
    public <S> Cases on(Object value, Class<S> subclass, UnaryOperator<Schema<S>> body) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(subclass, "subclass");
        Objects.requireNonNull(body, "body");
        String where = base.getSimpleName() + "." + discriminator.name();
        if (!base.isAssignableFrom(subclass)) {
            throw new IllegalArgumentException(where + ": " + subclass.getName() + " does not extend " + base.getName());
        }
        Object key = discriminator.coerce(value, where);
        if (cases.containsKey(key)) {
            throw new IllegalArgumentException(where + ": duplicate case for value " + key);
        }
        cases.put(key, new BranchParameter.Case(key, subclass, body.apply(parent.nested(subclass))));
        return this;
    }

    Map<Object, BranchParameter.Case> cases() {
        return cases;
    }
}
