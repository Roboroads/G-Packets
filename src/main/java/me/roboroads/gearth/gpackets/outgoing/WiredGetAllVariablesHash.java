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
 * Asks for the hash of all the room's variables; the server answers with
 * {@code WiredAllVariablesHash}. It has no parameters, so there's no all-arguments constructor
 * either.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class WiredGetAllVariablesHash implements Packet, JsonSerializable {
    public static final PacketType<WiredGetAllVariablesHash> TYPE = PacketType.of("WiredGetAllVariablesHash", HMessage.Direction.TOSERVER, Schema.of(WiredGetAllVariablesHash.class));

    public static WiredGetAllVariablesHash fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetAllVariablesHash fromJson(String json) {
        return Json.parse(WiredGetAllVariablesHash.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
