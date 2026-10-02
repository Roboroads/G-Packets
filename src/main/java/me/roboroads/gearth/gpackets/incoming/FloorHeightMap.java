package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.AreaHideData;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The room's floor plan, wall height, hidden areas and starting camera position, sent when you enter a room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FloorHeightMap implements Packet, JsonSerializable {
    public static final PacketType<FloorHeightMap> TYPE = PacketType.of("FloorHeightMap", HMessage.Direction.TOCLIENT, Schema.of(FloorHeightMap.class)
            .bool("isSmallScale")
            .integer("fixedWallsHeight")
            .string("floorPlan")
            .list("areaHideData", AreaHideData.SCHEMA)
            .integer("cameraInitX")
            .integer("cameraInitY")
            .floatValue("cameraInitZ"));

    // The client draws the room at scale 32 when true and 64 when false (G-Rust: is_small_scale).
    private Boolean isSmallScale;
    // -1 when the room has no fixed wall height: the floor plan editor then leaves its
    // walls_fixed_height_enabled_checkbox off.
    private Integer fixedWallsHeight;
    // Rows separated by "\r". Each character is a tile: its height as a base-36 digit, or "x" for no
    // tile. The client calls it text; the floor plan editor calls the same string the floor plan.
    private String floorPlan;
    private List<AreaHideData> areaHideData;
    private Integer cameraInitX;
    private Integer cameraInitY;
    private Float cameraInitZ;

    public static FloorHeightMap fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FloorHeightMap fromJson(String json) {
        return Json.parse(FloorHeightMap.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
