package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.WiredMovements;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.CatalogNode;
import me.roboroads.gearth.gpackets.incoming.sub.wired.UserDirectionUpdate;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WallItemMove;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredMovement;
import me.roboroads.gearth.gpackets.model.enums.CatalogType;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogIndex;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPageWithEarliestExpiry;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinderTest {

    private static final Schema<Chat> CHAT = Schema.of(Chat.class)
            .string("text")
            .enumInt("style", ChatBarStyle.class)
            .integer("trackingId");

    private static final Schema<WiredMovement> MOVEMENT = Schema.of(WiredMovement.class)
            .enumInt("movementType", WiredMovementType.class)
            .branch("movementType", cases -> cases
                    .on(WiredMovementType.WALL_ITEM_MOVE, WallItemMove.class, s -> s.integer("itemId"))
                    .on(WiredMovementType.USER_DIRECTION_UPDATE, UserDirectionUpdate.class, s -> s
                            .integer("userIndex")
                            .enumInt("bodyDirection", Direction.class)
                            .enumInt("headDirection", Direction.class)));

    private static HPacket packet() {
        return new HPacket("Test", HMessage.Direction.TOCLIENT);
    }

    private static String bytes(HPacket packet) {
        return Arrays.toString(packet.toBytes());
    }

    @Test
    void parseBuildsThroughTheBuilder() {
        HPacket p = packet().appendString("hi").appendInt(2).appendInt(7);

        assertEquals(new Chat("hi", ChatBarStyle.ROBOT, 7), CHAT.parse(p));
    }

    @Test
    void appendReadsThroughTheGetters() {
        HPacket out = packet();

        CHAT.append(new Chat("hi", ChatBarStyle.ROBOT, 7), out);

        assertEquals(bytes(packet().appendString("hi").appendInt(2).appendInt(7)), bytes(out));
    }

    @Test
    void absentValuesKeepBuilderDefaults() {
        Schema<Chat> textOnly = Schema.of(Chat.class).string("text");

        assertEquals(-1, textOnly.parse(packet().appendString("hi")).trackingId());
    }

    @Test
    void unknownEnumValuesBecomeNull() {
        assertNull(CHAT.parse(packet().appendString("hi").appendInt(999).appendInt(7)).style());
    }

    @Test
    void stringEnumsMatchIgnoringCase() {
        Schema<GetCatalogIndex> schema = Schema.of(GetCatalogIndex.class).enumString("catalogType", CatalogType.class);

        assertEquals(CatalogType.NORMAL, schema.parse(packet().appendString("normal")).catalogType());
    }

    @Test
    void parsePicksTheSubclassFromTheBranch() {
        HPacket p = packet().appendInt(3).appendInt(9).appendInt(1).appendInt(5);
        UserDirectionUpdate expected = UserDirectionUpdate.builder()
                .movementType(WiredMovementType.USER_DIRECTION_UPDATE)
                .userIndex(9).bodyDirection(Direction.NORTH_EAST).headDirection(Direction.SOUTH_WEST)
                .build();

        assertEquals(expected, MOVEMENT.parse(p));
    }

    @Test
    void appendFillsANullDiscriminatorFromTheClass() {
        HPacket out = packet();

        MOVEMENT.append(UserDirectionUpdate.builder()
                .userIndex(9).bodyDirection(Direction.NORTH).headDirection(Direction.NORTH)
                .build(), out);

        assertEquals(bytes(packet().appendInt(3).appendInt(9).appendInt(0).appendInt(0)), bytes(out));
    }

    @Test
    void appendRejectsADiscriminatorThatContradictsTheClass() {
        UserDirectionUpdate wrong = UserDirectionUpdate.builder()
                .movementType(WiredMovementType.WALL_ITEM_MOVE).userIndex(9)
                .build();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MOVEMENT.append(wrong, packet()));

        assertEquals("WiredMovement.movementType: is 2 but the object is a UserDirectionUpdate", e.getMessage());
    }

    @Test
    void listsOfSchemasBindEachElement() {
        Schema<WiredMovements> schema = Schema.of(WiredMovements.class).list("movements", MOVEMENT);
        HPacket p = packet().appendInt(2)
                .appendInt(2).appendInt(55)
                .appendInt(3).appendInt(1).appendInt(0).appendInt(0);

        WiredMovements parsed = schema.parse(p);

        assertInstanceOf(WallItemMove.class, parsed.movements().get(0));
        assertEquals(55, ((WallItemMove) parsed.movements().get(0)).itemId());
        assertInstanceOf(UserDirectionUpdate.class, parsed.movements().get(1));
    }

    @Test
    void structsBindToNestedObjects() {
        Schema<CatalogIndex> schema = Schema.of(CatalogIndex.class)
                .struct("root", Schema.of(CatalogNode.class).string("pageName"));

        assertEquals("root", schema.parse(packet().appendString("root")).root().pageName());
    }

    @Test
    void aClassWithoutABuilderIsBuiltWithItsConstructor() {
        Schema<GetCatalogPageWithEarliestExpiry> empty = Schema.of(GetCatalogPageWithEarliestExpiry.class);

        assertEquals(new GetCatalogPageWithEarliestExpiry(), empty.parse(packet()));
    }
}
