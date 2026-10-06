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

/** You have no rights in a room: the client sets your level there to {@code NOT_CONTROLLER}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class YouAreNotController implements Packet, JsonSerializable {
    public static final PacketType<YouAreNotController> TYPE = PacketType.of("YouAreNotController", HMessage.Direction.TOCLIENT, Schema.of(YouAreNotController.class)
            .integer("roomId"));

    // The client calls it flatId; RoomPermissionsHandler looks up that room's session with it.
    private Integer roomId;

    public static YouAreNotController fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static YouAreNotController fromJson(String json) {
        return Json.parse(YouAreNotController.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
