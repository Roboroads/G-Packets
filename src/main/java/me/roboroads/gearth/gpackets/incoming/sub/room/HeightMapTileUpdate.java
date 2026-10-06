package me.roboroads.gearth.gpackets.incoming.sub.room;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** One changed tile in {@code HeightMapUpdate}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class HeightMapTileUpdate implements SubPacket, JsonSerializable {
    public static final Schema<HeightMapTileUpdate> SCHEMA = Schema.of(HeightMapTileUpdate.class)
            .byteValue("x")
            .byteValue("y")
            .shortValue("tile");

    private Byte x;
    private Byte y;
    // Encoded like each entry of HeightMap.tiles: -1 is no tile, the stacking-blocked bit (bit 14 by
    // default) blocks stacking, and the bits below it are the stacking height times 256.
    private Short tile;

    public static HeightMapTileUpdate fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
