package me.roboroads.gearth.gpackets.support.schema.limit;

/** A string with at least one character. */
public final class NotEmpty extends Limit {

    NotEmpty() {
    }

    @Override
    public Target target() {
        return Target.STRING;
    }

    @Override
    public String describe() {
        return "not empty";
    }

    @Override
    public String problem(Object value) {
        return ((String) value).isEmpty() ? "must not be empty" : null;
    }
}
