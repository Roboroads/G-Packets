package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomThickness;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Whether the room hides its walls, and how thick its walls and floor are drawn. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomVisualizationSettings implements Packet, JsonSerializable {
    public static final PacketType<RoomVisualizationSettings> TYPE = PacketType.of("RoomVisualizationSettings", HMessage.Direction.TOCLIENT, Schema.of(RoomVisualizationSettings.class)
            .bool("hideWalls")
            .enumInt("wallThickness", RoomThickness.class)
            .enumInt("floorThickness", RoomThickness.class));

    // The client calls it wallsHidden.
    private Boolean hideWalls;
    // The client clamps both thicknesses to -2..1 and draws them 2^thickness times as thick
    // (wallThicknessMultiplier, floorThicknessMultiplier).
    private RoomThickness wallThickness;
    private RoomThickness floorThickness;

    public static RoomVisualizationSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomVisualizationSettings fromJson(String json) {
        return Json.parse(RoomVisualizationSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
