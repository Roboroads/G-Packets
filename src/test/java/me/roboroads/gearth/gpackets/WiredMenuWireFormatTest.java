package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.WiredAllVariableHolders;
import me.roboroads.gearth.gpackets.incoming.WiredAllVariablesDiffs;
import me.roboroads.gearth.gpackets.incoming.WiredAllVariablesHash;
import me.roboroads.gearth.gpackets.incoming.WiredErrorLogs;
import me.roboroads.gearth.gpackets.incoming.WiredMenuError;
import me.roboroads.gearth.gpackets.incoming.WiredPermissions;
import me.roboroads.gearth.gpackets.incoming.WiredRoomLogs;
import me.roboroads.gearth.gpackets.incoming.WiredRoomStats;
import me.roboroads.gearth.gpackets.incoming.WiredSetUserPermanentVariableResult;
import me.roboroads.gearth.gpackets.incoming.WiredUserPermanentVariables;
import me.roboroads.gearth.gpackets.incoming.WiredUserVariablesList;
import me.roboroads.gearth.gpackets.incoming.WiredVariablesForObject;
import me.roboroads.gearth.gpackets.incoming.sub.wired.HashedVariable;
import me.roboroads.gearth.gpackets.incoming.sub.wired.StoredVariable;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableHash;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableHolder;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableOwner;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableStorage;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableTextConnection;
import me.roboroads.gearth.gpackets.incoming.sub.wired.VariableValue;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredError;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredLogEntry;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredVariable;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.model.enums.WiredLogLevel;
import me.roboroads.gearth.gpackets.model.enums.WiredLogSource;
import me.roboroads.gearth.gpackets.model.enums.WiredMenuErrorCode;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableAction;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableAvailability;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableSort;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableTarget;
import me.roboroads.gearth.gpackets.model.enums.WiredVariableType;
import me.roboroads.gearth.gpackets.outgoing.WiredClearErrorLogs;
import me.roboroads.gearth.gpackets.outgoing.WiredDeleteAllVariableHolders;
import me.roboroads.gearth.gpackets.outgoing.WiredGetAllVariableHolders;
import me.roboroads.gearth.gpackets.outgoing.WiredGetAllVariablesDiffs;
import me.roboroads.gearth.gpackets.outgoing.WiredGetAllVariablesHash;
import me.roboroads.gearth.gpackets.outgoing.WiredGetErrorLogs;
import me.roboroads.gearth.gpackets.outgoing.WiredGetRoomLogs;
import me.roboroads.gearth.gpackets.outgoing.WiredGetRoomStats;
import me.roboroads.gearth.gpackets.outgoing.WiredGetUserPermanentVariables;
import me.roboroads.gearth.gpackets.outgoing.WiredGetVariableOwnersPage;
import me.roboroads.gearth.gpackets.outgoing.WiredGetVariablesForObject;
import me.roboroads.gearth.gpackets.outgoing.WiredSetObjectVariableValue;
import me.roboroads.gearth.gpackets.outgoing.WiredSetPreferences;
import me.roboroads.gearth.gpackets.outgoing.WiredSetUserPermanentVariable;
import me.roboroads.gearth.gpackets.outgoing.WiredUpdateRoom;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("deprecation") // tests the unused WiredAllVariableHolders.unknownInt1, ownerFigure and timestamp
class WiredMenuWireFormatTest {

    // ---- shared samples ----

    // A user variable with a text connector.
    static WiredVariable scoreVariable() {
        return WiredVariable.builder()
                .variableId("u_score").variableType(WiredVariableType.CREATED_BY_USER).variableName("score")
                .availabilityType(WiredVariableAvailability.PERMANENT).variableTarget(WiredVariableTarget.USER)
                .alwaysAvailable(false).canCreateAndDelete(true).hasValue(true).canWriteValue(true)
                .canInterceptChanges(true).isInvisible(false).canReadCreationTime(true).canReadLastUpdateTime(false)
                .hasTextConnector(true)
                .textConnector(Arrays.asList(new VariableTextConnection(1, "bronze"), new VariableTextConnection(2, "silver")))
                .build();
    }

    static void appendScoreVariable(HPacket p) {
        p.appendString("u_score").appendInt(0).appendString("score").appendInt(10).appendInt(1)
                .appendBoolean(false).appendBoolean(true).appendBoolean(true).appendBoolean(true)
                .appendBoolean(true).appendBoolean(false).appendBoolean(true).appendBoolean(false)
                .appendBoolean(true)
                .appendInt(2).appendInt(1).appendString("bronze").appendInt(2).appendString("silver");
    }

    // An internal furni variable without a text connector, with an availability the library doesn't name.
    static WiredVariable stateVariable() {
        return WiredVariable.builder()
                .variableId("f_state").variableType(WiredVariableType.INTERNAL).variableName("@state")
                .availabilityType(WiredVariableAvailability.of(21)).variableTarget(WiredVariableTarget.FURNI)
                .alwaysAvailable(true).canCreateAndDelete(false).hasValue(true).canWriteValue(false)
                .canInterceptChanges(false).isInvisible(true).canReadCreationTime(false).canReadLastUpdateTime(false)
                .hasTextConnector(false)
                .build();
    }

    static void appendStateVariable(HPacket p) {
        p.appendString("f_state").appendInt(1).appendString("@state").appendInt(21).appendInt(0)
                .appendBoolean(true).appendBoolean(false).appendBoolean(true).appendBoolean(false)
                .appendBoolean(false).appendBoolean(true).appendBoolean(false).appendBoolean(false)
                .appendBoolean(false);
    }

    static VariableStorage storage() {
        return new VariableStorage(42, 1759658400000L, "05-10-2026 12:00", 1759662000000L, "05-10-2026 13:00");
    }

    static void appendStorage(HPacket p) {
        p.appendInt(42).appendLong(1759658400000L).appendString("05-10-2026 12:00")
                .appendLong(1759662000000L).appendString("05-10-2026 13:00");
    }

    // ---- WiredAllVariableHolders ----

    static WiredAllVariableHolders allVariableHolders() {
        return new WiredAllVariableHolders(3, scoreVariable(),
                Arrays.asList(new VariableHolder(4, 100), new VariableHolder(9, -5)));
    }

    static HPacket allVariableHoldersPacket() {
        HPacket p = new HPacket("WiredAllVariableHolders", HMessage.Direction.TOCLIENT);
        p.appendInt(3);
        appendScoreVariable(p);
        p.appendInt(2).appendInt(4).appendInt(100).appendInt(9).appendInt(-5);
        return p;
    }

    @Test
    void wiredAllVariableHoldersToPacket() {
        assertSameBytes(allVariableHoldersPacket(), allVariableHolders().toPacket());
    }

    @Test
    void wiredAllVariableHoldersFromPacket() {
        assertEquals(allVariableHolders(), WiredAllVariableHolders.fromPacket(allVariableHoldersPacket()));
    }

    // ---- WiredAllVariablesDiffs ----

    static WiredAllVariablesDiffs allVariablesDiffs() {
        return new WiredAllVariablesDiffs(-123456, true, Collections.singletonList("u_old"),
                Arrays.asList(new HashedVariable(77, scoreVariable()), new HashedVariable(78, stateVariable())));
    }

    static HPacket allVariablesDiffsPacket() {
        HPacket p = new HPacket("WiredAllVariablesDiffs", HMessage.Direction.TOCLIENT);
        p.appendInt(-123456).appendBoolean(true).appendInt(1).appendString("u_old").appendInt(2);
        p.appendInt(77);
        appendScoreVariable(p);
        p.appendInt(78);
        appendStateVariable(p);
        return p;
    }

    @Test
    void wiredAllVariablesDiffsToPacket() {
        assertSameBytes(allVariablesDiffsPacket(), allVariablesDiffs().toPacket());
    }

    @Test
    void wiredAllVariablesDiffsFromPacket() {
        WiredAllVariablesDiffs parsed = WiredAllVariablesDiffs.fromPacket(allVariablesDiffsPacket());

        assertEquals(allVariablesDiffs(), parsed);
        WiredVariable state = parsed.addedOrUpdated().get(1).variable();
        assertNull(state.textConnector());
        assertEquals(21, state.availabilityType().value());
        assertFalse(state.availabilityType().known());
    }

    // ---- WiredAllVariablesHash ----

    @Test
    void wiredAllVariablesHashBothWays() {
        HPacket p = new HPacket("WiredAllVariablesHash", HMessage.Direction.TOCLIENT);
        p.appendInt(987654);

        assertSameBytes(p, new WiredAllVariablesHash(987654).toPacket());
        assertEquals(new WiredAllVariablesHash(987654), WiredAllVariablesHash.fromPacket(p));
    }

    // ---- WiredErrorLogs ----

    static WiredErrorLogs errorLogs() {
        return new WiredErrorLogs(Arrays.asList(
                new WiredError(3, "MARKED_AS_HEAVY", "performance", 2, 15000L),
                new WiredError(6, "TOO_MANY_VARIABLES", "variables", 0, -1L)));
    }

    static HPacket errorLogsPacket() {
        HPacket p = new HPacket("WiredErrorLogs", HMessage.Direction.TOCLIENT);
        p.appendInt(2)
                .appendInt(3).appendString("MARKED_AS_HEAVY").appendString("performance").appendInt(2).appendLong(15000L)
                .appendInt(6).appendString("TOO_MANY_VARIABLES").appendString("variables").appendInt(0).appendLong(-1L);
        return p;
    }

    @Test
    void wiredErrorLogsToPacket() {
        assertSameBytes(errorLogsPacket(), errorLogs().toPacket());
    }

    @Test
    void wiredErrorLogsFromPacket() {
        assertEquals(errorLogs(), WiredErrorLogs.fromPacket(errorLogsPacket()));
    }

    // ---- WiredMenuError ----

    @Test
    void wiredMenuErrorBothWaysAsAShort() {
        HPacket p = new HPacket("WiredMenuError", HMessage.Direction.TOCLIENT);
        p.appendShort((short) 7);

        assertSameBytes(p, new WiredMenuError(WiredMenuErrorCode.NOT_VARIABLE_OWNER).toPacket());
        assertEquals(new WiredMenuError(WiredMenuErrorCode.NOT_VARIABLE_OWNER), WiredMenuError.fromPacket(p));
    }

    @Test
    void wiredMenuErrorKeepsAnUnnamedCode() {
        HPacket p = new HPacket("WiredMenuError", HMessage.Direction.TOCLIENT);
        p.appendShort((short) 0);

        WiredMenuError parsed = WiredMenuError.fromPacket(p);

        assertEquals(0, parsed.errorCode().value());
        assertFalse(parsed.errorCode().known());
        assertSameBytes(p, parsed.toPacket());
    }

    // ---- WiredPermissions ----

    @Test
    void wiredPermissionsBothWays() {
        HPacket p = new HPacket("WiredPermissions", HMessage.Direction.TOCLIENT);
        p.appendBoolean(false).appendBoolean(true);

        assertSameBytes(p, new WiredPermissions(false, true).toPacket());
        assertEquals(new WiredPermissions(false, true), WiredPermissions.fromPacket(p));
    }

    // ---- WiredRoomLogs ----

    static WiredLogEntry logEntry() {
        return new WiredLogEntry(5000000001L, WiredLogLevel.WARN, WiredLogSource.WIRED, "Signal loop stopped",
                1759658400000L, "12:00:00");
    }

    static void appendLogEntry(HPacket p) {
        p.appendLong(5000000001L).appendByte((byte) 1).appendByte((byte) 1).appendString("Signal loop stopped")
                .appendLong(1759658400000L).appendString("12:00:00");
    }

    static WiredRoomLogs filteredRoomLogs() {
        return WiredRoomLogs.builder()
                .totalEntries(51).currentPage(2).pageSize(50).entries(Collections.singletonList(logEntry()))
                .hasLogLevelFilter(true).logLevelFilter(WiredLogLevel.WARN)
                .hasLogSourceFilter(true).logSourceFilter(WiredLogSource.WIRED)
                .hasQuery(true).query("loop")
                .build();
    }

    static HPacket filteredRoomLogsPacket() {
        HPacket p = new HPacket("WiredRoomLogs", HMessage.Direction.TOCLIENT);
        p.appendInt(51).appendInt(2).appendInt(50).appendInt(1);
        appendLogEntry(p);
        p.appendBoolean(true).appendByte((byte) 1)
                .appendBoolean(true).appendByte((byte) 1)
                .appendBoolean(true).appendString("loop");
        return p;
    }

    @Test
    void wiredRoomLogsToPacket() {
        assertSameBytes(filteredRoomLogsPacket(), filteredRoomLogs().toPacket());
    }

    @Test
    void wiredRoomLogsFromPacket() {
        assertEquals(filteredRoomLogs(), WiredRoomLogs.fromPacket(filteredRoomLogsPacket()));
    }

    @Test
    void wiredRoomLogsWithoutFiltersOnlySendsTheFlags() {
        WiredRoomLogs logs = WiredRoomLogs.builder()
                .totalEntries(0).currentPage(1).pageSize(50).entries(Collections.emptyList())
                .hasLogLevelFilter(false).hasLogSourceFilter(false).hasQuery(false)
                .build();
        HPacket p = new HPacket("WiredRoomLogs", HMessage.Direction.TOCLIENT);
        p.appendInt(0).appendInt(1).appendInt(50).appendInt(0)
                .appendBoolean(false).appendBoolean(false).appendBoolean(false);

        assertSameBytes(p, logs.toPacket());
        assertEquals(logs, WiredRoomLogs.fromPacket(p));
    }

    // ---- WiredRoomStats ----

    static WiredRoomStats roomStats() {
        return new WiredRoomStats(1234.5, 10000.0, false, 812, 2500, 30, 300, 5, 100, 40, 200, 2, 50);
    }

    static HPacket roomStatsPacket() {
        HPacket p = new HPacket("WiredRoomStats", HMessage.Direction.TOCLIENT);
        p.appendDouble(1234.5).appendDouble(10000.0).appendBoolean(false)
                .appendInt(812).appendInt(2500).appendInt(30).appendInt(300)
                .appendInt(5).appendInt(100).appendInt(40).appendInt(200).appendInt(2).appendInt(50);
        return p;
    }

    @Test
    void wiredRoomStatsToPacket() {
        assertSameBytes(roomStatsPacket(), roomStats().toPacket());
    }

    @Test
    void wiredRoomStatsFromPacket() {
        assertEquals(roomStats(), WiredRoomStats.fromPacket(roomStatsPacket()));
    }

    // ---- WiredSetUserPermanentVariableResult ----

    @Test
    void wiredSetUserPermanentVariableResultBothWays() {
        HPacket p = new HPacket("WiredSetUserPermanentVariableResult", HMessage.Direction.TOCLIENT);
        p.appendBoolean(false);

        assertSameBytes(p, new WiredSetUserPermanentVariableResult(false).toPacket());
        assertEquals(new WiredSetUserPermanentVariableResult(false), WiredSetUserPermanentVariableResult.fromPacket(p));
    }

    // ---- WiredUserPermanentVariables ----

    static WiredUserPermanentVariables petVariables() {
        return WiredUserPermanentVariables.builder()
                .entityType(UserType.PET).entityId(31).entityName("Rex").entityFigure("0 4 ffffff")
                .ownerId(1001).ownerName("Roboroads").ownerFigure("hd-180-1")
                .variables(Collections.singletonList(new StoredVariable("u_score", storage())))
                .build();
    }

    static HPacket petVariablesPacket() {
        HPacket p = new HPacket("WiredUserPermanentVariables", HMessage.Direction.TOCLIENT);
        p.appendInt(2).appendInt(31).appendString("Rex").appendString("0 4 ffffff")
                .appendInt(1001).appendString("Roboroads").appendString("hd-180-1")
                .appendInt(1).appendString("u_score");
        appendStorage(p);
        return p;
    }

    @Test
    void wiredUserPermanentVariablesToPacket() {
        assertSameBytes(petVariablesPacket(), petVariables().toPacket());
    }

    @Test
    void wiredUserPermanentVariablesFromPacket() {
        assertEquals(petVariables(), WiredUserPermanentVariables.fromPacket(petVariablesPacket()));
    }

    @Test
    void wiredUserPermanentVariablesOfAUserHaveNoOwner() {
        WiredUserPermanentVariables user = WiredUserPermanentVariables.builder()
                .entityType(UserType.PLAYER).entityId(1001).entityName("Roboroads").entityFigure("hd-180-1")
                .variables(Collections.emptyList())
                .build();
        HPacket p = new HPacket("WiredUserPermanentVariables", HMessage.Direction.TOCLIENT);
        p.appendInt(1).appendInt(1001).appendString("Roboroads").appendString("hd-180-1").appendInt(0);

        assertSameBytes(p, user.toPacket());
        assertEquals(user, WiredUserPermanentVariables.fromPacket(p));
    }

    // ---- WiredUserVariablesList ----

    static WiredUserVariablesList userVariablesList() {
        return new WiredUserVariablesList("u_score", 2, 1, 50,
                Arrays.asList(new VariableOwner(UserType.PLAYER, 1001, "Roboroads", storage()),
                        new VariableOwner(UserType.BOT, 77, "Frank", storage())),
                -1, WiredVariableSort.LATEST_UPDATE);
    }

    static HPacket userVariablesListPacket() {
        HPacket p = new HPacket("WiredUserVariablesList", HMessage.Direction.TOCLIENT);
        p.appendString("u_score").appendInt(2).appendInt(1).appendInt(50).appendInt(2);
        p.appendInt(1).appendInt(1001).appendString("Roboroads");
        appendStorage(p);
        p.appendInt(4).appendInt(77).appendString("Frank");
        appendStorage(p);
        p.appendInt(-1).appendInt(5);
        return p;
    }

    @Test
    void wiredUserVariablesListToPacket() {
        assertSameBytes(userVariablesListPacket(), userVariablesList().toPacket());
    }

    @Test
    void wiredUserVariablesListFromPacket() {
        assertEquals(userVariablesList(), WiredUserVariablesList.fromPacket(userVariablesListPacket()));
    }

    // ---- WiredVariablesForObject ----

    static WiredVariablesForObject furniVariables() {
        return WiredVariablesForObject.builder()
                .variableTarget(WiredVariableTarget.FURNI).objectId(5001)
                .variableValues(Arrays.asList(new VariableValue("f_state", 1), new VariableValue("f_count", 12)))
                .configuredInWireds(Arrays.asList(6001, 6002))
                .build();
    }

    static HPacket furniVariablesPacket() {
        HPacket p = new HPacket("WiredVariablesForObject", HMessage.Direction.TOCLIENT);
        p.appendInt(0).appendInt(5001)
                .appendInt(2).appendString("f_state").appendInt(1).appendString("f_count").appendInt(12)
                .appendInt(2).appendInt(6001).appendInt(6002);
        return p;
    }

    @Test
    void wiredVariablesForObjectToPacket() {
        assertSameBytes(furniVariablesPacket(), furniVariables().toPacket());
    }

    @Test
    void wiredVariablesForObjectFromPacket() {
        assertEquals(furniVariables(), WiredVariablesForObject.fromPacket(furniVariablesPacket()));
    }

    @Test
    void wiredVariablesForAUserSendTheRoomIndexAndNoWireds() {
        WiredVariablesForObject user = WiredVariablesForObject.builder()
                .variableTarget(WiredVariableTarget.USER).userIndex(3)
                .variableValues(Collections.singletonList(new VariableValue("u_score", 42)))
                .build();
        HPacket p = new HPacket("WiredVariablesForObject", HMessage.Direction.TOCLIENT);
        p.appendInt(1).appendInt(3).appendInt(1).appendString("u_score").appendInt(42);

        assertSameBytes(p, user.toPacket());
        assertEquals(user, WiredVariablesForObject.fromPacket(p));
    }

    @Test
    void wiredVariablesForTheRoomOnlySendTheValues() {
        WiredVariablesForObject global = WiredVariablesForObject.builder()
                .variableTarget(WiredVariableTarget.GLOBAL)
                .variableValues(Collections.singletonList(new VariableValue("g_round", 7)))
                .build();
        HPacket p = new HPacket("WiredVariablesForObject", HMessage.Direction.TOCLIENT);
        p.appendInt(-10).appendInt(1).appendString("g_round").appendInt(7);

        assertSameBytes(p, global.toPacket());
        assertEquals(global, WiredVariablesForObject.fromPacket(p));
    }

    // ---- WiredClearErrorLogs, WiredGetAllVariablesHash, WiredGetErrorLogs, WiredGetRoomStats ----

    @Test
    void packetsWithoutParametersHaveAnEmptyBody() {
        assertSameBytes(new HPacket("WiredClearErrorLogs", HMessage.Direction.TOSERVER), new WiredClearErrorLogs().toPacket());
        assertSameBytes(new HPacket("WiredGetAllVariablesHash", HMessage.Direction.TOSERVER), new WiredGetAllVariablesHash().toPacket());
        assertSameBytes(new HPacket("WiredGetErrorLogs", HMessage.Direction.TOSERVER), new WiredGetErrorLogs().toPacket());
        assertSameBytes(new HPacket("WiredGetRoomStats", HMessage.Direction.TOSERVER), new WiredGetRoomStats().toPacket());

        assertEquals(new WiredClearErrorLogs(), WiredClearErrorLogs.fromPacket(new HPacket("WiredClearErrorLogs", HMessage.Direction.TOSERVER)));
        assertEquals(new WiredGetAllVariablesHash(), WiredGetAllVariablesHash.fromPacket(new HPacket("WiredGetAllVariablesHash", HMessage.Direction.TOSERVER)));
        assertEquals(new WiredGetErrorLogs(), WiredGetErrorLogs.fromPacket(new HPacket("WiredGetErrorLogs", HMessage.Direction.TOSERVER)));
        assertEquals(new WiredGetRoomStats(), WiredGetRoomStats.fromPacket(new HPacket("WiredGetRoomStats", HMessage.Direction.TOSERVER)));
    }

    // ---- WiredDeleteAllVariableHolders ----

    @Test
    void wiredDeleteAllVariableHoldersBothWays() {
        HPacket p = new HPacket("WiredDeleteAllVariableHolders", HMessage.Direction.TOSERVER);
        p.appendString("u_score");

        assertSameBytes(p, new WiredDeleteAllVariableHolders("u_score").toPacket());
        assertEquals(new WiredDeleteAllVariableHolders("u_score"), WiredDeleteAllVariableHolders.fromPacket(p));
    }

    // ---- WiredGetAllVariableHolders ----

    @Test
    void wiredGetAllVariableHoldersBothWays() {
        HPacket p = new HPacket("WiredGetAllVariableHolders", HMessage.Direction.TOSERVER);
        p.appendString("u_score");

        assertSameBytes(p, new WiredGetAllVariableHolders("u_score").toPacket());
        assertEquals(new WiredGetAllVariableHolders("u_score"), WiredGetAllVariableHolders.fromPacket(p));
    }

    // ---- WiredGetAllVariablesDiffs ----

    static WiredGetAllVariablesDiffs getAllVariablesDiffs() {
        return new WiredGetAllVariablesDiffs(Arrays.asList(new VariableHash("u_score", 77), new VariableHash("f_state", 78)));
    }

    static HPacket getAllVariablesDiffsPacket() {
        HPacket p = new HPacket("WiredGetAllVariablesDiffs", HMessage.Direction.TOSERVER);
        p.appendInt(2).appendString("u_score").appendInt(77).appendString("f_state").appendInt(78);
        return p;
    }

    @Test
    void wiredGetAllVariablesDiffsToPacket() {
        assertSameBytes(getAllVariablesDiffsPacket(), getAllVariablesDiffs().toPacket());
    }

    @Test
    void wiredGetAllVariablesDiffsFromPacket() {
        assertEquals(getAllVariablesDiffs(), WiredGetAllVariablesDiffs.fromPacket(getAllVariablesDiffsPacket()));
    }

    @Test
    void wiredGetAllVariablesDiffsSendsAnEmptyListTheFirstTime() {
        HPacket p = new HPacket("WiredGetAllVariablesDiffs", HMessage.Direction.TOSERVER);
        p.appendInt(0);

        assertSameBytes(p, new WiredGetAllVariablesDiffs(Collections.emptyList()).toPacket());
    }

    // ---- WiredGetRoomLogs ----

    static HPacket getRoomLogsPacket() {
        HPacket p = new HPacket("WiredGetRoomLogs", HMessage.Direction.TOSERVER);
        p.appendInt(1).appendInt(50).appendInt(-1).appendInt(-1).appendString("");
        return p;
    }

    @Test
    void wiredGetRoomLogsToPacket() {
        WiredGetRoomLogs request = WiredGetRoomLogs.builder()
                .page(1).logLevelFilter(WiredLogLevel.ALL).logSourceFilter(WiredLogSource.ALL).query("")
                .build();

        assertSameBytes(getRoomLogsPacket(), request.toPacket());
    }

    @Test
    void wiredGetRoomLogsFromPacket() {
        assertEquals(new WiredGetRoomLogs(1, 50, WiredLogLevel.ALL, WiredLogSource.ALL, ""),
                WiredGetRoomLogs.fromPacket(getRoomLogsPacket()));
    }

    // ---- WiredGetUserPermanentVariables ----

    @Test
    void wiredGetUserPermanentVariablesBothWays() {
        HPacket p = new HPacket("WiredGetUserPermanentVariables", HMessage.Direction.TOSERVER);
        p.appendInt(2).appendInt(31);

        assertSameBytes(p, new WiredGetUserPermanentVariables(UserType.PET, 31).toPacket());
        assertEquals(new WiredGetUserPermanentVariables(UserType.PET, 31), WiredGetUserPermanentVariables.fromPacket(p));
    }

    // ---- WiredGetVariableOwnersPage ----

    static HPacket getVariableOwnersPagePacket() {
        HPacket p = new HPacket("WiredGetVariableOwnersPage", HMessage.Direction.TOSERVER);
        p.appendString("u_score").appendInt(1).appendInt(50).appendInt(0).appendInt(-1);
        return p;
    }

    @Test
    void wiredGetVariableOwnersPageToPacket() {
        WiredGetVariableOwnersPage request = WiredGetVariableOwnersPage.builder()
                .variableId("u_score").page(1).sortType(WiredVariableSort.HIGHEST_VALUE).userTypeFilter(-1)
                .build();

        assertSameBytes(getVariableOwnersPagePacket(), request.toPacket());
    }

    @Test
    void wiredGetVariableOwnersPageFromPacket() {
        assertEquals(new WiredGetVariableOwnersPage("u_score", 1, 50, WiredVariableSort.HIGHEST_VALUE, -1),
                WiredGetVariableOwnersPage.fromPacket(getVariableOwnersPagePacket()));
    }

    // ---- WiredGetVariablesForObject ----

    @Test
    void wiredGetVariablesForObjectBothWays() {
        HPacket p = new HPacket("WiredGetVariablesForObject", HMessage.Direction.TOSERVER);
        p.appendInt(1).appendInt(3);

        assertSameBytes(p, new WiredGetVariablesForObject(WiredVariableTarget.USER, 3).toPacket());
        assertEquals(new WiredGetVariablesForObject(WiredVariableTarget.USER, 3), WiredGetVariablesForObject.fromPacket(p));
    }

    // ---- WiredSetObjectVariableValue ----

    static HPacket setObjectVariableValuePacket() {
        HPacket p = new HPacket("WiredSetObjectVariableValue", HMessage.Direction.TOSERVER);
        p.appendInt(0).appendInt(5001).appendString("f_count").appendInt(13).appendInt(0);
        return p;
    }

    static WiredSetObjectVariableValue setObjectVariableValue() {
        return new WiredSetObjectVariableValue(WiredVariableTarget.FURNI, 5001, "f_count", 13, WiredVariableAction.SET_VALUE);
    }

    @Test
    void wiredSetObjectVariableValueToPacket() {
        assertSameBytes(setObjectVariableValuePacket(), setObjectVariableValue().toPacket());
    }

    @Test
    void wiredSetObjectVariableValueFromPacket() {
        assertEquals(setObjectVariableValue(), WiredSetObjectVariableValue.fromPacket(setObjectVariableValuePacket()));
    }

    // ---- WiredSetPreferences ----

    static HPacket setPreferencesPacket() {
        HPacket p = new HPacket("WiredSetPreferences", HMessage.Direction.TOSERVER);
        p.appendBoolean(true).appendBoolean(false).appendBoolean(true).appendInt(0)
                .appendBoolean(false).appendBoolean(true).appendString("volter");
        return p;
    }

    @Test
    void wiredSetPreferencesToPacket() {
        WiredSetPreferences preferences = WiredSetPreferences.builder()
                .wiredMenuButton(true).wiredInspectButton(false).playTestMode(true)
                .wiredWhisperDisabled(false).showAllNotifications(true).uiStyle("volter")
                .build();

        assertSameBytes(setPreferencesPacket(), preferences.toPacket());
    }

    @Test
    void wiredSetPreferencesFromPacket() {
        assertEquals(new WiredSetPreferences(true, false, true, 0, false, true, "volter"),
                WiredSetPreferences.fromPacket(setPreferencesPacket()));
    }

    // ---- WiredSetUserPermanentVariable ----

    static HPacket setUserPermanentVariablePacket() {
        HPacket p = new HPacket("WiredSetUserPermanentVariable", HMessage.Direction.TOSERVER);
        p.appendInt(4).appendInt(77).appendString("u_score").appendInt(0).appendInt(2);
        return p;
    }

    static WiredSetUserPermanentVariable setUserPermanentVariable() {
        return new WiredSetUserPermanentVariable(UserType.BOT, 77, "u_score", 0, WiredVariableAction.DELETE);
    }

    @Test
    void wiredSetUserPermanentVariableToPacket() {
        assertSameBytes(setUserPermanentVariablePacket(), setUserPermanentVariable().toPacket());
    }

    @Test
    void wiredSetUserPermanentVariableFromPacket() {
        assertEquals(setUserPermanentVariable(), WiredSetUserPermanentVariable.fromPacket(setUserPermanentVariablePacket()));
    }

    // ---- WiredUpdateRoom ----

    @Test
    void wiredUpdateRoomBothWays() {
        HPacket p = new HPacket("WiredUpdateRoom", HMessage.Direction.TOSERVER);
        p.appendBoolean(true);

        assertSameBytes(p, new WiredUpdateRoom(true).toPacket());
        assertEquals(new WiredUpdateRoom(true), WiredUpdateRoom.fromPacket(p));
    }
}
