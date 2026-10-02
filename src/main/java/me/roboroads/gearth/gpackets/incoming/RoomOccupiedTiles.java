package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.OccupiedTile;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The tiles the floor plan editor won't let you change; the answer to {@code GetOccupiedTiles}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomOccupiedTiles implements Packet, JsonSerializable {
    public static final PacketType<RoomOccupiedTiles> TYPE = PacketType.of("RoomOccupiedTiles", HMessage.Direction.TOCLIENT, Schema.of(RoomOccupiedTiles.class)
            .list("occupiedTiles", OccupiedTile.SCHEMA));

    private List<OccupiedTile> occupiedTiles;

    public static RoomOccupiedTiles fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomOccupiedTiles fromJson(String json) {
        return Json.parse(RoomOccupiedTiles.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
