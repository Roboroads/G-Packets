package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.DisconnectReasonCode;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** The server is about to close the connection, and says why. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class DisconnectReason implements Packet, JsonSerializable {
    public static final PacketType<DisconnectReason> TYPE = PacketType.of("DisconnectReason", HMessage.Direction.TOCLIENT, Schema.of(DisconnectReason.class)
            .optional(s -> s.enumInt("reason", DisconnectReasonCode.class)));

    // Only read when bytes are left; the client uses -1 when it's missing. The client shows it in
    // the "Logged out" alert.
    private DisconnectReasonCode reason;

    public static DisconnectReason fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static DisconnectReason fromJson(String json) {
        return Json.parse(DisconnectReason.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
