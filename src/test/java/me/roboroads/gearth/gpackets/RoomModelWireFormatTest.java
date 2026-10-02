package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.FloorHeightMap;
import me.roboroads.gearth.gpackets.incoming.FurnitureAliases;
import me.roboroads.gearth.gpackets.incoming.HeightMap;
import me.roboroads.gearth.gpackets.incoming.HeightMapUpdate;
import me.roboroads.gearth.gpackets.incoming.RoomEntryInfo;
import me.roboroads.gearth.gpackets.incoming.RoomEntryTile;
import me.roboroads.gearth.gpackets.incoming.RoomOccupiedTiles;
import me.roboroads.gearth.gpackets.incoming.RoomProperty;
import me.roboroads.gearth.gpackets.incoming.RoomVisualizationSettings;
import me.roboroads.gearth.gpackets.incoming.SpecialRoomEffect;
import me.roboroads.gearth.gpackets.incoming.sub.room.AreaHideData;
import me.roboroads.gearth.gpackets.incoming.sub.room.FurnitureAlias;
import me.roboroads.gearth.gpackets.incoming.sub.room.HeightMapTileUpdate;
import me.roboroads.gearth.gpackets.incoming.sub.room.OccupiedTile;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.RoomPropertyType;
import me.roboroads.gearth.gpackets.model.enums.RoomThickness;
import me.roboroads.gearth.gpackets.model.enums.SpecialRoomEffectType;
import me.roboroads.gearth.gpackets.outgoing.GetFurnitureAliases;
import me.roboroads.gearth.gpackets.outgoing.GetOccupiedTiles;
import me.roboroads.gearth.gpackets.outgoing.GetRoomEntryTile;
import me.roboroads.gearth.gpackets.outgoing.RequestRoomPropertySet;
import me.roboroads.gearth.gpackets.outgoing.UpdateFloorProperties;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomModelWireFormatTest {

    // ---- FloorHeightMap ----

    static FloorHeightMap floorHeightMap() {
        return new FloorHeightMap(true, 4, "xxx\rx00\rx01\r",
                Collections.singletonList(new AreaHideData(77, true, 1, 2, 3, 4, false)), 5, 6, 1.5f);
    }

    static HPacket floorHeightMapPacket() {
        HPacket p = new HPacket("FloorHeightMap", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true).appendInt(4).appendString("xxx\rx00\rx01\r");
        p.appendInt(1).appendInt(77).appendBoolean(true).appendInt(1).appendInt(2).appendInt(3).appendInt(4).appendBoolean(false);
        p.appendInt(5).appendInt(6).appendFloat(1.5f);
        return p;
    }

    @Test
    void floorHeightMapToPacket() {
        assertSameBytes(floorHeightMapPacket(), floorHeightMap().toPacket());
    }

    @Test
    void floorHeightMapFromPacket() {
        assertEquals(floorHeightMap(), FloorHeightMap.fromPacket(floorHeightMapPacket()));
    }

    // ---- FurnitureAliases ----

    static FurnitureAliases furnitureAliases() {
        return new FurnitureAliases(Arrays.asList(new FurnitureAlias("rare_dragonlamp*1", "rare_dragonlamp"),
                new FurnitureAlias("chair_norja*2", "chair_norja")));
    }

    static HPacket furnitureAliasesPacket() {
        HPacket p = new HPacket("FurnitureAliases", HMessage.Direction.TOCLIENT);
        p.appendInt(2).appendString("rare_dragonlamp*1").appendString("rare_dragonlamp")
                .appendString("chair_norja*2").appendString("chair_norja");
        return p;
    }

    @Test
    void furnitureAliasesToPacket() {
        assertSameBytes(furnitureAliasesPacket(), furnitureAliases().toPacket());
    }

    @Test
    void furnitureAliasesFromPacket() {
        assertEquals(furnitureAliases(), FurnitureAliases.fromPacket(furnitureAliasesPacket()));
    }

    // ---- HeightMap ----

    static HeightMap heightMap() {
        return new HeightMap(2, Arrays.asList((short) -1, (short) 0, (short) 0x4100, (short) 512));
    }

    static HPacket heightMapPacket() {
        HPacket p = new HPacket("HeightMap", HMessage.Direction.TOCLIENT);
        p.appendInt(2).appendInt(4).appendShort((short) -1).appendShort((short) 0).appendShort((short) 0x4100).appendShort((short) 512);
        return p;
    }

    @Test
    void heightMapToPacket() {
        assertSameBytes(heightMapPacket(), heightMap().toPacket());
    }

    @Test
    void heightMapFromPacket() {
        assertEquals(heightMap(), HeightMap.fromPacket(heightMapPacket()));
    }

    // ---- HeightMapUpdate ----

    static HeightMapUpdate heightMapUpdate() {
        return new HeightMapUpdate(Arrays.asList(new HeightMapTileUpdate((byte) 3, (byte) 4, (short) 256),
                new HeightMapTileUpdate((byte) 5, (byte) 6, (short) -1)));
    }

    static HPacket heightMapUpdatePacket() {
        HPacket p = new HPacket("HeightMapUpdate", HMessage.Direction.TOCLIENT);
        p.appendByte((byte) 2)
                .appendByte((byte) 3).appendByte((byte) 4).appendShort((short) 256)
                .appendByte((byte) 5).appendByte((byte) 6).appendShort((short) -1);
        return p;
    }

    @Test
    void heightMapUpdateToPacket() {
        assertSameBytes(heightMapUpdatePacket(), heightMapUpdate().toPacket());
    }

    @Test
    void heightMapUpdateFromPacket() {
        assertEquals(heightMapUpdate(), HeightMapUpdate.fromPacket(heightMapUpdatePacket()));
    }

    // ---- RoomEntryInfo ----

    static HPacket roomEntryInfoPacket() {
        HPacket p = new HPacket("RoomEntryInfo", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendBoolean(true);
        return p;
    }

    @Test
    void roomEntryInfoToPacket() {
        assertSameBytes(roomEntryInfoPacket(), new RoomEntryInfo(12345, true).toPacket());
    }

    @Test
    void roomEntryInfoFromPacket() {
        assertEquals(new RoomEntryInfo(12345, true), RoomEntryInfo.fromPacket(roomEntryInfoPacket()));
    }

    // ---- RoomEntryTile ----

    static HPacket roomEntryTilePacket() {
        HPacket p = new HPacket("RoomEntryTile", HMessage.Direction.TOCLIENT);
        p.appendInt(0).appendInt(5).appendInt(2);
        return p;
    }

    @Test
    void roomEntryTileToPacket() {
        assertSameBytes(roomEntryTilePacket(), new RoomEntryTile(0, 5, Direction.EAST).toPacket());
    }

    @Test
    void roomEntryTileFromPacket() {
        assertEquals(new RoomEntryTile(0, 5, Direction.EAST), RoomEntryTile.fromPacket(roomEntryTilePacket()));
    }

    // ---- RoomOccupiedTiles ----

    static RoomOccupiedTiles roomOccupiedTiles() {
        return new RoomOccupiedTiles(Arrays.asList(new OccupiedTile(1, 2), new OccupiedTile(3, 4)));
    }

    static HPacket roomOccupiedTilesPacket() {
        HPacket p = new HPacket("RoomOccupiedTiles", HMessage.Direction.TOCLIENT);
        p.appendInt(2).appendInt(1).appendInt(2).appendInt(3).appendInt(4);
        return p;
    }

    @Test
    void roomOccupiedTilesToPacket() {
        assertSameBytes(roomOccupiedTilesPacket(), roomOccupiedTiles().toPacket());
    }

    @Test
    void roomOccupiedTilesFromPacket() {
        assertEquals(roomOccupiedTiles(), RoomOccupiedTiles.fromPacket(roomOccupiedTilesPacket()));
    }

    // ---- RoomProperty ----

    static HPacket roomPropertyPacket() {
        HPacket p = new HPacket("RoomProperty", HMessage.Direction.TOCLIENT);
        p.appendString("landscapeanim").appendString("2.1");
        return p;
    }

    @Test
    void roomPropertyToPacket() {
        assertSameBytes(roomPropertyPacket(), new RoomProperty(RoomPropertyType.LANDSCAPE_ANIMATION, "2.1").toPacket());
    }

    @Test
    void roomPropertyFromPacket() {
        assertEquals(new RoomProperty(RoomPropertyType.LANDSCAPE_ANIMATION, "2.1"), RoomProperty.fromPacket(roomPropertyPacket()));
    }

    // ---- RoomVisualizationSettings ----

    static HPacket roomVisualizationSettingsPacket() {
        HPacket p = new HPacket("RoomVisualizationSettings", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true).appendInt(-2).appendInt(1);
        return p;
    }

    @Test
    void roomVisualizationSettingsToPacket() {
        assertSameBytes(roomVisualizationSettingsPacket(),
                new RoomVisualizationSettings(true, RoomThickness.THINNEST, RoomThickness.THICK).toPacket());
    }

    @Test
    void roomVisualizationSettingsFromPacket() {
        assertEquals(new RoomVisualizationSettings(true, RoomThickness.THINNEST, RoomThickness.THICK),
                RoomVisualizationSettings.fromPacket(roomVisualizationSettingsPacket()));
    }

    // ---- SpecialRoomEffect ----

    static HPacket specialRoomEffectPacket() {
        HPacket p = new HPacket("SpecialRoomEffect", HMessage.Direction.TOCLIENT);
        p.appendInt(3);
        return p;
    }

    @Test
    void specialRoomEffectToPacket() {
        assertSameBytes(specialRoomEffectPacket(), new SpecialRoomEffect(SpecialRoomEffectType.DISCO).toPacket());
    }

    @Test
    void specialRoomEffectFromPacket() {
        assertEquals(new SpecialRoomEffect(SpecialRoomEffectType.DISCO), SpecialRoomEffect.fromPacket(specialRoomEffectPacket()));
    }

    // ---- GetFurnitureAliases ----

    static HPacket getFurnitureAliasesPacket() {
        return new HPacket("GetFurnitureAliases", HMessage.Direction.TOSERVER);
    }

    @Test
    void getFurnitureAliasesToPacket() {
        assertSameBytes(getFurnitureAliasesPacket(), new GetFurnitureAliases().toPacket());
    }

    @Test
    void getFurnitureAliasesFromPacket() {
        assertEquals(new GetFurnitureAliases(), GetFurnitureAliases.fromPacket(getFurnitureAliasesPacket()));
    }

    // ---- GetOccupiedTiles ----

    static HPacket getOccupiedTilesPacket() {
        return new HPacket("GetOccupiedTiles", HMessage.Direction.TOSERVER);
    }

    @Test
    void getOccupiedTilesToPacket() {
        assertSameBytes(getOccupiedTilesPacket(), new GetOccupiedTiles().toPacket());
    }

    @Test
    void getOccupiedTilesFromPacket() {
        assertEquals(new GetOccupiedTiles(), GetOccupiedTiles.fromPacket(getOccupiedTilesPacket()));
    }

    // ---- GetRoomEntryTile ----

    static HPacket getRoomEntryTilePacket() {
        return new HPacket("GetRoomEntryTile", HMessage.Direction.TOSERVER);
    }

    @Test
    void getRoomEntryTileToPacket() {
        assertSameBytes(getRoomEntryTilePacket(), new GetRoomEntryTile().toPacket());
    }

    @Test
    void getRoomEntryTileFromPacket() {
        assertEquals(new GetRoomEntryTile(), GetRoomEntryTile.fromPacket(getRoomEntryTilePacket()));
    }

    // ---- RequestRoomPropertySet ----

    static HPacket requestRoomPropertySetPacket() {
        HPacket p = new HPacket("RequestRoomPropertySet", HMessage.Direction.TOSERVER);
        p.appendInt(987654);
        return p;
    }

    @Test
    void requestRoomPropertySetToPacket() {
        assertSameBytes(requestRoomPropertySetPacket(), new RequestRoomPropertySet(987654).toPacket());
    }

    @Test
    void requestRoomPropertySetFromPacket() {
        assertEquals(new RequestRoomPropertySet(987654), RequestRoomPropertySet.fromPacket(requestRoomPropertySetPacket()));
    }

    // ---- UpdateFloorProperties ----

    static UpdateFloorProperties updateFloorProperties() {
        return new UpdateFloorProperties("xxx\rx00\r", 1, 1, Direction.SOUTH, RoomThickness.THIN, RoomThickness.NORMAL, 7);
    }

    static HPacket updateFloorPropertiesPacket() {
        HPacket p = new HPacket("UpdateFloorProperties", HMessage.Direction.TOSERVER);
        p.appendString("xxx\rx00\r").appendInt(1).appendInt(1).appendInt(4).appendInt(-1).appendInt(0).appendInt(7);
        return p;
    }

    @Test
    void updateFloorPropertiesToPacket() {
        assertSameBytes(updateFloorPropertiesPacket(), updateFloorProperties().toPacket());
    }

    @Test
    void updateFloorPropertiesFromPacket() {
        assertEquals(updateFloorProperties(), UpdateFloorProperties.fromPacket(updateFloorPropertiesPacket()));
    }

    static UpdateFloorProperties updateFloorPropertiesWithoutWallHeight() {
        return UpdateFloorProperties.builder().floorPlan("x0\r").entryX(1).entryY(0).entryDirection(Direction.WEST)
                .wallThickness(RoomThickness.NORMAL).floorThickness(RoomThickness.THICK).build();
    }

    static HPacket updateFloorPropertiesWithoutWallHeightPacket() {
        HPacket p = new HPacket("UpdateFloorProperties", HMessage.Direction.TOSERVER);
        p.appendString("x0\r").appendInt(1).appendInt(0).appendInt(6).appendInt(0).appendInt(1);
        return p;
    }

    @Test
    void updateFloorPropertiesWithoutWallHeightToPacket() {
        assertSameBytes(updateFloorPropertiesWithoutWallHeightPacket(), updateFloorPropertiesWithoutWallHeight().toPacket());
    }

    @Test
    void updateFloorPropertiesWithoutWallHeightFromPacket() {
        assertEquals(updateFloorPropertiesWithoutWallHeight(),
                UpdateFloorProperties.fromPacket(updateFloorPropertiesWithoutWallHeightPacket()));
    }

    static HPacket updateFloorPropertiesPlanOnlyPacket() {
        HPacket p = new HPacket("UpdateFloorProperties", HMessage.Direction.TOSERVER);
        p.appendString("x0\r");
        return p;
    }

    @Test
    void updateFloorPropertiesPlanOnlyToPacket() {
        assertSameBytes(updateFloorPropertiesPlanOnlyPacket(), UpdateFloorProperties.builder().floorPlan("x0\r").build().toPacket());
    }

    @Test
    void updateFloorPropertiesPlanOnlyFromPacket() {
        assertEquals(UpdateFloorProperties.builder().floorPlan("x0\r").build(),
                UpdateFloorProperties.fromPacket(updateFloorPropertiesPlanOnlyPacket()));
    }
}
