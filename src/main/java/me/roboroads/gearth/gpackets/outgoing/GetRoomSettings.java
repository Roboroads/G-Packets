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

/** Asks for a room's settings; the server answers with {@code RoomSettingsData} or {@code RoomSettingsError}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GetRoomSettings implements Packet, JsonSerializable {
    public static final PacketType<GetRoomSettings> TYPE = PacketType.of("GetRoomSettings", HMessage.Direction.TOSERVER, Schema.of(GetRoomSettings.class)
            .integer("roomId"));

    private Integer roomId;

    public static GetRoomSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetRoomSettings fromJson(String json) {
        return Json.parse(GetRoomSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
