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

/** The server saved a room's settings, the answer to {@code SaveRoomSettings}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsSaved implements Packet, JsonSerializable {
    public static final PacketType<RoomSettingsSaved> TYPE = PacketType.of("RoomSettingsSaved", HMessage.Direction.TOCLIENT, Schema.of(RoomSettingsSaved.class)
            .integer("roomId"));

    private Integer roomId;

    public static RoomSettingsSaved fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomSettingsSaved fromJson(String json) {
        return Json.parse(RoomSettingsSaved.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
