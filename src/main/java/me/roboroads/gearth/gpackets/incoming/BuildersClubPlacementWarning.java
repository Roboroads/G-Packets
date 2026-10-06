package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.BuildersClubPlacementType;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * The server's answer to a {@code BuildersClubPlaceRoomItem} or {@code BuildersClubPlaceWallItem}
 * when placing the furni would hide the room from the navigator. It repeats the placement. The
 * client asks you to confirm (room.confirm.hide_room, "You are using the Free Trial of Builders Club.
 * Placing this furniture will hide the room from the navigator, are you sure you want to
 * continue?") and on OK sends the same placement again with {@code confirmHideRoom} set.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BuildersClubPlacementWarning implements Packet, JsonSerializable {
    public static final PacketType<BuildersClubPlacementWarning> TYPE = PacketType.of("BuildersClubPlacementWarning", HMessage.Direction.TOCLIENT, Schema.of(BuildersClubPlacementWarning.class)
            .enumInt("placementType", BuildersClubPlacementType.class)
            .integer("pageId")
            .integer("offerId")
            .string("extraParam")
            .when("placementType", BuildersClubPlacementType.FLOOR, s -> s
                    .integer("x")
                    .integer("y")
                    .enumInt("direction", Direction.class))
            .when("placementType", BuildersClubPlacementType.WALL, s -> s
                    .string("wallLocation")));

    // The client calls it typeCode.
    private BuildersClubPlacementType placementType;
    // The same values the placement sent; the client copies them into the confirmed placement.
    private Integer pageId;
    private Integer offerId;
    private String extraParam;
    // Only for FLOOR.
    private Integer x;
    private Integer y;
    private Direction direction;
    // Only for WALL. Same format as BuildersClubPlaceWallItem.wallLocation.
    private String wallLocation;

    public static BuildersClubPlacementWarning fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BuildersClubPlacementWarning fromJson(String json) {
        return Json.parse(BuildersClubPlacementWarning.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
