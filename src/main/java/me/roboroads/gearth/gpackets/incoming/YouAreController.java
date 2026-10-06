package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomControllerLevel;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Your rights in a room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class YouAreController implements Packet, JsonSerializable {
    public static final PacketType<YouAreController> TYPE = PacketType.of("YouAreController", HMessage.Direction.TOCLIENT, Schema.of(YouAreController.class)
            .integer("roomId")
            .enumInt("roomControllerLevel", RoomControllerLevel.class));

    // The client calls it flatId; RoomPermissionsHandler looks up that room's session with it.
    private Integer roomId;
    // Stored as the room session's roomControllerLevel.
    private RoomControllerLevel roomControllerLevel;

    public static YouAreController fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static YouAreController fromJson(String json) {
        return Json.parse(YouAreController.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
