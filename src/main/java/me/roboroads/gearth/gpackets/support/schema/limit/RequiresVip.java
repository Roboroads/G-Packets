package me.roboroads.gearth.gpackets.support.schema.limit;

/**
 * A setting the client only lets VIP users change. It's described, not checked: whether the user
 * is VIP isn't in the packet, and the client still sends the stored value for other users.
 */
public final class RequiresVip extends Limit {

    RequiresVip() {
    }

    @Override
    public Target target() {
        return Target.ANY;
    }

    @Override
    public String describe() {
        return "VIP only";
    }

    @Override
    public boolean checked() {
        return false;
    }

    @Override
    public String problem(Object value) {
        return null;
    }
}
