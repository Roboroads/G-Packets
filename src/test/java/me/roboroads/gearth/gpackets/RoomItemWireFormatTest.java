package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.ItemAdd;
import me.roboroads.gearth.gpackets.incoming.ItemDataUpdate;
import me.roboroads.gearth.gpackets.incoming.ItemRemove;
import me.roboroads.gearth.gpackets.incoming.ItemRemoveMultiple;
import me.roboroads.gearth.gpackets.incoming.ItemStateUpdate;
import me.roboroads.gearth.gpackets.incoming.ItemUpdate;
import me.roboroads.gearth.gpackets.incoming.Items;
import me.roboroads.gearth.gpackets.incoming.ItemsStateUpdate;
import me.roboroads.gearth.gpackets.incoming.ObjectAdd;
import me.roboroads.gearth.gpackets.incoming.ObjectDataUpdate;
import me.roboroads.gearth.gpackets.incoming.ObjectRemove;
import me.roboroads.gearth.gpackets.incoming.ObjectRemoveConfirm;
import me.roboroads.gearth.gpackets.incoming.ObjectRemoveMultiple;
import me.roboroads.gearth.gpackets.incoming.ObjectUpdate;
import me.roboroads.gearth.gpackets.incoming.Objects;
import me.roboroads.gearth.gpackets.incoming.ObjectsDataUpdate;
import me.roboroads.gearth.gpackets.incoming.SlideObjectBundle;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FloorItem;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FloorItemDataUpdate;
import me.roboroads.gearth.gpackets.incoming.sub.furni.FurniOwner;
import me.roboroads.gearth.gpackets.incoming.sub.furni.LegacyStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.MapStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.MapStuffDataEntry;
import me.roboroads.gearth.gpackets.incoming.sub.furni.SlideObject;
import me.roboroads.gearth.gpackets.incoming.sub.furni.WallItem;
import me.roboroads.gearth.gpackets.incoming.sub.furni.WallItemStateUpdate;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.FurniPlacementType;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.SlideUserMoveType;
import me.roboroads.gearth.gpackets.outgoing.ClickFurni;
import me.roboroads.gearth.gpackets.outgoing.GetItemData;
import me.roboroads.gearth.gpackets.outgoing.MoveEntityInFlat;
import me.roboroads.gearth.gpackets.outgoing.MoveObject;
import me.roboroads.gearth.gpackets.outgoing.MoveWallItem;
import me.roboroads.gearth.gpackets.outgoing.PickupObject;
import me.roboroads.gearth.gpackets.outgoing.PlaceObject;
import me.roboroads.gearth.gpackets.outgoing.RemoveItem;
import me.roboroads.gearth.gpackets.outgoing.SetClothingChangeData;
import me.roboroads.gearth.gpackets.outgoing.SetItemData;
import me.roboroads.gearth.gpackets.outgoing.SetObjectData;
import me.roboroads.gearth.gpackets.outgoing.UseFurniture;
import me.roboroads.gearth.gpackets.outgoing.UseWallItem;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomItemWireFormatTest {

    // ---- shared samples ----

    // A floor furni with a state string, facing south.
    static FloorItem floorItem() {
        return FloorItem.builder()
                .furniId(501).furniClassId(230).x(3).y(4).direction(Direction.SOUTH)
                .z("0.5").sizeZ("1.0").extra(0)
                .stuffData(LegacyStuffData.builder().typeAndFlags(0).legacyString("1").build())
                .secondsToExpiration(-1).usagePolicy(2).ownerId(77)
                .build();
    }

    static void appendFloorItem(HPacket p) {
        p.appendInt(501).appendInt(230).appendInt(3).appendInt(4).appendInt(4)
                .appendString("0.5").appendString("1.0").appendInt(0)
                .appendInt(0).appendString("1")
                .appendInt(-1).appendInt(2).appendInt(77);
    }

    // A furni named by its class: a negative type, then the class name at the end.
    static FloorItem staticFloorItem() {
        return FloorItem.builder()
                .furniId(9).furniClassId(-1).x(0).y(7).direction(Direction.EAST)
                .z("0.0").sizeZ("0.0").extra(0)
                .stuffData(MapStuffData.builder().typeAndFlags(1)
                        .entries(Collections.singletonList(new MapStuffDataEntry("state", "0"))).build())
                .secondsToExpiration(-1).usagePolicy(0).ownerId(0)
                .staticClass("door")
                .build();
    }

    static void appendStaticFloorItem(HPacket p) {
        p.appendInt(9).appendInt(-1).appendInt(0).appendInt(7).appendInt(2)
                .appendString("0.0").appendString("0.0").appendInt(0)
                .appendInt(1).appendInt(1).appendString("state").appendString("0")
                .appendInt(-1).appendInt(0).appendInt(0)
                .appendString("door");
    }

    static WallItem wallItem() {
        return WallItem.builder()
                .furniId("601").furniClassId(4001).location(":w=3,5 l=12,30 r").data("0")
                .secondsToExpiration(3600).usagePolicy(1).ownerId(77)
                .build();
    }

    static void appendWallItem(HPacket p) {
        p.appendString("601").appendInt(4001).appendString(":w=3,5 l=12,30 r").appendString("0")
                .appendInt(3600).appendInt(1).appendInt(77);
    }

    static HPacket incoming(String header) {
        return new HPacket(header, HMessage.Direction.TOCLIENT);
    }

    static HPacket outgoing(String header) {
        return new HPacket(header, HMessage.Direction.TOSERVER);
    }

    // ---- ItemAdd ----

    static HPacket itemAddPacket() {
        HPacket p = incoming("ItemAdd");
        appendWallItem(p);
        p.appendString("Owner");
        return p;
    }

    @Test
    void itemAddToPacket() {
        assertSameBytes(itemAddPacket(), new ItemAdd(wallItem(), "Owner").toPacket());
    }

    @Test
    void itemAddFromPacket() {
        assertEquals(new ItemAdd(wallItem(), "Owner"), ItemAdd.fromPacket(itemAddPacket()));
    }

    // ---- ItemDataUpdate ----

    static HPacket itemDataUpdatePacket() {
        HPacket p = incoming("ItemDataUpdate");
        p.appendString("601").appendString("9CCEFF hello");
        return p;
    }

    @Test
    void itemDataUpdateToPacket() {
        assertSameBytes(itemDataUpdatePacket(), new ItemDataUpdate("601", "9CCEFF hello").toPacket());
    }

    @Test
    void itemDataUpdateFromPacket() {
        assertEquals(new ItemDataUpdate("601", "9CCEFF hello"), ItemDataUpdate.fromPacket(itemDataUpdatePacket()));
    }

    // ---- ItemRemove ----

    static HPacket itemRemovePacket() {
        HPacket p = incoming("ItemRemove");
        p.appendString("601").appendInt(77);
        return p;
    }

    @Test
    void itemRemoveToPacket() {
        assertSameBytes(itemRemovePacket(), new ItemRemove("601", 77).toPacket());
    }

    @Test
    void itemRemoveFromPacket() {
        assertEquals(new ItemRemove("601", 77), ItemRemove.fromPacket(itemRemovePacket()));
    }

    // ---- ItemRemoveMultiple ----

    static HPacket itemRemoveMultiplePacket() {
        HPacket p = incoming("ItemRemoveMultiple");
        p.appendInt(2).appendInt(601).appendInt(602).appendInt(77);
        return p;
    }

    @Test
    void itemRemoveMultipleToPacket() {
        assertSameBytes(itemRemoveMultiplePacket(), new ItemRemoveMultiple(Arrays.asList(601, 602), 77).toPacket());
    }

    @Test
    void itemRemoveMultipleFromPacket() {
        assertEquals(new ItemRemoveMultiple(Arrays.asList(601, 602), 77),
                ItemRemoveMultiple.fromPacket(itemRemoveMultiplePacket()));
    }

    // ---- ItemStateUpdate ----

    static HPacket itemStateUpdatePacket() {
        HPacket p = incoming("ItemStateUpdate");
        p.appendInt(601).appendString("1");
        return p;
    }

    @Test
    void itemStateUpdateToPacket() {
        assertSameBytes(itemStateUpdatePacket(), new ItemStateUpdate(601, "1").toPacket());
    }

    @Test
    void itemStateUpdateFromPacket() {
        assertEquals(new ItemStateUpdate(601, "1"), ItemStateUpdate.fromPacket(itemStateUpdatePacket()));
    }

    // ---- ItemUpdate ----

    static HPacket itemUpdatePacket() {
        HPacket p = incoming("ItemUpdate");
        appendWallItem(p);
        return p;
    }

    @Test
    void itemUpdateToPacket() {
        assertSameBytes(itemUpdatePacket(), new ItemUpdate(wallItem()).toPacket());
    }

    @Test
    void itemUpdateFromPacket() {
        assertEquals(new ItemUpdate(wallItem()), ItemUpdate.fromPacket(itemUpdatePacket()));
    }

    // ---- Items ----

    static HPacket itemsPacket() {
        HPacket p = incoming("Items");
        p.appendInt(1).appendInt(77).appendString("Owner");
        p.appendInt(1);
        appendWallItem(p);
        return p;
    }

    static Items items() {
        return new Items(Collections.singletonList(new FurniOwner(77, "Owner")), Collections.singletonList(wallItem()));
    }

    @Test
    void itemsToPacket() {
        assertSameBytes(itemsPacket(), items().toPacket());
    }

    @Test
    void itemsFromPacket() {
        assertEquals(items(), Items.fromPacket(itemsPacket()));
    }

    // ---- ItemsStateUpdate ----

    static HPacket itemsStateUpdatePacket() {
        HPacket p = incoming("ItemsStateUpdate");
        p.appendInt(2).appendInt(601).appendString("1").appendInt(602).appendString("0");
        return p;
    }

    static ItemsStateUpdate itemsStateUpdate() {
        return new ItemsStateUpdate(Arrays.asList(new WallItemStateUpdate(601, "1"), new WallItemStateUpdate(602, "0")));
    }

    @Test
    void itemsStateUpdateToPacket() {
        assertSameBytes(itemsStateUpdatePacket(), itemsStateUpdate().toPacket());
    }

    @Test
    void itemsStateUpdateFromPacket() {
        assertEquals(itemsStateUpdate(), ItemsStateUpdate.fromPacket(itemsStateUpdatePacket()));
    }

    // ---- ObjectAdd ----

    static HPacket objectAddPacket() {
        HPacket p = incoming("ObjectAdd");
        appendFloorItem(p);
        p.appendString("Owner");
        return p;
    }

    @Test
    void objectAddToPacket() {
        assertSameBytes(objectAddPacket(), new ObjectAdd(floorItem(), "Owner").toPacket());
    }

    @Test
    void objectAddFromPacket() {
        assertEquals(new ObjectAdd(floorItem(), "Owner"), ObjectAdd.fromPacket(objectAddPacket()));
    }

    // ---- ObjectDataUpdate ----

    static HPacket objectDataUpdatePacket() {
        HPacket p = incoming("ObjectDataUpdate");
        p.appendString("501").appendInt(0).appendString("2");
        return p;
    }

    static ObjectDataUpdate objectDataUpdate() {
        return new ObjectDataUpdate("501", LegacyStuffData.builder().typeAndFlags(0).legacyString("2").build());
    }

    @Test
    void objectDataUpdateToPacket() {
        assertSameBytes(objectDataUpdatePacket(), objectDataUpdate().toPacket());
    }

    @Test
    void objectDataUpdateFromPacket() {
        assertEquals(objectDataUpdate(), ObjectDataUpdate.fromPacket(objectDataUpdatePacket()));
    }

    // ---- ObjectRemove ----

    static HPacket objectRemovePacket() {
        HPacket p = incoming("ObjectRemove");
        p.appendString("501").appendBoolean(false).appendInt(77).appendInt(500);
        return p;
    }

    @Test
    void objectRemoveToPacket() {
        assertSameBytes(objectRemovePacket(), new ObjectRemove("501", false, 77, 500).toPacket());
    }

    @Test
    void objectRemoveFromPacket() {
        assertEquals(new ObjectRemove("501", false, 77, 500), ObjectRemove.fromPacket(objectRemovePacket()));
    }

    // ---- ObjectRemoveConfirm ----

    static HPacket objectRemoveConfirmPacket() {
        HPacket p = incoming("ObjectRemoveConfirm");
        p.appendInt(2).appendInt(501).appendString("room.confirm.pickup.title").appendString("room.confirm.pickup.body");
        return p;
    }

    static ObjectRemoveConfirm objectRemoveConfirm() {
        return new ObjectRemoveConfirm(2, 501, "room.confirm.pickup.title", "room.confirm.pickup.body");
    }

    @Test
    void objectRemoveConfirmToPacket() {
        assertSameBytes(objectRemoveConfirmPacket(), objectRemoveConfirm().toPacket());
    }

    @Test
    void objectRemoveConfirmFromPacket() {
        assertEquals(objectRemoveConfirm(), ObjectRemoveConfirm.fromPacket(objectRemoveConfirmPacket()));
    }

    // ---- ObjectRemoveMultiple ----

    static HPacket objectRemoveMultiplePacket() {
        HPacket p = incoming("ObjectRemoveMultiple");
        p.appendInt(2).appendInt(501).appendInt(502).appendInt(77);
        return p;
    }

    @Test
    void objectRemoveMultipleToPacket() {
        assertSameBytes(objectRemoveMultiplePacket(), new ObjectRemoveMultiple(Arrays.asList(501, 502), 77).toPacket());
    }

    @Test
    void objectRemoveMultipleFromPacket() {
        assertEquals(new ObjectRemoveMultiple(Arrays.asList(501, 502), 77),
                ObjectRemoveMultiple.fromPacket(objectRemoveMultiplePacket()));
    }

    // ---- ObjectUpdate ----

    static HPacket objectUpdatePacket() {
        HPacket p = incoming("ObjectUpdate");
        appendFloorItem(p);
        return p;
    }

    @Test
    void objectUpdateToPacket() {
        assertSameBytes(objectUpdatePacket(), new ObjectUpdate(floorItem()).toPacket());
    }

    @Test
    void objectUpdateFromPacket() {
        assertEquals(new ObjectUpdate(floorItem()), ObjectUpdate.fromPacket(objectUpdatePacket()));
    }

    // ---- Objects ----

    static HPacket objectsPacket() {
        HPacket p = incoming("Objects");
        p.appendInt(1).appendInt(77).appendString("Owner");
        p.appendInt(2);
        appendFloorItem(p);
        appendStaticFloorItem(p);
        return p;
    }

    static Objects objects() {
        return new Objects(Collections.singletonList(new FurniOwner(77, "Owner")), Arrays.asList(floorItem(), staticFloorItem()));
    }

    @Test
    void objectsToPacket() {
        assertSameBytes(objectsPacket(), objects().toPacket());
    }

    @Test
    void objectsFromPacket() {
        assertEquals(objects(), Objects.fromPacket(objectsPacket()));
    }

    @Test
    void aFloorItemOnlySendsItsStaticClassForANegativeType() {
        Objects parsed = Objects.fromPacket(objectsPacket());

        assertNull(parsed.objects().get(0).staticClass());
        assertEquals("door", parsed.objects().get(1).staticClass());
    }

    // ---- ObjectsDataUpdate ----

    static HPacket objectsDataUpdatePacket() {
        HPacket p = incoming("ObjectsDataUpdate");
        p.appendInt(1).appendInt(501).appendInt(0).appendString("3");
        return p;
    }

    static ObjectsDataUpdate objectsDataUpdate() {
        return new ObjectsDataUpdate(Collections.singletonList(
                new FloorItemDataUpdate(501, LegacyStuffData.builder().typeAndFlags(0).legacyString("3").build())));
    }

    @Test
    void objectsDataUpdateToPacket() {
        assertSameBytes(objectsDataUpdatePacket(), objectsDataUpdate().toPacket());
    }

    @Test
    void objectsDataUpdateFromPacket() {
        assertEquals(objectsDataUpdate(), ObjectsDataUpdate.fromPacket(objectsDataUpdatePacket()));
    }

    // ---- SlideObjectBundle ----

    static HPacket slideObjectBundleHead(HPacket p) {
        p.appendInt(3).appendInt(4).appendInt(4).appendInt(4);
        p.appendInt(1).appendInt(501).appendString("0.5").appendString("0.0");
        p.appendInt(700);
        return p;
    }

    static SlideObjectBundle.SlideObjectBundleBuilder slideObjectBundle() {
        return SlideObjectBundle.builder()
                .oldX(3).oldY(4).newX(4).newY(4)
                .objects(Collections.singletonList(new SlideObject(501, "0.5", "0.0")))
                .rollerId(700);
    }

    static HPacket slideObjectBundleWithUserPacket() {
        HPacket p = slideObjectBundleHead(incoming("SlideObjectBundle"));
        p.appendInt(1).appendInt(12).appendString("0.5").appendString("0.0");
        return p;
    }

    static SlideObjectBundle slideObjectBundleWithUser() {
        return slideObjectBundle()
                .userMoveType(SlideUserMoveType.WALK).userIndex(12).userOldZ("0.5").userNewZ("0.0")
                .build();
    }

    @Test
    void slideObjectBundleToPacket() {
        assertSameBytes(slideObjectBundleWithUserPacket(), slideObjectBundleWithUser().toPacket());
    }

    @Test
    void slideObjectBundleFromPacket() {
        assertEquals(slideObjectBundleWithUser(), SlideObjectBundle.fromPacket(slideObjectBundleWithUserPacket()));
    }

    @Test
    void aSlidingUserHasTheSameLayoutAsAWalkingOne() {
        HPacket p = slideObjectBundleHead(incoming("SlideObjectBundle"));
        p.appendInt(2).appendInt(12).appendString("0.5").appendString("0.0");
        SlideObjectBundle sliding = slideObjectBundle()
                .userMoveType(SlideUserMoveType.SLIDE).userIndex(12).userOldZ("0.5").userNewZ("0.0")
                .build();

        assertSameBytes(p, sliding.toPacket());
        assertEquals(sliding, SlideObjectBundle.fromPacket(p));
    }

    @Test
    void aBundleWithoutAUserSendsOnlyTheMoveType() {
        HPacket p = slideObjectBundleHead(incoming("SlideObjectBundle"));
        p.appendInt(0);
        SlideObjectBundle none = slideObjectBundle().userMoveType(SlideUserMoveType.NONE).build();

        assertSameBytes(p, none.toPacket());
        assertEquals(none, SlideObjectBundle.fromPacket(p));
    }

    @Test
    void aBundleCanLeaveTheUserPartOut() {
        HPacket p = slideObjectBundleHead(incoming("SlideObjectBundle"));
        SlideObjectBundle bare = slideObjectBundle().build();

        assertSameBytes(p, bare.toPacket());
        assertEquals(bare, SlideObjectBundle.fromPacket(p));
    }

    // ---- ClickFurni ----

    static HPacket clickFurniPacket() {
        HPacket p = outgoing("ClickFurni");
        p.appendInt(-601).appendInt(0);
        return p;
    }

    @Test
    void clickFurniToPacket() {
        assertSameBytes(clickFurniPacket(), ClickFurni.builder().furniId(-601).build().toPacket());
    }

    @Test
    void clickFurniFromPacket() {
        assertEquals(new ClickFurni(-601, 0), ClickFurni.fromPacket(clickFurniPacket()));
    }

    // ---- GetItemData ----

    static HPacket getItemDataPacket() {
        HPacket p = outgoing("GetItemData");
        p.appendInt(601);
        return p;
    }

    @Test
    void getItemDataToPacket() {
        assertSameBytes(getItemDataPacket(), new GetItemData(601).toPacket());
    }

    @Test
    void getItemDataFromPacket() {
        assertEquals(new GetItemData(601), GetItemData.fromPacket(getItemDataPacket()));
    }

    // ---- MoveEntityInFlat ----

    static HPacket moveEntityInFlatPacket() {
        HPacket p = outgoing("MoveEntityInFlat");
        p.appendInt(12).appendInt(5).appendInt(6).appendInt(2);
        return p;
    }

    @Test
    void moveEntityInFlatToPacket() {
        assertSameBytes(moveEntityInFlatPacket(), new MoveEntityInFlat(12, 5, 6, Direction.EAST).toPacket());
    }

    @Test
    void moveEntityInFlatFromPacket() {
        assertEquals(new MoveEntityInFlat(12, 5, 6, Direction.EAST), MoveEntityInFlat.fromPacket(moveEntityInFlatPacket()));
    }

    // ---- MoveObject ----

    static HPacket moveObjectPacket() {
        HPacket p = outgoing("MoveObject");
        p.appendInt(501).appendInt(5).appendInt(6).appendInt(6);
        return p;
    }

    @Test
    void moveObjectToPacket() {
        assertSameBytes(moveObjectPacket(), new MoveObject(501, 5, 6, Direction.WEST).toPacket());
    }

    @Test
    void moveObjectFromPacket() {
        assertEquals(new MoveObject(501, 5, 6, Direction.WEST), MoveObject.fromPacket(moveObjectPacket()));
    }

    // ---- MoveWallItem ----

    static HPacket moveWallItemPacket() {
        HPacket p = outgoing("MoveWallItem");
        p.appendInt(601).appendString(":w=4,5 l=10,20 l");
        return p;
    }

    @Test
    void moveWallItemToPacket() {
        assertSameBytes(moveWallItemPacket(), new MoveWallItem(601, ":w=4,5 l=10,20 l").toPacket());
    }

    @Test
    void moveWallItemFromPacket() {
        assertEquals(new MoveWallItem(601, ":w=4,5 l=10,20 l"), MoveWallItem.fromPacket(moveWallItemPacket()));
    }

    // ---- PickupObject ----

    static HPacket pickupObjectPacket() {
        HPacket p = outgoing("PickupObject");
        p.appendInt(2).appendInt(501).appendBoolean(false);
        return p;
    }

    @Test
    void pickupObjectToPacket() {
        assertSameBytes(pickupObjectPacket(),
                PickupObject.builder().placementType(FurniPlacementType.FLOOR).furniId(501).build().toPacket());
    }

    @Test
    void pickupObjectFromPacket() {
        assertEquals(new PickupObject(FurniPlacementType.FLOOR, 501, false), PickupObject.fromPacket(pickupObjectPacket()));
    }

    @Test
    void aConfirmedWallItemPickupSendsOneAndTrue() {
        HPacket p = outgoing("PickupObject");
        p.appendInt(1).appendInt(601).appendBoolean(true);

        assertSameBytes(p, new PickupObject(FurniPlacementType.WALL, 601, true).toPacket());
    }

    // ---- PlaceObject ----

    static HPacket placeObjectPacket() {
        HPacket p = outgoing("PlaceObject");
        p.appendString("1234 5 6 2");
        return p;
    }

    @Test
    void placeObjectToPacket() {
        assertSameBytes(placeObjectPacket(), new PlaceObject("1234 5 6 2").toPacket());
    }

    @Test
    void placeObjectFromPacket() {
        assertEquals(new PlaceObject("1234 5 6 2"), PlaceObject.fromPacket(placeObjectPacket()));
    }

    // ---- RemoveItem ----

    static HPacket removeItemPacket() {
        HPacket p = outgoing("RemoveItem");
        p.appendInt(601);
        return p;
    }

    @Test
    void removeItemToPacket() {
        assertSameBytes(removeItemPacket(), new RemoveItem(601).toPacket());
    }

    @Test
    void removeItemFromPacket() {
        assertEquals(new RemoveItem(601), RemoveItem.fromPacket(removeItemPacket()));
    }

    // ---- SetClothingChangeData ----

    static HPacket setClothingChangeDataPacket() {
        HPacket p = outgoing("SetClothingChangeData");
        p.appendInt(501).appendString("F").appendString("hd-99999-99999.ch-630-62.lg-695-62");
        return p;
    }

    static SetClothingChangeData setClothingChangeData() {
        return new SetClothingChangeData(501, Gender.FEMALE, "hd-99999-99999.ch-630-62.lg-695-62");
    }

    @Test
    void setClothingChangeDataToPacket() {
        assertSameBytes(setClothingChangeDataPacket(), setClothingChangeData().toPacket());
    }

    @Test
    void setClothingChangeDataFromPacket() {
        assertEquals(setClothingChangeData(), SetClothingChangeData.fromPacket(setClothingChangeDataPacket()));
    }

    // ---- SetItemData ----

    static HPacket setItemDataPacket() {
        HPacket p = outgoing("SetItemData");
        p.appendInt(601).appendString("FFFF33").appendString("hello");
        return p;
    }

    @Test
    void setItemDataToPacket() {
        assertSameBytes(setItemDataPacket(), new SetItemData(601, "FFFF33", "hello").toPacket());
    }

    @Test
    void setItemDataFromPacket() {
        assertEquals(new SetItemData(601, "FFFF33", "hello"), SetItemData.fromPacket(setItemDataPacket()));
    }

    // ---- SetObjectData ----

    static HPacket setObjectDataPacket() {
        HPacket p = outgoing("SetObjectData");
        p.appendInt(501).appendInt(2).appendString("videoId").appendString("42");
        return p;
    }

    @Test
    void setObjectDataToPacket() {
        assertSameBytes(setObjectDataPacket(), new SetObjectData(501, Arrays.asList("videoId", "42")).toPacket());
    }

    @Test
    void setObjectDataFromPacket() {
        assertEquals(new SetObjectData(501, Arrays.asList("videoId", "42")), SetObjectData.fromPacket(setObjectDataPacket()));
    }

    @Test
    void setObjectDataRefusesAKeyWithoutAValue() {
        SetObjectData odd = new SetObjectData(501, Arrays.asList("videoId", "42", "extra"));

        LimitException e = assertThrows(LimitException.class, odd::toPacket);

        assertEquals("SetObjectData breaks 1 limit (use toPacketUnchecked() to send it anyway):\n"
                + "  SetObjectData: data holds keys and values in pairs (an even number of strings)", e.getMessage());
    }

    // ---- UseFurniture ----

    static HPacket useFurniturePacket() {
        HPacket p = outgoing("UseFurniture");
        p.appendInt(501).appendInt(0);
        return p;
    }

    @Test
    void useFurnitureToPacket() {
        assertSameBytes(useFurniturePacket(), UseFurniture.builder().furniId(501).build().toPacket());
    }

    @Test
    void useFurnitureFromPacket() {
        assertEquals(new UseFurniture(501, 0), UseFurniture.fromPacket(useFurniturePacket()));
    }

    // ---- UseWallItem ----

    static HPacket useWallItemPacket() {
        HPacket p = outgoing("UseWallItem");
        p.appendInt(601).appendInt(0);
        return p;
    }

    @Test
    void useWallItemToPacket() {
        assertSameBytes(useWallItemPacket(), UseWallItem.builder().furniId(601).build().toPacket());
    }

    @Test
    void useWallItemFromPacket() {
        assertEquals(new UseWallItem(601, 0), UseWallItem.fromPacket(useWallItemPacket()));
    }
}
