package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.AvatarEffect;
import me.roboroads.gearth.gpackets.incoming.CarryObject;
import me.roboroads.gearth.gpackets.incoming.Dance;
import me.roboroads.gearth.gpackets.incoming.Expression;
import me.roboroads.gearth.gpackets.incoming.HandItemReceived;
import me.roboroads.gearth.gpackets.incoming.Sleep;
import me.roboroads.gearth.gpackets.incoming.UseObject;
import me.roboroads.gearth.gpackets.incoming.UserChange;
import me.roboroads.gearth.gpackets.incoming.UserRemove;
import me.roboroads.gearth.gpackets.incoming.UserUpdate;
import me.roboroads.gearth.gpackets.incoming.sub.user.UnknownUserChangeEntry;
import me.roboroads.gearth.gpackets.incoming.sub.user.UserUpdateData;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.ExpressionType;
import me.roboroads.gearth.gpackets.outgoing.DropCarryItem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation") // tests UserChange's unused parameters
class RoomAvatarWireFormatTest {

    // ---- AvatarEffect ----

    static HPacket avatarEffectPacket() {
        HPacket p = new HPacket("AvatarEffect", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(108).appendInt(500);
        return p;
    }

    @Test
    void avatarEffectToPacket() {
        assertSameBytes(avatarEffectPacket(), new AvatarEffect(3, 108, 500).toPacket());
    }

    @Test
    void avatarEffectFromPacket() {
        assertEquals(new AvatarEffect(3, 108, 500), AvatarEffect.fromPacket(avatarEffectPacket()));
    }

    // ---- CarryObject ----

    static HPacket carryObjectPacket() {
        HPacket p = new HPacket("CarryObject", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(2);
        return p;
    }

    @Test
    void carryObjectToPacket() {
        assertSameBytes(carryObjectPacket(), new CarryObject(3, 2).toPacket());
    }

    @Test
    void carryObjectFromPacket() {
        assertEquals(new CarryObject(3, 2), CarryObject.fromPacket(carryObjectPacket()));
    }

    // ---- Dance (incoming) ----

    static HPacket dancePacket() {
        HPacket p = new HPacket("Dance", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(2);
        return p;
    }

    @Test
    void danceToPacket() {
        assertSameBytes(dancePacket(), new Dance(3, 2).toPacket());
    }

    @Test
    void danceFromPacket() {
        assertEquals(new Dance(3, 2), Dance.fromPacket(dancePacket()));
    }

    // ---- Expression ----

    static HPacket expressionPacket() {
        HPacket p = new HPacket("Expression", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(67);
        return p;
    }

    @Test
    void expressionToPacket() {
        assertSameBytes(expressionPacket(), new Expression(3, ExpressionType.EXPRESSION_67).toPacket());
    }

    @Test
    void expressionFromPacket() {
        assertEquals(new Expression(3, ExpressionType.EXPRESSION_67), Expression.fromPacket(expressionPacket()));
    }

    // ---- HandItemReceived ----

    static HPacket handItemReceivedPacket() {
        HPacket p = new HPacket("HandItemReceived", HMessage.Direction.TOCLIENT);
        p.appendInt(5).appendInt(7);
        return p;
    }

    @Test
    void handItemReceivedToPacket() {
        assertSameBytes(handItemReceivedPacket(), new HandItemReceived(5, 7).toPacket());
    }

    @Test
    void handItemReceivedFromPacket() {
        assertEquals(new HandItemReceived(5, 7), HandItemReceived.fromPacket(handItemReceivedPacket()));
    }

    // ---- Sleep ----

    static HPacket sleepPacket() {
        HPacket p = new HPacket("Sleep", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendBoolean(true);
        return p;
    }

    @Test
    void sleepToPacket() {
        assertSameBytes(sleepPacket(), new Sleep(3, true).toPacket());
    }

    @Test
    void sleepFromPacket() {
        assertEquals(new Sleep(3, true), Sleep.fromPacket(sleepPacket()));
    }

    // ---- UseObject ----

    static HPacket useObjectPacket() {
        HPacket p = new HPacket("UseObject", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(1);
        return p;
    }

    @Test
    void useObjectToPacket() {
        assertSameBytes(useObjectPacket(), new UseObject(3, 1).toPacket());
    }

    @Test
    void useObjectFromPacket() {
        assertEquals(new UseObject(3, 1), UseObject.fromPacket(useObjectPacket()));
    }

    // ---- UserChange ----

    static UserChange userChange() {
        return UserChange.builder()
                .id(3).figure("hd-180-1.ch-210-66").sex("m").customInfo("motto").achievementScore(120)
                .unknownString6("x")
                .unknownList7(Arrays.asList(new UnknownUserChangeEntry(1, 2, 3), new UnknownUserChangeEntry(4, 5, 6)))
                .badgesRank(9)
                .build();
    }

    static HPacket userChangePacket() {
        HPacket p = new HPacket("UserChange", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendString("hd-180-1.ch-210-66").appendString("m").appendString("motto").appendInt(120)
                .appendString("x")
                .appendInt(2).appendInt(1).appendInt(2).appendInt(3).appendInt(4).appendInt(5).appendInt(6)
                .appendInt(9);
        return p;
    }

    @Test
    void userChangeToPacket() {
        assertSameBytes(userChangePacket(), userChange().toPacket());
    }

    @Test
    void userChangeFromPacket() {
        assertEquals(userChange(), UserChange.fromPacket(userChangePacket()));
    }

    // ---- UserRemove ----

    static HPacket userRemovePacket() {
        HPacket p = new HPacket("UserRemove", HMessage.Direction.TOCLIENT);
        p.appendString("3");
        return p;
    }

    @Test
    void userRemoveToPacket() {
        assertSameBytes(userRemovePacket(), new UserRemove("3").toPacket());
    }

    @Test
    void userRemoveFromPacket() {
        assertEquals(new UserRemove("3"), UserRemove.fromPacket(userRemovePacket()));
    }

    // ---- UserUpdate ----

    static UserUpdate userUpdate() {
        return new UserUpdate(Arrays.asList(
                new UserUpdateData(3, 5, 6, "0.0", Direction.EAST, Direction.SOUTH_EAST, 0, "/mv 6,6,0.0/"),
                new UserUpdateData(4, 1, 2, "1.5", Direction.NORTH, Direction.NORTH, 0, "/sit 1.0 1/")));
    }

    static HPacket userUpdatePacket() {
        HPacket p = new HPacket("UserUpdate", HMessage.Direction.TOCLIENT);
        p.appendInt(2);
        p.appendInt(3).appendInt(5).appendInt(6).appendString("0.0").appendInt(2).appendInt(3).appendInt(0)
                .appendString("/mv 6,6,0.0/");
        p.appendInt(4).appendInt(1).appendInt(2).appendString("1.5").appendInt(0).appendInt(0).appendInt(0)
                .appendString("/sit 1.0 1/");
        return p;
    }

    @Test
    void userUpdateToPacket() {
        assertSameBytes(userUpdatePacket(), userUpdate().toPacket());
    }

    @Test
    void userUpdateFromPacket() {
        assertEquals(userUpdate(), UserUpdate.fromPacket(userUpdatePacket()));
    }

    // ---- DropCarryItem ----

    static HPacket dropCarryItemPacket() {
        return new HPacket("DropCarryItem", HMessage.Direction.TOSERVER);
    }

    @Test
    void dropCarryItemToPacket() {
        assertSameBytes(dropCarryItemPacket(), new DropCarryItem().toPacket());
    }

    @Test
    void dropCarryItemFromPacket() {
        assertEquals(new DropCarryItem(), DropCarryItem.fromPacket(dropCarryItemPacket()));
    }
}
