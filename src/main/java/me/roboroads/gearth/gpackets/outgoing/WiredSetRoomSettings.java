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

/** Saves the current room's wired settings. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredSetRoomSettings implements Packet, JsonSerializable {
    public static final PacketType<WiredSetRoomSettings> TYPE = PacketType.of("WiredSetRoomSettings", HMessage.Direction.TOSERVER, Schema.of(WiredSetRoomSettings.class)
            .integer("modifyPermissionMask")
            .integer("readPermissionMask")
            .string("timezone"));

    // Bit masks; the client's wired menu sets and clears one bit per checkbox.
    private Integer modifyPermissionMask;
    private Integer readPermissionMask;
    // The client sends "" when no timezone is selected.
    private String timezone;

    public static WiredSetRoomSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredSetRoomSettings fromJson(String json) {
        return Json.parse(WiredSetRoomSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
