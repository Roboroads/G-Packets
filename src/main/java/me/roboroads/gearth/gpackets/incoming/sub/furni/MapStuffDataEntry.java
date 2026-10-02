package me.roboroads.gearth.gpackets.incoming.sub.furni;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** One key and value of a {@link MapStuffData}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MapStuffDataEntry implements SubPacket, JsonSerializable {
    public static final Schema<MapStuffDataEntry> SCHEMA = Schema.of(MapStuffDataEntry.class)
            .string("key")
            .string("value");

    private String key;
    private String value;

    public static MapStuffDataEntry fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
