package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsData;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsError;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaveError;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaved;
import me.roboroads.gearth.gpackets.incoming.WiredRoomSettings;
import me.roboroads.gearth.gpackets.incoming.sub.room.RoomModerationSettings;
import me.roboroads.gearth.gpackets.model.enums.ChatFloodSensitivity;
import me.roboroads.gearth.gpackets.model.enums.DoorMode;
import me.roboroads.gearth.gpackets.model.enums.RoomModerationPermission;
import me.roboroads.gearth.gpackets.model.enums.RoomThickness;
import me.roboroads.gearth.gpackets.model.enums.TradeMode;
import me.roboroads.gearth.gpackets.outgoing.GetRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.SaveRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.WiredGetRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.WiredSetRoomSettings;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation") // tests RoomSettingsError
class RoomSettingsWireFormatTest {

    // ---- RoomSettingsData ----

    static RoomSettingsData roomSettingsData() {
        return RoomSettingsData.builder()
                .roomId(42).name("My room").description("Chill").doorMode(DoorMode.PASSWORD).categoryId(3)
                .maximumVisitors(25).maximumVisitorsLimit(50).tags(Arrays.asList("chill", "music"))
                .tradeMode(TradeMode.EVERYONE).allowPets(1).allowFoodConsume(0).allowWalkThrough(1).hideWalls(0)
                .wallThickness(RoomThickness.THIN).floorThickness(RoomThickness.NORMAL)
                .chatFloodSensitivity(ChatFloodSensitivity.NORMAL)
                .leaveOnDoorTileEnabled(true).idleSleepEnabled(true).idleSleepTimeoutSeconds(600)
                .idleAutokickEnabled(false).idleAutokickTimeoutSeconds(0).muteAllPets(false)
                .roomModerationSettings(new RoomModerationSettings(
                        RoomModerationPermission.RIGHTS, RoomModerationPermission.GROUP_ADMINS, RoomModerationPermission.NONE))
                .hiddenByBc(false)
                .build();
    }

    static HPacket roomSettingsDataPacket() {
        HPacket p = new HPacket("RoomSettingsData", HMessage.Direction.TOCLIENT);
        p.appendInt(42).appendString("My room").appendString("Chill").appendInt(2).appendInt(3)
                .appendInt(25).appendInt(50).appendInt(2).appendString("chill").appendString("music")
                .appendInt(2).appendInt(1).appendInt(0).appendInt(1).appendInt(0)
                .appendInt(-1).appendInt(0).appendInt(1)
                .appendBoolean(true).appendBoolean(true).appendInt(600)
                .appendBoolean(false).appendInt(0).appendBoolean(false)
                .appendInt(1).appendInt(4).appendInt(0)
                .appendBoolean(false);
        return p;
    }

    @Test
    void roomSettingsDataToPacket() {
        assertSameBytes(roomSettingsDataPacket(), roomSettingsData().toPacket());
    }

    @Test
    void roomSettingsDataFromPacket() {
        assertEquals(roomSettingsData(), RoomSettingsData.fromPacket(roomSettingsDataPacket()));
    }

    // ---- RoomSettingsError ----

    static HPacket roomSettingsErrorPacket() {
        HPacket p = new HPacket("RoomSettingsError", HMessage.Direction.TOCLIENT);
        p.appendInt(42).appendInt(1);
        return p;
    }

    @Test
    void roomSettingsErrorToPacket() {
        assertSameBytes(roomSettingsErrorPacket(), new RoomSettingsError(42, 1).toPacket());
    }

    @Test
    void roomSettingsErrorFromPacket() {
        assertEquals(new RoomSettingsError(42, 1), RoomSettingsError.fromPacket(roomSettingsErrorPacket()));
    }

    // ---- RoomSettingsSaved ----

    static HPacket roomSettingsSavedPacket() {
        HPacket p = new HPacket("RoomSettingsSaved", HMessage.Direction.TOCLIENT);
        p.appendInt(42);
        return p;
    }

    @Test
    void roomSettingsSavedToPacket() {
        assertSameBytes(roomSettingsSavedPacket(), new RoomSettingsSaved(42).toPacket());
    }

    @Test
    void roomSettingsSavedFromPacket() {
        assertEquals(new RoomSettingsSaved(42), RoomSettingsSaved.fromPacket(roomSettingsSavedPacket()));
    }

    // ---- RoomSettingsSaveError ----

    static HPacket roomSettingsSaveErrorPacket() {
        HPacket p = new HPacket("RoomSettingsSaveError", HMessage.Direction.TOCLIENT);
        p.appendInt(42).appendInt(7).appendString("badword");
        return p;
    }

    @Test
    void roomSettingsSaveErrorToPacket() {
        assertSameBytes(roomSettingsSaveErrorPacket(), new RoomSettingsSaveError(42, 7, "badword").toPacket());
    }

    @Test
    void roomSettingsSaveErrorFromPacket() {
        assertEquals(new RoomSettingsSaveError(42, 7, "badword"), RoomSettingsSaveError.fromPacket(roomSettingsSaveErrorPacket()));
    }

    // ---- WiredRoomSettings ----

    static HPacket wiredRoomSettingsPacket() {
        HPacket p = new HPacket("WiredRoomSettings", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(7).appendString("Europe/Amsterdam");
        return p;
    }

    @Test
    void wiredRoomSettingsToPacket() {
        assertSameBytes(wiredRoomSettingsPacket(), new WiredRoomSettings(3, 7, "Europe/Amsterdam").toPacket());
    }

    @Test
    void wiredRoomSettingsFromPacket() {
        assertEquals(new WiredRoomSettings(3, 7, "Europe/Amsterdam"), WiredRoomSettings.fromPacket(wiredRoomSettingsPacket()));
    }

    // ---- GetRoomSettings ----

    static HPacket getRoomSettingsPacket() {
        HPacket p = new HPacket("GetRoomSettings", HMessage.Direction.TOSERVER);
        p.appendInt(42);
        return p;
    }

    @Test
    void getRoomSettingsToPacket() {
        assertSameBytes(getRoomSettingsPacket(), new GetRoomSettings(42).toPacket());
    }

    @Test
    void getRoomSettingsFromPacket() {
        assertEquals(new GetRoomSettings(42), GetRoomSettings.fromPacket(getRoomSettingsPacket()));
    }

    // ---- SaveRoomSettings ----

    static SaveRoomSettings saveRoomSettings() {
        return SaveRoomSettings.builder()
                .roomId(42).name("My room").description("Chill").doorMode(DoorMode.PASSWORD).password("secret")
                .maximumVisitors(25).categoryId(3).tags(Arrays.asList("chill", "music"))
                .tradeMode(TradeMode.EVERYONE).allowPets(true).allowFoodConsume(false).allowWalkThrough(true).hideWalls(false)
                .wallThickness(RoomThickness.THIN).floorThickness(RoomThickness.NORMAL)
                .whoCanMute(RoomModerationPermission.RIGHTS).whoCanKick(RoomModerationPermission.GROUP_ADMINS)
                .whoCanBan(RoomModerationPermission.NONE).chatFloodSensitivity(ChatFloodSensitivity.NORMAL)
                .leaveOnDoorTileEnabled(true).idleSleepEnabled(true).idleSleepTimeoutSeconds(600)
                .idleAutokickEnabled(false).idleAutokickTimeoutSeconds(0).muteAllPets(false)
                .build();
    }

    static HPacket saveRoomSettingsPacket() {
        HPacket p = new HPacket("SaveRoomSettings", HMessage.Direction.TOSERVER);
        p.appendInt(42).appendString("My room").appendString("Chill").appendInt(2).appendString("secret")
                .appendInt(25).appendInt(3).appendInt(2).appendString("chill").appendString("music")
                .appendInt(2).appendBoolean(true).appendBoolean(false).appendBoolean(true).appendBoolean(false)
                .appendInt(-1).appendInt(0)
                .appendInt(1).appendInt(4).appendInt(0).appendInt(1)
                .appendBoolean(true).appendBoolean(true).appendInt(600)
                .appendBoolean(false).appendInt(0).appendBoolean(false);
        return p;
    }

    @Test
    void saveRoomSettingsToPacket() {
        assertSameBytes(saveRoomSettingsPacket(), saveRoomSettings().toPacket());
    }

    @Test
    void saveRoomSettingsFromPacket() {
        assertEquals(saveRoomSettings(), SaveRoomSettings.fromPacket(saveRoomSettingsPacket()));
    }

    // ---- WiredGetRoomSettings ----

    @Test
    void wiredGetRoomSettingsHasNoBody() {
        HPacket expected = new HPacket("WiredGetRoomSettings", HMessage.Direction.TOSERVER);

        assertSameBytes(expected, new WiredGetRoomSettings().toPacket());
        assertEquals(new WiredGetRoomSettings(), WiredGetRoomSettings.fromPacket(expected));
    }

    // ---- WiredSetRoomSettings ----

    static HPacket wiredSetRoomSettingsPacket() {
        HPacket p = new HPacket("WiredSetRoomSettings", HMessage.Direction.TOSERVER);
        p.appendInt(3).appendInt(7).appendString("Europe/Amsterdam");
        return p;
    }

    @Test
    void wiredSetRoomSettingsToPacket() {
        assertSameBytes(wiredSetRoomSettingsPacket(), new WiredSetRoomSettings(3, 7, "Europe/Amsterdam").toPacket());
    }

    @Test
    void wiredSetRoomSettingsFromPacket() {
        assertEquals(new WiredSetRoomSettings(3, 7, "Europe/Amsterdam"), WiredSetRoomSettings.fromPacket(wiredSetRoomSettingsPacket()));
    }
}
