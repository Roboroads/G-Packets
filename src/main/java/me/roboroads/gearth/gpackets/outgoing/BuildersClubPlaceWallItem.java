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
 * Borrows a wall furni from the Builders Club catalog and hangs it in the room you're in. The
 * catalog also sends it for a floor pattern, wallpaper or landscape. Sent from the same places as
 * {@code BuildersClubPlaceRoomItem}, and the server can answer it with a
 * {@code BuildersClubPlacementWarning} the same way.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BuildersClubPlaceWallItem implements Packet, JsonSerializable {
    public static final PacketType<BuildersClubPlaceWallItem> TYPE = PacketType.of("BuildersClubPlaceWallItem", HMessage.Direction.TOSERVER, Schema.of(BuildersClubPlaceWallItem.class)
            .integer("pageId")
            .integer("offerId")
            .string("extraParam")
            .string("wallLocation")
            .bool("confirmHideRoom"));

    // The catalog page of the offer. For an offer from the search results, the catalog sends the
    // first page that has it, or CatalogNavigator.DUMMY_PAGE_ID_FOR_OFFER_SEARCH (-12345678) when
    // none does. The info stand sends -1.
    private Integer pageId;
    // The info stand sends the furni's bcOfferId.
    private Integer offerId;
    // The product's extraParam, like FurniProduct.extraParam.
    private String extraParam;
    // ":w=<wall x>,<wall y> l=<offset x>,<offset y> <l or r>", built by the room's
    // getOldLocationString.
    private String wallLocation;
    // False on the first try. The client only sends true when it repeats a
    // BuildersClubPlacementWarning after you click OK on room.confirm.hide_room ("Placing this
    // furniture will hide the room from the navigator"). The composer's parameter has no name.
    private boolean confirmHideRoom;

    public static BuildersClubPlaceWallItem fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BuildersClubPlaceWallItem fromJson(String json) {
        return Json.parse(BuildersClubPlaceWallItem.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
