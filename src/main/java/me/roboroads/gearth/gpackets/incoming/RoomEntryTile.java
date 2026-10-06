package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The room's door tile and the direction users face when they walk in; the answer to {@code GetRoomEntryTile}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntryTile implements Packet, JsonSerializable {
    public static final PacketType<RoomEntryTile> TYPE = PacketType.of("RoomEntryTile", HMessage.Direction.TOCLIENT, Schema.of(RoomEntryTile.class)
            .integer("x")
            .integer("y")
            .enumInt("direction", Direction.class));

    private Integer x;
    private Integer y;
    // The client calls it dir.
    private Direction direction;

    public static RoomEntryTile fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomEntryTile fromJson(String json) {
        return Json.parse(RoomEntryTile.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
