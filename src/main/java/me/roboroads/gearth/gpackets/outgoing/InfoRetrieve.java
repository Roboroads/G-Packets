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
 * Asks the server for your account info. The client sends it right after
 * {@code AuthenticationOK}. It has no parameters, so there's no all-arguments constructor either.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class InfoRetrieve implements Packet, JsonSerializable {
    public static final PacketType<InfoRetrieve> TYPE = PacketType.of("InfoRetrieve", HMessage.Direction.TOSERVER, Schema.of(InfoRetrieve.class));

    public static InfoRetrieve fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static InfoRetrieve fromJson(String json) {
        return Json.parse(InfoRetrieve.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
