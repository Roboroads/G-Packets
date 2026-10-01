package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
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
 * Asks for the current room's wired settings; the server answers with {@code WiredRoomSettings}.
 * It has no parameters, so there's no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class WiredGetRoomSettings implements Packet, JsonSerializable {
    public static final PacketType<WiredGetRoomSettings> TYPE = PacketType.of("WiredGetRoomSettings", HMessage.Direction.TOSERVER, Schema.of(WiredGetRoomSettings.class));

    public static WiredGetRoomSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetRoomSettings fromJson(String json) {
        return Json.parse(WiredGetRoomSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
