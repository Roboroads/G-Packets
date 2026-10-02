package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.HeightMapTileUpdate;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** Changes to some tiles of the {@code HeightMap}, for example after furni moved. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class HeightMapUpdate implements Packet, JsonSerializable {
    public static final PacketType<HeightMapUpdate> TYPE = PacketType.of("HeightMapUpdate", HMessage.Direction.TOCLIENT, Schema.of(HeightMapUpdate.class)
            .listWithCount("tileUpdates", WireType.BYTE, HeightMapTileUpdate.SCHEMA));

    // Counted by a byte, so at most 127 per packet. G-Rust calls it tile_updates.
    private List<HeightMapTileUpdate> tileUpdates;

    public static HeightMapUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static HeightMapUpdate fromJson(String json) {
        return Json.parse(HeightMapUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
