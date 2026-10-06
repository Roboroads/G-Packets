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

/**
 * What you may do with the current room's wired. The room owner and staff may always do both: the
 * client's WiredMenuController only checks these for other users.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredPermissions implements Packet, JsonSerializable {
    public static final PacketType<WiredPermissions> TYPE = PacketType.of("WiredPermissions", HMessage.Direction.TOCLIENT, Schema.of(WiredPermissions.class)
            .bool("canModify")
            .bool("canRead"));

    // The wired menu's write permission (hasWritePermission): it lets you change variables, clear the
    // error logs and reload the room.
    private Boolean canModify;
    // The wired menu's read permission (hasReadPermission): without it the menu doesn't open, and the
    // client closes it when this turns false.
    private Boolean canRead;

    public static WiredPermissions fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredPermissions fromJson(String json) {
        return Json.parse(WiredPermissions.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
