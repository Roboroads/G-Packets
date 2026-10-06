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
 * You stopped typing in the chat input, after {@code StartTyping}. It has no parameters, so there's
 * no all-arguments constructor either.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class CancelTyping implements Packet, JsonSerializable {
    public static final PacketType<CancelTyping> TYPE = PacketType.of("CancelTyping", HMessage.Direction.TOSERVER, Schema.of(CancelTyping.class));

    public static CancelTyping fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CancelTyping fromJson(String json) {
        return Json.parse(CancelTyping.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
