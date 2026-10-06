package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Borrows a floor furni from the Builders Club catalog and places it in the room you're in. The
 * catalog sends it when you drop an offer in the room, and the info stand when you use the place
 * more button on a borrowed furni. When placing it would hide the room, the server answers with a
 * {@code BuildersClubPlacementWarning} instead.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BuildersClubPlaceRoomItem implements Packet, JsonSerializable {
    public static final PacketType<BuildersClubPlaceRoomItem> TYPE = PacketType.of("BuildersClubPlaceRoomItem", HMessage.Direction.TOSERVER, Schema.of(BuildersClubPlaceRoomItem.class)
            .integer("pageId")
            .integer("offerId")
            .string("extraParam")
            .integer("x")
            .integer("y")
            .enumInt("direction", Direction.class)
            .bool("confirmHideRoom"));

    // The catalog page of the offer. For an offer from the search results, the catalog sends the
    // first page that has it, or CatalogNavigator.DUMMY_PAGE_ID_FOR_OFFER_SEARCH (-12345678) when
    // none does. The info stand sends -1.
    private Integer pageId;
    // The info stand sends the furni's bcOfferId.
    private Integer offerId;
    // The product's extraParam, like FurniProduct.extraParam.
    private String extraParam;
    private Integer x;
    private Integer y;
    // The room engine turns the furni's rotation into 0-7 before the catalog sends it.
    private Direction direction;
    // False on the first try. The client only sends true when it repeats a
    // BuildersClubPlacementWarning after you click OK on room.confirm.hide_room ("Placing this
    // furniture will hide the room from the navigator"). The composer's parameter has no name.
    private boolean confirmHideRoom;

    public static BuildersClubPlaceRoomItem fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BuildersClubPlaceRoomItem fromJson(String json) {
        return Json.parse(BuildersClubPlaceRoomItem.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
