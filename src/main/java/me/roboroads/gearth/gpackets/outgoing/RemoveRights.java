package me.roboroads.gearth.gpackets.outgoing;

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
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxSize;

/**
 * Takes a user's rights away in the room you're in: the avatar menu's "Remove rights" button, or a
 * user clicked in the room settings' rights list. The server answers with
 * {@code FlatControllerRemoved}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemoveRights implements Packet, JsonSerializable {
    public static final PacketType<RemoveRights> TYPE = PacketType.of("RemoveRights", HMessage.Direction.TOSERVER, Schema.of(RemoveRights.class)
            .list("userIds", WireType.INT, maxSize(1)));

    // Account ids (User.id, FlatController.userId), not room indexes. The wire takes a list, but
    // both places that send it (RoomSession.removeRights and the room settings' UserListCtrl) put
    // exactly one id in it.
    private List<Integer> userIds;

    public static RemoveRights fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemoveRights fromJson(String json) {
        return Json.parse(RemoveRights.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
