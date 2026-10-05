package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.BuildersClubFurniCount;
import me.roboroads.gearth.gpackets.incoming.BuildersClubPlacementWarning;
import me.roboroads.gearth.gpackets.incoming.BuildersClubSubscriptionStatus;
import me.roboroads.gearth.gpackets.model.enums.BuildersClubPlacementType;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.outgoing.BuildersClubPlaceRoomItem;
import me.roboroads.gearth.gpackets.outgoing.BuildersClubPlaceWallItem;
import me.roboroads.gearth.gpackets.outgoing.BuildersClubQueryFurniCount;
import org.junit.jupiter.api.Test;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BuildersClubWireFormatTest {

    // ---- BuildersClubFurniCount ----

    static HPacket furniCountPacket() {
        HPacket p = new HPacket("BuildersClubFurniCount", HMessage.Direction.TOCLIENT);
        p.appendInt(37);
        return p;
    }

    @Test
    void furniCountToPacket() {
        assertSameBytes(furniCountPacket(), new BuildersClubFurniCount(37).toPacket());
    }

    @Test
    void furniCountFromPacket() {
        assertEquals(new BuildersClubFurniCount(37), BuildersClubFurniCount.fromPacket(furniCountPacket()));
    }

    // ---- BuildersClubPlacementWarning ----

    static BuildersClubPlacementWarning floorWarning() {
        return BuildersClubPlacementWarning.builder()
                .placementType(BuildersClubPlacementType.FLOOR).pageId(1201).offerId(88001).extraParam("")
                .x(5).y(9).direction(Direction.SOUTH)
                .build();
    }

    static HPacket floorWarningPacket() {
        HPacket p = new HPacket("BuildersClubPlacementWarning", HMessage.Direction.TOCLIENT);
        p.appendInt(0).appendInt(1201).appendInt(88001).appendString("")
                .appendInt(5).appendInt(9).appendInt(4);
        return p;
    }

    static BuildersClubPlacementWarning wallWarning() {
        return BuildersClubPlacementWarning.builder()
                .placementType(BuildersClubPlacementType.WALL).pageId(-1).offerId(88002).extraParam("poster_7")
                .wallLocation(":w=3,4 l=12,30 r")
                .build();
    }

    static HPacket wallWarningPacket() {
        HPacket p = new HPacket("BuildersClubPlacementWarning", HMessage.Direction.TOCLIENT);
        p.appendInt(1).appendInt(-1).appendInt(88002).appendString("poster_7")
                .appendString(":w=3,4 l=12,30 r");
        return p;
    }

    @Test
    void floorPlacementWarningToPacket() {
        assertSameBytes(floorWarningPacket(), floorWarning().toPacket());
    }

    @Test
    void floorPlacementWarningFromPacket() {
        assertEquals(floorWarning(), BuildersClubPlacementWarning.fromPacket(floorWarningPacket()));
    }

    @Test
    void wallPlacementWarningToPacket() {
        assertSameBytes(wallWarningPacket(), wallWarning().toPacket());
    }

    @Test
    void wallPlacementWarningFromPacket() {
        assertEquals(wallWarning(), BuildersClubPlacementWarning.fromPacket(wallWarningPacket()));
    }

    // ---- BuildersClubSubscriptionStatus ----

    static HPacket subscriptionStatusPacket() {
        HPacket p = new HPacket("BuildersClubSubscriptionStatus", HMessage.Direction.TOCLIENT);
        p.appendInt(86400).appendInt(50).appendInt(2500).appendInt(172800);
        return p;
    }

    @Test
    void subscriptionStatusToPacket() {
        assertSameBytes(subscriptionStatusPacket(), new BuildersClubSubscriptionStatus(86400, 50, 2500, 172800).toPacket());
    }

    @Test
    void subscriptionStatusFromPacket() {
        assertEquals(new BuildersClubSubscriptionStatus(86400, 50, 2500, 172800),
                BuildersClubSubscriptionStatus.fromPacket(subscriptionStatusPacket()));
    }

    @Test
    void subscriptionStatusMayLeaveOffTheGraceSeconds() {
        HPacket p = new HPacket("BuildersClubSubscriptionStatus", HMessage.Direction.TOCLIENT);
        p.appendInt(0).appendInt(50).appendInt(2500);
        BuildersClubSubscriptionStatus withoutGrace = new BuildersClubSubscriptionStatus(0, 50, 2500, null);

        assertSameBytes(p, withoutGrace.toPacket());
        assertEquals(withoutGrace, BuildersClubSubscriptionStatus.fromPacket(p));
    }

    // ---- BuildersClubPlaceRoomItem ----

    static HPacket placeRoomItemPacket(boolean confirmHideRoom) {
        HPacket p = new HPacket("BuildersClubPlaceRoomItem", HMessage.Direction.TOSERVER);
        p.appendInt(1201).appendInt(88001).appendString("").appendInt(5).appendInt(9).appendInt(2)
                .appendBoolean(confirmHideRoom);
        return p;
    }

    static BuildersClubPlaceRoomItem placeRoomItem(boolean confirmHideRoom) {
        return new BuildersClubPlaceRoomItem(1201, 88001, "", 5, 9, Direction.EAST, confirmHideRoom);
    }

    @Test
    void placeRoomItemToPacket() {
        assertSameBytes(placeRoomItemPacket(false), placeRoomItem(false).toPacket());
        assertSameBytes(placeRoomItemPacket(true), placeRoomItem(true).toPacket());
    }

    @Test
    void placeRoomItemFromPacket() {
        assertEquals(placeRoomItem(false), BuildersClubPlaceRoomItem.fromPacket(placeRoomItemPacket(false)));
        assertEquals(placeRoomItem(true), BuildersClubPlaceRoomItem.fromPacket(placeRoomItemPacket(true)));
    }

    @Test
    void placeRoomItemDoesNotConfirmUnlessAsked() {
        assertFalse(BuildersClubPlaceRoomItem.builder().build().confirmHideRoom());
    }

    // ---- BuildersClubPlaceWallItem ----

    static HPacket placeWallItemPacket(boolean confirmHideRoom) {
        HPacket p = new HPacket("BuildersClubPlaceWallItem", HMessage.Direction.TOSERVER);
        p.appendInt(-1).appendInt(88002).appendString("poster_7").appendString(":w=3,4 l=12,30 r")
                .appendBoolean(confirmHideRoom);
        return p;
    }

    static BuildersClubPlaceWallItem placeWallItem(boolean confirmHideRoom) {
        return new BuildersClubPlaceWallItem(-1, 88002, "poster_7", ":w=3,4 l=12,30 r", confirmHideRoom);
    }

    @Test
    void placeWallItemToPacket() {
        assertSameBytes(placeWallItemPacket(false), placeWallItem(false).toPacket());
        assertSameBytes(placeWallItemPacket(true), placeWallItem(true).toPacket());
    }

    @Test
    void placeWallItemFromPacket() {
        assertEquals(placeWallItem(false), BuildersClubPlaceWallItem.fromPacket(placeWallItemPacket(false)));
        assertEquals(placeWallItem(true), BuildersClubPlaceWallItem.fromPacket(placeWallItemPacket(true)));
    }

    // ---- BuildersClubQueryFurniCount ----

    static HPacket queryFurniCountPacket() {
        return new HPacket("BuildersClubQueryFurniCount", HMessage.Direction.TOSERVER);
    }

    @Test
    void queryFurniCountToPacket() {
        assertSameBytes(queryFurniCountPacket(), new BuildersClubQueryFurniCount().toPacket());
    }

    @Test
    void queryFurniCountFromPacket() {
        assertEquals(new BuildersClubQueryFurniCount(), BuildersClubQueryFurniCount.fromPacket(queryFurniCountPacket()));
    }
}
