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

/** The server refused to save a room's settings. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsSaveError implements Packet, JsonSerializable {
    public static final PacketType<RoomSettingsSaveError> TYPE = PacketType.of("RoomSettingsSaveError", HMessage.Direction.TOCLIENT, Schema.of(RoomSettingsSaveError.class)
            .integer("roomId")
            .integer("errorCode")
            .string("info"));

    private Integer roomId;
    // The client declares 1 to 13 and 16 but only explains some, so this stays an int:
    // 5 password missing, 7 name missing, 8 name has unacceptable words, 10 description has
    // unacceptable words, 11 a tag has unacceptable words, 12 a tag users can't choose,
    // 13 a tag is too long, 16 a custom error described by info.
    private Integer errorCode;
    private String info;

    public static RoomSettingsSaveError fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomSettingsSaveError fromJson(String json) {
        return Json.parse(RoomSettingsSaveError.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
