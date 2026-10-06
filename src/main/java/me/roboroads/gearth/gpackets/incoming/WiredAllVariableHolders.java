package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableHolder;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredVariable;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * Every furni or user in the room that holds a variable, the answer to
 * {@code WiredGetAllVariableHolders}. The wired menu's variable overview highlights them.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredAllVariableHolders implements Packet, JsonSerializable {
    public static final PacketType<WiredAllVariableHolders> TYPE = PacketType.of("WiredAllVariableHolders", HMessage.Direction.TOCLIENT, Schema.of(WiredAllVariableHolders.class)
            .integer("unknownInt1")
            .struct("variable", WiredVariable.SCHEMA)
            .list("holders", VariableHolder.SCHEMA));

    // No evidence: the parser reads this int and drops it before it reads the variable.
    @Unused("The client reads it and drops it")
    @Deprecated
    private Integer unknownInt1;
    private WiredVariable variable;
    // The wired menu stops highlighting with wiredmenu.variable_overview.highlight.error.too_many
    // above 1000 holders, or 400 for a variable with a value.
    private List<VariableHolder> holders;

    public static WiredAllVariableHolders fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredAllVariableHolders fromJson(String json) {
        return Json.parse(WiredAllVariableHolders.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
