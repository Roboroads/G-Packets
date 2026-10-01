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

/** A room's wired settings, the answer to {@code WiredGetRoomSettings}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredRoomSettings implements Packet, JsonSerializable {
    public static final PacketType<WiredRoomSettings> TYPE = PacketType.of("WiredRoomSettings", HMessage.Direction.TOCLIENT, Schema.of(WiredRoomSettings.class)
            .integer("modifyPermissionMask")
            .integer("readPermissionMask")
            .string("timezone"));

    // Bit masks; the client's wired menu sets and clears one bit per checkbox.
    private Integer modifyPermissionMask;
    private Integer readPermissionMask;
    private String timezone;

    public static WiredRoomSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredRoomSettings fromJson(String json) {
        return Json.parse(WiredRoomSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
