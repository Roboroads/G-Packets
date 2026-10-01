package testfixtures.limits;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.each;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxLength;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxSize;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.notEmpty;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.range;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.requiresVip;

/**
 * Packets with limits, one per direction, for the limit tests. They live outside the
 * {@code me.roboroads.gearth.gpackets} scan root, so the packet rules don't gate them.
 */
public final class LimitFixtures {
    private LimitFixtures() {
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        public static final Schema<Item> SCHEMA = Schema.of(Item.class).string("label", maxLength(3));

        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Outgoing implements Packet {
        public static final PacketType<Outgoing> TYPE = PacketType.of("LimitedOut", HMessage.Direction.TOSERVER, schema(Outgoing.class));

        private String name;
        private List<String> tags;
        private Boolean sleepEnabled;
        private Integer sleep;
        private Boolean kickEnabled;
        private Integer kick;
        private List<Item> items;

        @Override
        public HPacket toPacket() {
            return TYPE.toPacket(this);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Incoming implements Packet {
        public static final PacketType<Incoming> TYPE = PacketType.of("LimitedIn", HMessage.Direction.TOCLIENT, schema(Incoming.class));

        private String name;
        private List<String> tags;
        private Boolean sleepEnabled;
        private Integer sleep;
        private Boolean kickEnabled;
        private Integer kick;
        private List<Item> items;

        @Override
        public HPacket toPacket() {
            return TYPE.toPacket(this);
        }
    }

    private static <T> Schema<T> schema(Class<T> type) {
        return Schema.of(type)
                .string("name", notEmpty(), maxLength(5))
                .list("tags", WireType.STRING, maxSize(2), each(maxLength(3)))
                .bool("sleepEnabled", requiresVip())
                .integer("sleep", range(30, 3600).orZero())
                .bool("kickEnabled")
                .integer("kick", range(60, 36000).orZero())
                .list("items", Item.SCHEMA)
                .rule("kick is at least sleep + 30 when both are enabled",
                        values -> !(Boolean) values.get("sleepEnabled") || !(Boolean) values.get("kickEnabled")
                                || (Integer) values.get("kick") >= (Integer) values.get("sleep") + 30);
    }
}
