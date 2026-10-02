package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The room you entered is ready; the client sets it as the current room and starts showing it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomReady implements Packet, JsonSerializable {
    public static final PacketType<RoomReady> TYPE = PacketType.of("RoomReady", HMessage.Direction.TOCLIENT, Schema.of(RoomReady.class)
            .string("roomType")
            .integer("roomId"));

    // The client stores it as the room's world type (RoomEngine.setWorldType). Nothing in this
    // client calls getWorldType to read it back.
    private String roomType;
    private Integer roomId;

    public static RoomReady fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomReady fromJson(String json) {
        return Json.parse(RoomReady.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
