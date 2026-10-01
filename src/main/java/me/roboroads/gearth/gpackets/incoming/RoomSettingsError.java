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
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The server can't send a room's settings. */
// The client's handler (onRoomSettingsError) only takes the parser and does nothing else.
@Unused("The client's handler takes the packet and does nothing with it")
@Deprecated
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsError implements Packet, JsonSerializable {
    public static final PacketType<RoomSettingsError> TYPE = PacketType.of("RoomSettingsError", HMessage.Direction.TOCLIENT, Schema.of(RoomSettingsError.class)
            .integer("roomId")
            .integer("errorCode"));

    private Integer roomId;
    private Integer errorCode;

    public static RoomSettingsError fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomSettingsError fromJson(String json) {
        return Json.parse(RoomSettingsError.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
