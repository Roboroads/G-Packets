package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
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
 * You started typing in the chat input; the room sees a typing bubble above you
 * ({@code UserTyping}). {@code CancelTyping} stops it. It has no parameters, so there's no
 * all-arguments constructor either.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class StartTyping implements Packet, JsonSerializable {
    public static final PacketType<StartTyping> TYPE = PacketType.of("StartTyping", HMessage.Direction.TOSERVER, Schema.of(StartTyping.class));

    public static StartTyping fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static StartTyping fromJson(String json) {
        return Json.parse(StartTyping.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
