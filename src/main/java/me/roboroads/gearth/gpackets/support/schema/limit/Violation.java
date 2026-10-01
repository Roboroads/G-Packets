package me.roboroads.gearth.gpackets.support.schema.limit;

/** One broken limit or rule: where, what's wrong, and which limit or rule it broke. */
public final class Violation {
    private final String path;
    private final String message;
    private final Limit limit;
    private final Rule rule;

    public Violation(String path, String message, Limit limit, Rule rule) {
        this.path = path;
        this.message = message;
        this.limit = limit;
        this.rule = rule;
    }

    /** The parameter's path in the packet, such as {@code items[1].label}, or the structure's for a rule. */
    public String path() {
        return path;
    }

    /** What's wrong, for example "at most 60 characters, got 72". */
    public String message() {
        return message;
    }

    /** The broken limit, or null for a rule. */
    public Limit limit() {
        return limit;
    }

    /** The broken rule, or null for a limit. */
    public Rule rule() {
        return rule;
    }

    @Override
    public String toString() {
        return path + ": " + message;
    }
}
