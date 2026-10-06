package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.BannedUsersFromRoom;
import me.roboroads.gearth.gpackets.incoming.FlatControllerAdded;
import me.roboroads.gearth.gpackets.incoming.FlatControllerRemoved;
import me.roboroads.gearth.gpackets.incoming.FlatControllers;
import me.roboroads.gearth.gpackets.incoming.UserUnbannedFromRoom;
import me.roboroads.gearth.gpackets.incoming.YouAreController;
import me.roboroads.gearth.gpackets.incoming.YouAreNotController;
import me.roboroads.gearth.gpackets.incoming.YouAreOwner;
import me.roboroads.gearth.gpackets.incoming.sub.room.BannedUser;
import me.roboroads.gearth.gpackets.incoming.sub.room.FlatController;
import me.roboroads.gearth.gpackets.model.enums.RoomBanDuration;
import me.roboroads.gearth.gpackets.model.enums.RoomControllerLevel;
import me.roboroads.gearth.gpackets.model.enums.RoomMuteDuration;
import me.roboroads.gearth.gpackets.outgoing.AmbassadorAlert;
import me.roboroads.gearth.gpackets.outgoing.AssignRights;
import me.roboroads.gearth.gpackets.outgoing.BanUserWithDuration;
import me.roboroads.gearth.gpackets.outgoing.GetBannedUsersFromRoom;
import me.roboroads.gearth.gpackets.outgoing.GetFlatControllers;
import me.roboroads.gearth.gpackets.outgoing.KickUser;
import me.roboroads.gearth.gpackets.outgoing.MuteUser;
import me.roboroads.gearth.gpackets.outgoing.RemoveAllRights;
import me.roboroads.gearth.gpackets.outgoing.RemoveOwnRoomRightsRoom;
import me.roboroads.gearth.gpackets.outgoing.RemoveRights;
import me.roboroads.gearth.gpackets.outgoing.UnbanUserFromRoom;
import me.roboroads.gearth.gpackets.outgoing.UnmuteUser;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomRightsWireFormatTest {

    // ---- BannedUsersFromRoom ----

    static BannedUsersFromRoom bannedUsersFromRoom() {
        return new BannedUsersFromRoom(4242, Arrays.asList(new BannedUser(12345, "Roboroads"), new BannedUser(67890, "Other")));
    }

    static HPacket bannedUsersFromRoomPacket() {
        HPacket p = new HPacket("BannedUsersFromRoom", HMessage.Direction.TOCLIENT);
        p.appendInt(4242).appendInt(2)
                .appendInt(12345).appendString("Roboroads")
                .appendInt(67890).appendString("Other");
        return p;
    }

    @Test
    void bannedUsersFromRoomToPacket() {
        assertSameBytes(bannedUsersFromRoomPacket(), bannedUsersFromRoom().toPacket());
    }

    @Test
    void bannedUsersFromRoomFromPacket() {
        assertEquals(bannedUsersFromRoom(), BannedUsersFromRoom.fromPacket(bannedUsersFromRoomPacket()));
    }

    // ---- FlatControllerAdded ----

    static HPacket flatControllerAddedPacket() {
        HPacket p = new HPacket("FlatControllerAdded", HMessage.Direction.TOCLIENT);
        p.appendInt(4242).appendInt(12345).appendString("Roboroads");
        return p;
    }

    @Test
    void flatControllerAddedToPacket() {
        assertSameBytes(flatControllerAddedPacket(), new FlatControllerAdded(4242, new FlatController(12345, "Roboroads")).toPacket());
    }

    @Test
    void flatControllerAddedFromPacket() {
        assertEquals(new FlatControllerAdded(4242, new FlatController(12345, "Roboroads")),
                FlatControllerAdded.fromPacket(flatControllerAddedPacket()));
    }

    // ---- FlatControllerRemoved ----

    static HPacket flatControllerRemovedPacket() {
        HPacket p = new HPacket("FlatControllerRemoved", HMessage.Direction.TOCLIENT);
        p.appendInt(4242).appendInt(12345);
        return p;
    }

    @Test
    void flatControllerRemovedToPacket() {
        assertSameBytes(flatControllerRemovedPacket(), new FlatControllerRemoved(4242, 12345).toPacket());
    }

    @Test
    void flatControllerRemovedFromPacket() {
        assertEquals(new FlatControllerRemoved(4242, 12345), FlatControllerRemoved.fromPacket(flatControllerRemovedPacket()));
    }

    // ---- FlatControllers ----

    static FlatControllers flatControllers() {
        return new FlatControllers(4242, Arrays.asList(new FlatController(12345, "Roboroads"), new FlatController(67890, "Other")));
    }

    static HPacket flatControllersPacket() {
        HPacket p = new HPacket("FlatControllers", HMessage.Direction.TOCLIENT);
        p.appendInt(4242).appendInt(2)
                .appendInt(12345).appendString("Roboroads")
                .appendInt(67890).appendString("Other");
        return p;
    }

    @Test
    void flatControllersToPacket() {
        assertSameBytes(flatControllersPacket(), flatControllers().toPacket());
    }

    @Test
    void flatControllersFromPacket() {
        assertEquals(flatControllers(), FlatControllers.fromPacket(flatControllersPacket()));
    }

    @Test
    void flatControllersReadsAnEmptyList() {
        HPacket p = new HPacket("FlatControllers", HMessage.Direction.TOCLIENT);
        p.appendInt(4242).appendInt(0);
        assertEquals(new FlatControllers(4242, Collections.emptyList()), FlatControllers.fromPacket(p));
    }

    // ---- MuteAllInRoom (incoming) ----

    static HPacket muteAllInRoomIncomingPacket() {
        HPacket p = new HPacket("MuteAllInRoom", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true);
        return p;
    }

    @Test
    void muteAllInRoomIncomingToPacket() {
        assertSameBytes(muteAllInRoomIncomingPacket(), new me.roboroads.gearth.gpackets.incoming.MuteAllInRoom(true).toPacket());
    }

    @Test
    void muteAllInRoomIncomingFromPacket() {
        assertEquals(new me.roboroads.gearth.gpackets.incoming.MuteAllInRoom(true),
                me.roboroads.gearth.gpackets.incoming.MuteAllInRoom.fromPacket(muteAllInRoomIncomingPacket()));
    }

    // ---- UserUnbannedFromRoom ----

    static HPacket userUnbannedFromRoomPacket() {
        HPacket p = new HPacket("UserUnbannedFromRoom", HMessage.Direction.TOCLIENT);
        p.appendInt(4242).appendInt(12345);
        return p;
    }

    @Test
    void userUnbannedFromRoomToPacket() {
        assertSameBytes(userUnbannedFromRoomPacket(), new UserUnbannedFromRoom(4242, 12345).toPacket());
    }

    @Test
    void userUnbannedFromRoomFromPacket() {
        assertEquals(new UserUnbannedFromRoom(4242, 12345), UserUnbannedFromRoom.fromPacket(userUnbannedFromRoomPacket()));
    }

    // ---- YouAreController ----

    static HPacket youAreControllerPacket() {
        HPacket p = new HPacket("YouAreController", HMessage.Direction.TOCLIENT);
        p.appendInt(4242).appendInt(4);
        return p;
    }

    @Test
    void youAreControllerToPacket() {
        assertSameBytes(youAreControllerPacket(), new YouAreController(4242, RoomControllerLevel.ROOM_OWNER).toPacket());
    }

    @Test
    void youAreControllerFromPacket() {
        assertEquals(new YouAreController(4242, RoomControllerLevel.ROOM_OWNER), YouAreController.fromPacket(youAreControllerPacket()));
    }

    // ---- YouAreNotController ----

    static HPacket youAreNotControllerPacket() {
        HPacket p = new HPacket("YouAreNotController", HMessage.Direction.TOCLIENT);
        p.appendInt(4242);
        return p;
    }

    @Test
    void youAreNotControllerToPacket() {
        assertSameBytes(youAreNotControllerPacket(), new YouAreNotController(4242).toPacket());
    }

    @Test
    void youAreNotControllerFromPacket() {
        assertEquals(new YouAreNotController(4242), YouAreNotController.fromPacket(youAreNotControllerPacket()));
    }

    // ---- YouAreOwner ----

    static HPacket youAreOwnerPacket() {
        HPacket p = new HPacket("YouAreOwner", HMessage.Direction.TOCLIENT);
        p.appendInt(4242);
        return p;
    }

    @Test
    void youAreOwnerToPacket() {
        assertSameBytes(youAreOwnerPacket(), new YouAreOwner(4242).toPacket());
    }

    @Test
    void youAreOwnerFromPacket() {
        assertEquals(new YouAreOwner(4242), YouAreOwner.fromPacket(youAreOwnerPacket()));
    }

    // ---- AmbassadorAlert ----

    static HPacket ambassadorAlertPacket() {
        HPacket p = new HPacket("AmbassadorAlert", HMessage.Direction.TOSERVER);
        p.appendInt(12345);
        return p;
    }

    @Test
    void ambassadorAlertToPacket() {
        assertSameBytes(ambassadorAlertPacket(), new AmbassadorAlert(12345).toPacket());
    }

    @Test
    void ambassadorAlertFromPacket() {
        assertEquals(new AmbassadorAlert(12345), AmbassadorAlert.fromPacket(ambassadorAlertPacket()));
    }

    // ---- AssignRights ----

    static HPacket assignRightsPacket() {
        HPacket p = new HPacket("AssignRights", HMessage.Direction.TOSERVER);
        p.appendInt(12345);
        return p;
    }

    @Test
    void assignRightsToPacket() {
        assertSameBytes(assignRightsPacket(), new AssignRights(12345).toPacket());
    }

    @Test
    void assignRightsFromPacket() {
        assertEquals(new AssignRights(12345), AssignRights.fromPacket(assignRightsPacket()));
    }

    // ---- BanUserWithDuration ----

    static HPacket banUserWithDurationPacket() {
        HPacket p = new HPacket("BanUserWithDuration", HMessage.Direction.TOSERVER);
        p.appendInt(12345).appendInt(4242).appendString("RWUAM_BAN_USER_DAY");
        return p;
    }

    @Test
    void banUserWithDurationToPacket() {
        assertSameBytes(banUserWithDurationPacket(), new BanUserWithDuration(12345, 4242, RoomBanDuration.DAY).toPacket());
    }

    @Test
    void banUserWithDurationFromPacket() {
        assertEquals(new BanUserWithDuration(12345, 4242, RoomBanDuration.DAY),
                BanUserWithDuration.fromPacket(banUserWithDurationPacket()));
    }

    // ---- GetBannedUsersFromRoom ----

    static HPacket getBannedUsersFromRoomPacket() {
        HPacket p = new HPacket("GetBannedUsersFromRoom", HMessage.Direction.TOSERVER);
        p.appendInt(4242);
        return p;
    }

    @Test
    void getBannedUsersFromRoomToPacket() {
        assertSameBytes(getBannedUsersFromRoomPacket(), new GetBannedUsersFromRoom(4242).toPacket());
    }

    @Test
    void getBannedUsersFromRoomFromPacket() {
        assertEquals(new GetBannedUsersFromRoom(4242), GetBannedUsersFromRoom.fromPacket(getBannedUsersFromRoomPacket()));
    }

    // ---- GetFlatControllers ----

    static HPacket getFlatControllersPacket() {
        HPacket p = new HPacket("GetFlatControllers", HMessage.Direction.TOSERVER);
        p.appendInt(4242);
        return p;
    }

    @Test
    void getFlatControllersToPacket() {
        assertSameBytes(getFlatControllersPacket(), new GetFlatControllers(4242).toPacket());
    }

    @Test
    void getFlatControllersFromPacket() {
        assertEquals(new GetFlatControllers(4242), GetFlatControllers.fromPacket(getFlatControllersPacket()));
    }

    // ---- KickUser ----

    static HPacket kickUserPacket() {
        HPacket p = new HPacket("KickUser", HMessage.Direction.TOSERVER);
        p.appendInt(12345);
        return p;
    }

    @Test
    void kickUserToPacket() {
        assertSameBytes(kickUserPacket(), new KickUser(12345).toPacket());
    }

    @Test
    void kickUserFromPacket() {
        assertEquals(new KickUser(12345), KickUser.fromPacket(kickUserPacket()));
    }

    // ---- MuteAllInRoom (outgoing) ----

    static HPacket muteAllInRoomOutgoingPacket() {
        return new HPacket("MuteAllInRoom", HMessage.Direction.TOSERVER);
    }

    @Test
    void muteAllInRoomOutgoingToPacket() {
        assertSameBytes(muteAllInRoomOutgoingPacket(), new me.roboroads.gearth.gpackets.outgoing.MuteAllInRoom().toPacket());
    }

    @Test
    void muteAllInRoomOutgoingFromPacket() {
        assertEquals(new me.roboroads.gearth.gpackets.outgoing.MuteAllInRoom(),
                me.roboroads.gearth.gpackets.outgoing.MuteAllInRoom.fromPacket(muteAllInRoomOutgoingPacket()));
    }

    // ---- MuteUser ----

    static HPacket muteUserPacket() {
        HPacket p = new HPacket("MuteUser", HMessage.Direction.TOSERVER);
        p.appendInt(12345).appendInt(4242).appendInt(1080);
        return p;
    }

    @Test
    void muteUserToPacket() {
        assertSameBytes(muteUserPacket(), new MuteUser(12345, 4242, RoomMuteDuration.HOURS_18).toPacket());
    }

    @Test
    void muteUserFromPacket() {
        assertEquals(new MuteUser(12345, 4242, RoomMuteDuration.HOURS_18), MuteUser.fromPacket(muteUserPacket()));
    }

    // ---- RemoveAllRights ----

    static HPacket removeAllRightsPacket() {
        HPacket p = new HPacket("RemoveAllRights", HMessage.Direction.TOSERVER);
        p.appendInt(4242);
        return p;
    }

    @Test
    void removeAllRightsToPacket() {
        assertSameBytes(removeAllRightsPacket(), new RemoveAllRights(4242).toPacket());
    }

    @Test
    void removeAllRightsFromPacket() {
        assertEquals(new RemoveAllRights(4242), RemoveAllRights.fromPacket(removeAllRightsPacket()));
    }

    // ---- RemoveOwnRoomRightsRoom ----

    static HPacket removeOwnRoomRightsRoomPacket() {
        HPacket p = new HPacket("RemoveOwnRoomRightsRoom", HMessage.Direction.TOSERVER);
        p.appendInt(4242);
        return p;
    }

    @Test
    void removeOwnRoomRightsRoomToPacket() {
        assertSameBytes(removeOwnRoomRightsRoomPacket(), new RemoveOwnRoomRightsRoom(4242).toPacket());
    }

    @Test
    void removeOwnRoomRightsRoomFromPacket() {
        assertEquals(new RemoveOwnRoomRightsRoom(4242), RemoveOwnRoomRightsRoom.fromPacket(removeOwnRoomRightsRoomPacket()));
    }

    // ---- RemoveRights ----

    static HPacket removeRightsPacket() {
        HPacket p = new HPacket("RemoveRights", HMessage.Direction.TOSERVER);
        p.appendInt(1).appendInt(12345);
        return p;
    }

    @Test
    void removeRightsToPacket() {
        assertSameBytes(removeRightsPacket(), new RemoveRights(Collections.singletonList(12345)).toPacket());
    }

    @Test
    void removeRightsFromPacket() {
        assertEquals(new RemoveRights(Collections.singletonList(12345)), RemoveRights.fromPacket(removeRightsPacket()));
    }

    @Test
    void removeRightsTakesOneUserAtATime() {
        RemoveRights twoUsers = new RemoveRights(Arrays.asList(12345, 67890));

        LimitException e = assertThrows(LimitException.class, twoUsers::toPacket);

        assertEquals("RemoveRights breaks 1 limit (use toPacketUnchecked() to send it anyway):\n"
                + "  userIds: at most 1 items, got 2", e.getMessage());
        HPacket expected = new HPacket("RemoveRights", HMessage.Direction.TOSERVER);
        expected.appendInt(2).appendInt(12345).appendInt(67890);
        assertSameBytes(expected, twoUsers.toPacketUnchecked());
    }

    // ---- UnbanUserFromRoom ----

    static HPacket unbanUserFromRoomPacket() {
        HPacket p = new HPacket("UnbanUserFromRoom", HMessage.Direction.TOSERVER);
        p.appendInt(12345).appendInt(4242);
        return p;
    }

    @Test
    void unbanUserFromRoomToPacket() {
        assertSameBytes(unbanUserFromRoomPacket(), new UnbanUserFromRoom(12345, 4242).toPacket());
    }

    @Test
    void unbanUserFromRoomFromPacket() {
        assertEquals(new UnbanUserFromRoom(12345, 4242), UnbanUserFromRoom.fromPacket(unbanUserFromRoomPacket()));
    }

    // ---- UnmuteUser ----

    static HPacket unmuteUserPacket() {
        HPacket p = new HPacket("UnmuteUser", HMessage.Direction.TOSERVER);
        p.appendInt(12345).appendInt(4242);
        return p;
    }

    @Test
    void unmuteUserToPacket() {
        assertSameBytes(unmuteUserPacket(), new UnmuteUser(12345, 4242).toPacket());
    }

    @Test
    void unmuteUserFromPacket() {
        assertEquals(new UnmuteUser(12345, 4242), UnmuteUser.fromPacket(unmuteUserPacket()));
    }
}
