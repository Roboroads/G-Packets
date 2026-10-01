package testfixtures.trailing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A packet whose only optional part sits inside a nested structure, for the trailing-bytes tests.
 * It lives outside the {@code me.roboroads.gearth.gpackets} scan root, so the packet rules don't
 * gate it.
 */
public final class TrailingFixtures {
    private TrailingFixtures() {
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inner {
        public static final Schema<Inner> SCHEMA = Schema.of(Inner.class)
                .integer("id")
                .optional(s -> s.string("label"));

        private Integer id;
        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NestedOptional implements Packet {
        public static final PacketType<NestedOptional> TYPE = PacketType.of("NestedOptional", HMessage.Direction.TOCLIENT, Schema.of(NestedOptional.class)
                .struct("inner", Inner.SCHEMA));

        private Inner inner;

        @Override
        public HPacket toPacket() {
            return TYPE.toPacket(this);
        }
    }
}
