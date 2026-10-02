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
 * Tells the server you're leaving; the client's communication manager sends it when the client
 * unloads. It has no parameters, so there's no all-arguments constructor either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class Disconnect implements Packet, JsonSerializable {
    public static final PacketType<Disconnect> TYPE = PacketType.of("Disconnect", HMessage.Direction.TOSERVER, Schema.of(Disconnect.class));

    public static Disconnect fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Disconnect fromJson(String json) {
        return Json.parse(Disconnect.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
