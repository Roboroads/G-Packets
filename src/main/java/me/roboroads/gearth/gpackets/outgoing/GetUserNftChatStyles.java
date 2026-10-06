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
 * Asks for the {@code UserNftChatStyles}. The client sends it once its session data is set up. It
 * has no parameters, so there's no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class GetUserNftChatStyles implements Packet, JsonSerializable {
    public static final PacketType<GetUserNftChatStyles> TYPE = PacketType.of("GetUserNftChatStyles", HMessage.Direction.TOSERVER, Schema.of(GetUserNftChatStyles.class));

    public static GetUserNftChatStyles fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetUserNftChatStyles fromJson(String json) {
        return Json.parse(GetUserNftChatStyles.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
