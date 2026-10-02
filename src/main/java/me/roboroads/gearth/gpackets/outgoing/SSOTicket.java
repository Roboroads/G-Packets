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

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.notEmpty;

/**
 * Logs in with a single sign-on ticket. The client sends it after {@code VersionCheck} and
 * {@code UniqueID}, once encryption is on; the server answers with {@code AuthenticationOK}.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SSOTicket implements Packet, JsonSerializable {
    public static final PacketType<SSOTicket> TYPE = PacketType.of("SSOTicket", HMessage.Direction.TOSERVER, Schema.of(SSOTicket.class)
            .string("ssoTicket", notEmpty())
            .integer("clientUptimeMilliSeconds"));

    // The login component's ssoTicket, usually its sso.token property. It only sends the packet
    // when the ticket isn't empty (the login component's sendConnectionParameters).
    private String ssoTicket;
    // The composer adds getTimer(): the milliseconds since the client started. G-Rust calls it time.
    private Integer clientUptimeMilliSeconds;

    public static SSOTicket fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SSOTicket fromJson(String json) {
        return Json.parse(SSOTicket.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
