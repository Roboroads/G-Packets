package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The server sends you to another room; the navigator goes there. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomForward implements Packet, JsonSerializable {
    public static final PacketType<RoomForward> TYPE = PacketType.of("RoomForward", HMessage.Direction.TOCLIENT, Schema.of(RoomForward.class)
            .integer("roomId"));

    private Integer roomId;

    public static RoomForward fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomForward fromJson(String json) {
        return Json.parse(RoomForward.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
