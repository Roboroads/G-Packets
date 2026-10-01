package testfixtures.unused;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * Classes with {@link Unused} marks for the unused-lookup and packet reference tests. They live
 * outside the {@code me.roboroads.gearth.gpackets} scan root, so the packet and marker rules don't
 * gate them.
 */
public final class UnusedFixtures {
    private UnusedFixtures() {
    }

    public static class Base {
        @Unused("The client stores it but never reads it")
        @Deprecated
        public Integer legacy;
        public Integer kind;
    }

    public static class Child extends Base {
        @Unused("Only sent by old servers")
        @Deprecated
        public String extra;
        public String kept;
    }

    /** Hides {@code Base.legacy} with an unmarked field of the same name. */
    public static class Shadow extends Base {
        public Integer legacy;
    }

    public enum Mode implements IntEnum {
        ON(1),
        @Unused("The client declares it but never acts on it")
        @Deprecated
        OFF(0);

        private final int value;

        Mode(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return value;
        }
    }

    @Unused("The client's handler ignores it")
    @Deprecated
    public static class IgnoredPacket implements Packet {
        @Override
        public HPacket toPacket() {
            return null;
        }
    }
}
