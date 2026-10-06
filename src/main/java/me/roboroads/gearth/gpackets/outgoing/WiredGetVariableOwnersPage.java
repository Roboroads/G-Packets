package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableSort;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Asks for a page of the users, pets and bots that hold a permanent variable; the server answers
 * with {@code WiredUserVariablesList}. The wired menu's manage button opens the variable management
 * window with page 1, {@code HIGHEST_VALUE} and all user types; the window sends it again when you
 * page or change a filter.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredGetVariableOwnersPage implements Packet, JsonSerializable {
    public static final PacketType<WiredGetVariableOwnersPage> TYPE = PacketType.of("WiredGetVariableOwnersPage", HMessage.Direction.TOSERVER, Schema.of(WiredGetVariableOwnersPage.class)
            .string("variableId")
            .integer("page")
            .integer("pageSize")
            .enumInt("sortType", WiredVariableSort.class)
            .integer("userTypeFilter"));

    // WiredVariable.variableId of a persisted user variable.
    private String variableId;
    // Starts at 1.
    private Integer page;
    // VariableManagementConfig.PAGE_SIZE: the client always sends 50.
    @Builder.Default
    private Integer pageSize = 50;
    private WiredVariableSort sortType;
    // -1 for all, otherwise a UserType value: 1 (PLAYER), 2 (PET) or 4 (BOT), from the window's
    // user_type_menu. Kept as an int because UserType has no -1. Not limited to those: when it asks
    // for another page, the client sends back the filter of the page it has.
    private Integer userTypeFilter;

    public static WiredGetVariableOwnersPage fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredGetVariableOwnersPage fromJson(String json) {
        return Json.parse(WiredGetVariableOwnersPage.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
