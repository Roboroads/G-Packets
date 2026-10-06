package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableOwner;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableSort;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * One page of the users, pets and bots that hold a permanent variable, the answer to
 * {@code WiredGetVariableOwnersPage}: the client's WiredUserVariablesPage, shown in the variable
 * management window.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredUserVariablesList implements Packet, JsonSerializable {
    public static final PacketType<WiredUserVariablesList> TYPE = PacketType.of("WiredUserVariablesList", HMessage.Direction.TOCLIENT, Schema.of(WiredUserVariablesList.class)
            .string("variableId")
            .integer("totalEntries")
            .integer("currentPage")
            .integer("pageSize")
            .list("owners", VariableOwner.SCHEMA)
            .integer("userTypeFilter")
            .enumInt("sortType", WiredVariableSort.class));

    private String variableId;
    private Integer totalEntries;
    // Starts at 1.
    private Integer currentPage;
    // Client: amount. The window drops a page whose size isn't its own page size, 50.
    private Integer pageSize;
    // Client: elements.
    private List<VariableOwner> owners;
    // -1 for all, otherwise a UserType value: 1 (PLAYER), 2 (PET) or 4 (BOT), the items of the
    // window's user_type_menu. Kept as an int because UserType has no -1.
    private Integer userTypeFilter;
    // Client: sortTypFilter.
    private WiredVariableSort sortType;

    public static WiredUserVariablesList fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredUserVariablesList fromJson(String json) {
        return Json.parse(WiredUserVariablesList.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
