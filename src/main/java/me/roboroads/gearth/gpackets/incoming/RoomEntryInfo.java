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

/** Which room you entered, and whether you own it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntryInfo implements Packet, JsonSerializable {
    public static final PacketType<RoomEntryInfo> TYPE = PacketType.of("RoomEntryInfo", HMessage.Direction.TOCLIENT, Schema.of(RoomEntryInfo.class)
            .integer("roomId")
            .bool("isOwner"));

    // The client calls it guestRoomId.
    private Integer roomId;
    // The client calls it owner. With it, the navigator lets you edit the room settings.
    private Boolean isOwner;

    public static RoomEntryInfo fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomEntryInfo fromJson(String json) {
        return Json.parse(RoomEntryInfo.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
