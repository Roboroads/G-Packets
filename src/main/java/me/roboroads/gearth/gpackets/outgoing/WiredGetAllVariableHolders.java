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

/**
 * Asks which furni or users hold a variable; the server answers with
 * {@code WiredAllVariableHolders}. The wired menu's variable overview sends it while it highlights
 * the holders.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredGetAllVariableHolders implements Packet, JsonSerializable {
    public static final PacketType<WiredGetAllVariableHolders> TYPE = PacketType.of("WiredGetAllVariableHolders", HMessage.Direction.TOSERVER, Schema.of(WiredGetAllVariableHolders.class)
            .string("variableId"));

    // WiredVariable.variableId of the selected variable.
    private String variableId;

    public static WiredGetAllVariableHolders fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetAllVariableHolders fromJson(String json) {
        return Json.parse(WiredGetAllVariableHolders.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
