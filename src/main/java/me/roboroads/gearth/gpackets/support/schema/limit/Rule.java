package me.roboroads.gearth.gpackets.support.schema.limit;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/** A limit across several parameters of one schema, declared with {@code Schema.rule}. */
public final class Rule {
    private final String description;
    private final Predicate<Map<String, Object>> check;

    public Rule(String description, Predicate<Map<String, Object>> check) {
        this.description = Objects.requireNonNull(description, "description");
        this.check = Objects.requireNonNull(check, "check");
    }

    /** The rule in words. */
    public String description() {
        return description;
    }

    /**
     * Whether the values keep this rule. The schema passes its values as they would be written:
     * a missing value as its wire default and an enum constant as its wire value.
     */
    public boolean holds(Map<String, Object> values) {
        return check.test(values);
    }

    @Override
    public String toString() {
        return description;
    }
}
