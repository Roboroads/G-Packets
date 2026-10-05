package me.roboroads.gearth.gpackets.outgoing;

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

/**
 * Takes rights away from everyone in a room: the room settings' remove all button, after the
 * {@code navigator.flatctrls.removeconfirm} confirmation.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemoveAllRights implements Packet, JsonSerializable {
    public static final PacketType<RemoveAllRights> TYPE = PacketType.of("RemoveAllRights", HMessage.Direction.TOSERVER, Schema.of(RemoveAllRights.class)
            .integer("roomId"));

    // The client sends the id of the room whose settings are open (its flatId).
    private Integer roomId;

    public static RemoveAllRights fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemoveAllRights fromJson(String json) {
        return Json.parse(RemoveAllRights.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
