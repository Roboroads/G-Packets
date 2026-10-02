package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.CantConnect;
import me.roboroads.gearth.gpackets.incoming.CloseConnection;
import me.roboroads.gearth.gpackets.incoming.ConfigurationItemStates;
import me.roboroads.gearth.gpackets.incoming.Doorbell;
import me.roboroads.gearth.gpackets.incoming.FlatAccessDenied;
import me.roboroads.gearth.gpackets.incoming.FlatAccessible;
import me.roboroads.gearth.gpackets.incoming.GamePlayerValue;
import me.roboroads.gearth.gpackets.incoming.OpenConnection;
import me.roboroads.gearth.gpackets.incoming.RoomForward;
import me.roboroads.gearth.gpackets.incoming.RoomQueueStatus;
import me.roboroads.gearth.gpackets.incoming.RoomReady;
import me.roboroads.gearth.gpackets.incoming.YouAreNotSpectator;
import me.roboroads.gearth.gpackets.incoming.YouArePlayingGame;
import me.roboroads.gearth.gpackets.incoming.YouAreSpectator;
import me.roboroads.gearth.gpackets.incoming.sub.room.RoomQueue;
import me.roboroads.gearth.gpackets.incoming.sub.room.RoomQueueSet;
import me.roboroads.gearth.gpackets.model.enums.CantConnectReason;
import me.roboroads.gearth.gpackets.model.enums.RoomQueueTarget;
import me.roboroads.gearth.gpackets.outgoing.ChangeQueue;
import me.roboroads.gearth.gpackets.outgoing.LetUserIn;
import me.roboroads.gearth.gpackets.outgoing.OpenFlatConnection;
import me.roboroads.gearth.gpackets.outgoing.Quit;
import me.roboroads.gearth.gpackets.outgoing.RoomNetworkOpenConnection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation") // tests RoomQueueSet's unused name
class RoomEntryWireFormatTest {

    // ---- CantConnect ----

    static HPacket cantConnectPacket() {
        HPacket p = new HPacket("CantConnect", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendString("queue_full");
        return p;
    }

    @Test
    void cantConnectToPacket() {
        assertSameBytes(cantConnectPacket(), new CantConnect(CantConnectReason.QUEUE_ERROR, "queue_full").toPacket());
    }

    @Test
    void cantConnectFromPacket() {
        assertEquals(new CantConnect(CantConnectReason.QUEUE_ERROR, "queue_full"), CantConnect.fromPacket(cantConnectPacket()));
    }

    static HPacket cantConnectBannedPacket() {
        HPacket p = new HPacket("CantConnect", HMessage.Direction.TOCLIENT);
        p.appendInt(4);
        return p;
    }

    @Test
    void cantConnectOnlySendsTheParameterForAQueueError() {
        assertSameBytes(cantConnectBannedPacket(), new CantConnect(CantConnectReason.BANNED, "ignored").toPacket());
        assertEquals(new CantConnect(CantConnectReason.BANNED, null), CantConnect.fromPacket(cantConnectBannedPacket()));
    }

    @Test
    void cantConnectKeepsAnUnnamedReason() {
        HPacket p = new HPacket("CantConnect", HMessage.Direction.TOCLIENT);
        p.appendInt(2);
        CantConnect parsed = CantConnect.fromPacket(p);
        assertEquals(2, parsed.reason().value());
        assertSameBytes(p, parsed.toPacket());
    }

    // ---- CloseConnection ----

    static HPacket closeConnectionPacket() {
        return new HPacket("CloseConnection", HMessage.Direction.TOCLIENT);
    }

    @Test
    void closeConnectionToPacket() {
        assertSameBytes(closeConnectionPacket(), new CloseConnection().toPacket());
    }

    @Test
    void closeConnectionFromPacket() {
        assertEquals(new CloseConnection(), CloseConnection.fromPacket(closeConnectionPacket()));
    }

    // ---- ConfigurationItemStates ----

    static HPacket configurationItemStatesPacket() {
        HPacket p = new HPacket("ConfigurationItemStates", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true).appendBoolean(false).appendBoolean(true).appendBoolean(true);
        return p;
    }

    @Test
    void configurationItemStatesToPacket() {
        assertSameBytes(configurationItemStatesPacket(), new ConfigurationItemStates(true, false, true, true).toPacket());
    }

    @Test
    void configurationItemStatesFromPacket() {
        assertEquals(new ConfigurationItemStates(true, false, true, true),
                ConfigurationItemStates.fromPacket(configurationItemStatesPacket()));
    }

    static HPacket configurationItemStatesShortPacket() {
        HPacket p = new HPacket("ConfigurationItemStates", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true).appendBoolean(true);
        return p;
    }

    @Test
    void configurationItemStatesMayStopAfterAnyValue() {
        assertSameBytes(configurationItemStatesShortPacket(), new ConfigurationItemStates(true, true, null, null).toPacket());
        assertEquals(new ConfigurationItemStates(true, true, null, null),
                ConfigurationItemStates.fromPacket(configurationItemStatesShortPacket()));
    }

    // ---- Doorbell ----

    static HPacket doorbellPacket() {
        HPacket p = new HPacket("Doorbell", HMessage.Direction.TOCLIENT);
        p.appendString("Roboroads");
        return p;
    }

    @Test
    void doorbellToPacket() {
        assertSameBytes(doorbellPacket(), new Doorbell("Roboroads").toPacket());
    }

    @Test
    void doorbellFromPacket() {
        assertEquals(new Doorbell("Roboroads"), Doorbell.fromPacket(doorbellPacket()));
    }

    // ---- FlatAccessDenied ----

    static HPacket flatAccessDeniedPacket() {
        HPacket p = new HPacket("FlatAccessDenied", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendString("Roboroads");
        return p;
    }

    @Test
    void flatAccessDeniedToPacket() {
        assertSameBytes(flatAccessDeniedPacket(), new FlatAccessDenied(12345, "Roboroads").toPacket());
    }

    @Test
    void flatAccessDeniedFromPacket() {
        assertEquals(new FlatAccessDenied(12345, "Roboroads"), FlatAccessDenied.fromPacket(flatAccessDeniedPacket()));
    }

    static HPacket flatAccessDeniedWithoutNamePacket() {
        HPacket p = new HPacket("FlatAccessDenied", HMessage.Direction.TOCLIENT);
        p.appendInt(12345);
        return p;
    }

    @Test
    void flatAccessDeniedMayLeaveOutTheName() {
        assertSameBytes(flatAccessDeniedWithoutNamePacket(), new FlatAccessDenied(12345, null).toPacket());
        assertEquals(new FlatAccessDenied(12345, null), FlatAccessDenied.fromPacket(flatAccessDeniedWithoutNamePacket()));
    }

    // ---- FlatAccessible ----

    static HPacket flatAccessiblePacket() {
        HPacket p = new HPacket("FlatAccessible", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendString("");
        return p;
    }

    @Test
    void flatAccessibleToPacket() {
        assertSameBytes(flatAccessiblePacket(), new FlatAccessible(12345, "").toPacket());
    }

    @Test
    void flatAccessibleFromPacket() {
        assertEquals(new FlatAccessible(12345, ""), FlatAccessible.fromPacket(flatAccessiblePacket()));
    }

    // ---- GamePlayerValue ----

    static HPacket gamePlayerValuePacket() {
        HPacket p = new HPacket("GamePlayerValue", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(42);
        return p;
    }

    @Test
    void gamePlayerValueToPacket() {
        assertSameBytes(gamePlayerValuePacket(), new GamePlayerValue(3, 42).toPacket());
    }

    @Test
    void gamePlayerValueFromPacket() {
        assertEquals(new GamePlayerValue(3, 42), GamePlayerValue.fromPacket(gamePlayerValuePacket()));
    }

    // ---- OpenConnection ----

    static HPacket openConnectionPacket() {
        HPacket p = new HPacket("OpenConnection", HMessage.Direction.TOCLIENT);
        p.appendInt(12345);
        return p;
    }

    @Test
    void openConnectionToPacket() {
        assertSameBytes(openConnectionPacket(), new OpenConnection(12345).toPacket());
    }

    @Test
    void openConnectionFromPacket() {
        assertEquals(new OpenConnection(12345), OpenConnection.fromPacket(openConnectionPacket()));
    }

    // ---- RoomForward ----

    static HPacket roomForwardPacket() {
        HPacket p = new HPacket("RoomForward", HMessage.Direction.TOCLIENT);
        p.appendInt(67890);
        return p;
    }

    @Test
    void roomForwardToPacket() {
        assertSameBytes(roomForwardPacket(), new RoomForward(67890).toPacket());
    }

    @Test
    void roomForwardFromPacket() {
        assertEquals(new RoomForward(67890), RoomForward.fromPacket(roomForwardPacket()));
    }

    // ---- RoomQueueStatus ----

    static RoomQueueStatus roomQueueStatus() {
        return new RoomQueueStatus(12345, Arrays.asList(
                new RoomQueueSet("visitors", RoomQueueTarget.VISITOR, Arrays.asList(new RoomQueue("c", 2), new RoomQueue("d", 7))),
                new RoomQueueSet("spectators", RoomQueueTarget.SPECTATOR, Collections.singletonList(new RoomQueue("d", 0)))));
    }

    static HPacket roomQueueStatusPacket() {
        HPacket p = new HPacket("RoomQueueStatus", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendInt(2);
        p.appendString("visitors").appendInt(2).appendInt(2);
        p.appendString("c").appendInt(2);
        p.appendString("d").appendInt(7);
        p.appendString("spectators").appendInt(1).appendInt(1);
        p.appendString("d").appendInt(0);
        return p;
    }

    @Test
    void roomQueueStatusToPacket() {
        assertSameBytes(roomQueueStatusPacket(), roomQueueStatus().toPacket());
    }

    @Test
    void roomQueueStatusFromPacket() {
        assertEquals(roomQueueStatus(), RoomQueueStatus.fromPacket(roomQueueStatusPacket()));
    }

    // ---- RoomReady ----

    static HPacket roomReadyPacket() {
        HPacket p = new HPacket("RoomReady", HMessage.Direction.TOCLIENT);
        p.appendString("model_a").appendInt(12345);
        return p;
    }

    @Test
    void roomReadyToPacket() {
        assertSameBytes(roomReadyPacket(), new RoomReady("model_a", 12345).toPacket());
    }

    @Test
    void roomReadyFromPacket() {
        assertEquals(new RoomReady("model_a", 12345), RoomReady.fromPacket(roomReadyPacket()));
    }

    // ---- YouAreNotSpectator ----

    static HPacket youAreNotSpectatorPacket() {
        HPacket p = new HPacket("YouAreNotSpectator", HMessage.Direction.TOCLIENT);
        p.appendInt(12345);
        return p;
    }

    @Test
    void youAreNotSpectatorToPacket() {
        assertSameBytes(youAreNotSpectatorPacket(), new YouAreNotSpectator(12345).toPacket());
    }

    @Test
    void youAreNotSpectatorFromPacket() {
        assertEquals(new YouAreNotSpectator(12345), YouAreNotSpectator.fromPacket(youAreNotSpectatorPacket()));
    }

    // ---- YouArePlayingGame ----

    static HPacket youArePlayingGamePacket() {
        HPacket p = new HPacket("YouArePlayingGame", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true);
        return p;
    }

    @Test
    void youArePlayingGameToPacket() {
        assertSameBytes(youArePlayingGamePacket(), new YouArePlayingGame(true).toPacket());
    }

    @Test
    void youArePlayingGameFromPacket() {
        assertEquals(new YouArePlayingGame(true), YouArePlayingGame.fromPacket(youArePlayingGamePacket()));
    }

    // ---- YouAreSpectator ----

    static HPacket youAreSpectatorPacket() {
        HPacket p = new HPacket("YouAreSpectator", HMessage.Direction.TOCLIENT);
        p.appendInt(12345);
        return p;
    }

    @Test
    void youAreSpectatorToPacket() {
        assertSameBytes(youAreSpectatorPacket(), new YouAreSpectator(12345).toPacket());
    }

    @Test
    void youAreSpectatorFromPacket() {
        assertEquals(new YouAreSpectator(12345), YouAreSpectator.fromPacket(youAreSpectatorPacket()));
    }

    // ---- ChangeQueue ----

    static HPacket changeQueuePacket() {
        HPacket p = new HPacket("ChangeQueue", HMessage.Direction.TOSERVER);
        p.appendInt(1);
        return p;
    }

    @Test
    void changeQueueToPacket() {
        assertSameBytes(changeQueuePacket(), new ChangeQueue(RoomQueueTarget.SPECTATOR).toPacket());
    }

    @Test
    void changeQueueFromPacket() {
        assertEquals(new ChangeQueue(RoomQueueTarget.SPECTATOR), ChangeQueue.fromPacket(changeQueuePacket()));
    }

    // ---- LetUserIn ----

    static HPacket letUserInPacket() {
        HPacket p = new HPacket("LetUserIn", HMessage.Direction.TOSERVER);
        p.appendString("Roboroads").appendBoolean(true);
        return p;
    }

    @Test
    void letUserInToPacket() {
        assertSameBytes(letUserInPacket(), new LetUserIn("Roboroads", true).toPacket());
    }

    @Test
    void letUserInFromPacket() {
        assertEquals(new LetUserIn("Roboroads", true), LetUserIn.fromPacket(letUserInPacket()));
    }

    // ---- OpenFlatConnection ----

    static HPacket openFlatConnectionPacket() {
        HPacket p = new HPacket("OpenFlatConnection", HMessage.Direction.TOSERVER);
        p.appendInt(12345).appendString("secret").appendInt(-1);
        return p;
    }

    @Test
    void openFlatConnectionToPacket() {
        assertSameBytes(openFlatConnectionPacket(), new OpenFlatConnection(12345, "secret", -1).toPacket());
    }

    @Test
    void openFlatConnectionFromPacket() {
        assertEquals(new OpenFlatConnection(12345, "secret", -1), OpenFlatConnection.fromPacket(openFlatConnectionPacket()));
    }

    @Test
    void openFlatConnectionSendsMinusOneLikeTheClientByDefault() {
        assertSameBytes(openFlatConnectionPacket(), OpenFlatConnection.builder().roomId(12345).password("secret").build().toPacket());
    }

    // ---- Quit ----

    static HPacket quitPacket() {
        return new HPacket("Quit", HMessage.Direction.TOSERVER);
    }

    @Test
    void quitToPacket() {
        assertSameBytes(quitPacket(), new Quit().toPacket());
    }

    @Test
    void quitFromPacket() {
        assertEquals(new Quit(), Quit.fromPacket(quitPacket()));
    }

    // ---- RoomNetworkOpenConnection ----

    static HPacket roomNetworkOpenConnectionPacket() {
        HPacket p = new HPacket("RoomNetworkOpenConnection", HMessage.Direction.TOSERVER);
        p.appendInt(7).appendInt(0);
        return p;
    }

    @Test
    void roomNetworkOpenConnectionToPacket() {
        assertSameBytes(roomNetworkOpenConnectionPacket(), new RoomNetworkOpenConnection(7, 0).toPacket());
    }

    @Test
    void roomNetworkOpenConnectionFromPacket() {
        assertEquals(new RoomNetworkOpenConnection(7, 0), RoomNetworkOpenConnection.fromPacket(roomNetworkOpenConnectionPacket()));
    }
}
