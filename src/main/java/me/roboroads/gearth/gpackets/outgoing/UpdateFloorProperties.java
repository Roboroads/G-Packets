package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.RoomThickness;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Saves the room's floor plan from the floor plan editor. The client only lets you save with Builders
 * Club time left or with security level 4.
 *
 * <p>The entry tile and thicknesses are only written when one of them is set, and
 * {@code fixedWallsHeight} only when it is set. The client always sends the entry tile and
 * thicknesses, and sends {@code fixedWallsHeight} when the editor's fixed wall height box is ticked.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFloorProperties implements Packet, JsonSerializable {
    public static final PacketType<UpdateFloorProperties> TYPE = PacketType.of("UpdateFloorProperties", HMessage.Direction.TOSERVER, Schema.of(UpdateFloorProperties.class)
            .string("floorPlan")
            .optional(s -> s
                    .integer("entryX")
                    .integer("entryY")
                    .enumInt("entryDirection", Direction.class)
                    .enumInt("wallThickness", RoomThickness.class)
                    .enumInt("floorThickness", RoomThickness.class)
                    .optional(t -> t.integer("fixedWallsHeight"))));

    // Rows ending in "\r", in the format of FloorHeightMap.floorPlan (FloorPlanCache.getData, or the
    // import dialog's text as typed). The editor caps a drawn plan at 64 by 64 tiles and, without the
    // BUILDER_AT_WORK perk, an area of 3025, but an imported plan isn't checked, so neither is a limit.
    private String floorPlan;
    // The client calls the entry tile entryPoint and its direction entryPointDir; the editor wraps the
    // direction to 0..7.
    private Integer entryX;
    private Integer entryY;
    private Direction entryDirection;
    // The editor's dropdown index minus 2 (BCFloorPlanEditor.getThicknessSettingBySelectionIndex).
    private RoomThickness wallThickness;
    private RoomThickness floorThickness;
    // The editor's wall height slider gives 0 to 16 (WALL_HEIGHT_LIMIT), but the client also sends back
    // the value FloorHeightMap gave it unchanged, so this isn't a limit.
    private Integer fixedWallsHeight;

    public static UpdateFloorProperties fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UpdateFloorProperties fromJson(String json) {
        return Json.parse(UpdateFloorProperties.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
