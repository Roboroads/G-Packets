package me.roboroads.gearth.gpackets.incoming;

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
 * How much of its wired and furni limits the room uses, the answer to {@code WiredGetRoomStats}:
 * the client's WiredRoomStatsData. The wired menu's monitor tab shows each count against its cap.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredRoomStats implements Packet, JsonSerializable {
    public static final PacketType<WiredRoomStats> TYPE = PacketType.of("WiredRoomStats", HMessage.Direction.TOCLIENT, Schema.of(WiredRoomStats.class)
            .doubleValue("executionCost")
            .doubleValue("executionCostCap")
            .bool("isHeavy")
            .integer("floorItemCount")
            .integer("floorItemCap")
            .integer("wallItemCount")
            .integer("wallItemCap")
            .integer("permanentFurniVariables")
            .integer("maxPermanentFurniVariables")
            .integer("permanentUserVariables")
            .integer("maxPermanentUserVariables")
            .integer("permanentGlobalVariables")
            .integer("maxPermanentGlobalVariables"));

    // Doubles on the wire. The monitor tab shows them as "Wired usage: <cost>/<cap>".
    private Double executionCost;
    private Double executionCostCap;
    // Shown as "Is heavy: yes/no" (wiredmenu.monitor.statistics.is_heavy).
    private Boolean isHeavy;
    private Integer floorItemCount;
    private Integer floorItemCap;
    private Integer wallItemCount;
    private Integer wallItemCap;
    private Integer permanentFurniVariables;
    private Integer maxPermanentFurniVariables;
    private Integer permanentUserVariables;
    private Integer maxPermanentUserVariables;
    private Integer permanentGlobalVariables;
    private Integer maxPermanentGlobalVariables;

    public static WiredRoomStats fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredRoomStats fromJson(String json) {
        return Json.parse(WiredRoomStats.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
