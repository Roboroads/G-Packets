package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
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
 * The hash of all the room's variables, the answer to {@code WiredGetAllVariablesHash}. When it
 * differs from the hash the client has, the client asks for the changes with
 * {@code WiredGetAllVariablesDiffs}.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredAllVariablesHash implements Packet, JsonSerializable {
    public static final PacketType<WiredAllVariablesHash> TYPE = PacketType.of("WiredAllVariablesHash", HMessage.Direction.TOCLIENT, Schema.of(WiredAllVariablesHash.class)
            .integer("allVariablesHash"));

    private Integer allVariablesHash;

    public static WiredAllVariablesHash fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredAllVariablesHash fromJson(String json) {
        return Json.parse(WiredAllVariablesHash.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
