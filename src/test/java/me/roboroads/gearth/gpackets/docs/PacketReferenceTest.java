package me.roboroads.gearth.gpackets.docs;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.PacketTypes;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testfixtures.NoTypePacket;
import testfixtures.limits.LimitFixtures;
import testfixtures.openenum.OpenEnumFixtures.Shade;
import testfixtures.unused.UnusedFixtures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.UnaryOperator;

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxLength;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.notEmpty;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.range;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.requiresVip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketReferenceTest {

    static class Plain {
    }

    static class Tree {
    }

    static class Leaf {
    }

    @CheckedAgainst("WIN63-202609091217-117204808")
    static class Room {
    }

    @CheckedAgainst("WIN63-202609091217-117204808")
    static class Door {
    }

    @CheckedAgainst("WIN63-202604010000-111111111")
    static class Window {
    }

    static class Node {
        static final Schema<Node> SCHEMA = Schema.of(Node.class)
                .string("name")
                .list("children", () -> Node.SCHEMA);
    }

    private static final Schema<Leaf> LEAF = Schema.of(Leaf.class).string("label");

    private static final String TABLE_HEAD = "| Name | Type | Notes |\n|---|---|---|\n";

    @Test
    void rendersValuesAndEnumOptions() {
        Schema<Plain> schema = Schema.of(Plain.class).integer("id").enumInt("dir", Direction.class);

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `id` | int |  |\n"
                        + "| `dir` | int → `Direction` |  |\n"
                        + "\n`dir` (`Direction`): `NORTH` = `0`, `NORTH_EAST` = `1`, `EAST` = `2`, `SOUTH_EAST` = `3`, "
                        + "`SOUTH` = `4`, `SOUTH_WEST` = `5`, `WEST` = `6`, `NORTH_WEST` = `7`\n",
                PacketReference.body(schema));
    }

    @Test
    void rendersListsAndStructsAsLinkedSectionsOnce() {
        Schema<Tree> schema = Schema.of(Tree.class)
                .list("numbers", WireType.SHORT)
                .list("leaves", LEAF)
                .struct("root", Node.SCHEMA);

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `numbers` | list of short |  |\n"
                        + "| `leaves` | list of [Leaf](#leaf) |  |\n"
                        + "| `root` | [Node](#node) |  |\n"
                        + "\n## Leaf\n\n" + TABLE_HEAD
                        + "| `label` | string |  |\n"
                        + "\n## Node\n\n" + TABLE_HEAD
                        + "| `name` | string |  |\n"
                        + "| `children` | list of [Node](#node) |  |\n",
                PacketReference.body(schema));
    }

    @Test
    void aStructureOnlySaysItsBuildWhenItDiffersFromThePackets() {
        Schema<Room> schema = Schema.of(Room.class)
                .struct("door", Schema.of(Door.class).integer("width"))
                .struct("window", Schema.of(Window.class).integer("height"));

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `door` | [Door](#door) |  |\n"
                        + "| `window` | [Window](#window) |  |\n"
                        + "\n## Door\n\n" + TABLE_HEAD
                        + "| `width` | int |  |\n"
                        + "\n## Window\n\n"
                        + "Checked against client build `WIN63-202604010000-111111111`.\n\n" + TABLE_HEAD
                        + "| `height` | int |  |\n",
                PacketReference.body(schema));
    }

    @Test
    void namesACountThatIsntAnInt() {
        Schema<Tree> schema = Schema.of(Tree.class).listWithCount("leaves", WireType.BYTE, LEAF);

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `leaves` | list of [Leaf](#leaf), counted by a byte |  |\n"
                        + "\n## Leaf\n\n" + TABLE_HEAD
                        + "| `label` | string |  |\n",
                PacketReference.body(schema));
    }

    @Test
    void marksOptionalParameters() {
        Schema<Plain> schema = Schema.of(Plain.class).bool("flag").optional(o -> o.string("hash"));

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `flag` | boolean |  |\n"
                        + "| `hash` | string | optional: only present if the packet has bytes left |\n",
                PacketReference.body(schema));
    }

    @Test
    void notesTheLimits() {
        Schema<Plain> schema = Schema.of(Plain.class)
                .string("name", notEmpty(), maxLength(5))
                .integer("sleep", range(30, 3600).orZero(), requiresVip());

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `name` | string | not empty; at most 5 characters |\n"
                        + "| `sleep` | int | 30 to 3600, or 0; VIP only |\n",
                PacketReference.body(schema));
    }

    @Test
    void listsTheRulesAfterTheTable() {
        Schema<Plain> schema = Schema.of(Plain.class).integer("a").rule("a is positive", values -> true);

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `a` | int |  |\n"
                        + "\nRules:\n\n- a is positive\n",
                PacketReference.body(schema));
    }

    @Test
    void anOutgoingPageWithLimitsSaysTheyAreChecked() {
        assertTrue(PacketReference.page(LimitFixtures.Outgoing.TYPE).contains(
                "```\n\nLimits are checked when you send this packet, see [Limits](../../changing-and-sending.md#limits).\n\n## Parameters"));
        assertFalse(PacketReference.page(LimitFixtures.Incoming.TYPE).contains("Limits are checked"));
    }

    @Test
    void saysSoWhenThereAreNoParameters() {
        assertEquals("## Parameters\n\nNo parameters.\n", PacketReference.body(Schema.of(Plain.class)));
    }

    @Test
    @SuppressWarnings("deprecation")
    void notesWhyTheClientIgnoresAParameter() {
        Schema<UnusedFixtures.Base> schema = Schema.of(UnusedFixtures.Base.class).integer("legacy").integer("kind");

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `legacy` | int | Unused by the client: The client stores it but never reads it |\n"
                        + "| `kind` | int |  |\n",
                PacketReference.body(schema));
    }

    @Test
    @SuppressWarnings("deprecation")
    void theUnusedNoteFollowsTheOptionalNote() {
        Schema<UnusedFixtures.Child> schema = Schema.of(UnusedFixtures.Child.class).integer("kind").optional(o -> o.string("extra"));

        String body = PacketReference.body(schema);

        assertTrue(body.contains("| `extra` | string | optional: only present if the packet has bytes left; "
                + "Unused by the client: Only sent by old servers |\n"), body);
    }

    @Test
    void marksUnusedEnumOptions() {
        Schema<Plain> schema = Schema.of(Plain.class).enumInt("mode", UnusedFixtures.Mode.class);

        String body = PacketReference.body(schema);

        assertTrue(body.endsWith("\n`mode` (`Mode`): `ON` = `1`, `OFF` = `0` (unused)\n"), body);
    }

    @Test
    void saysWhenAnEnumKeepsOtherIds() {
        Schema<Plain> schema = Schema.of(Plain.class).enumInt("shade", Shade.class);

        String body = PacketReference.body(schema);

        assertTrue(body.contains("| `shade` | int → `Shade` |  |\n"), body);
        assertTrue(body.endsWith("\n`shade` (`Shade`): `LIGHT` = `1`, `DARK` = `2`, `FADED` = `3` (unused). Other ids pass through.\n"), body);
    }

    @Test
    @SuppressWarnings("deprecation")
    void anUnusedPacketGetsAWarningAndAnIndexMark() {
        PacketType<UnusedFixtures.IgnoredPacket> ignored =
                PacketType.of("Ignored", HMessage.Direction.TOCLIENT, Schema.of(UnusedFixtures.IgnoredPacket.class));

        assertTrue(PacketReference.page(ignored).startsWith("# Ignored\n\n"
                + "!!! warning \"Unused by the client\"\n    The client's handler ignores it\n\n"
                + "- Direction: incoming (to client)\n"), PacketReference.page(ignored));
        String index = PacketReference.index(Collections.<PacketType<?>>singletonList(ignored));
        assertTrue(index.contains("| [Ignored](incoming/Ignored.md) (unused) | `testfixtures.unused.UnusedFixtures$IgnoredPacket` |\n"), index);
    }

    static class Animal {
    }

    static class Dog extends Animal {
    }

    static class Cat extends Animal {
    }

    @Test
    void rendersBranchCasesAndGroupsCasesThatRenderTheSame() {
        UnaryOperator<Schema<Cat>> cat = c -> c.bool("indoor").when("indoor", true, w -> w.string("room"));
        Schema<Animal> schema = Schema.of(Animal.class)
                .enumInt("kind", UserType.class)
                .branch("kind", cases -> cases
                        .on(UserType.PLAYER, Dog.class, d -> d.integer("bark"))
                        .on(UserType.PET, Cat.class, cat)
                        .on(UserType.BOT, Cat.class, cat));

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `kind` | int → `UserType` |  |\n"
                        + "|  |  | depends on `kind`, see below |\n"
                        + "\n`kind` (`UserType`): `PLAYER` = `1`, `PET` = `2`, `OLD_BOT` = `3`, `BOT` = `4`\n"
                        + "\n### When `kind` is `1` (`PLAYER`): `Dog`\n\n" + TABLE_HEAD
                        + "| `bark` | int |  |\n"
                        + "\n### When `kind` is one of `2` (`PET`), `4` (`BOT`): `Cat`\n\n" + TABLE_HEAD
                        + "| `indoor` | boolean |  |\n"
                        + "| `room` | string | only when `indoor` is `true` |\n",
                PacketReference.body(schema));
    }

    @Test
    void rendersTheParametersOfAWhenOneOfOnce() {
        Schema<Animal> schema = Schema.of(Animal.class)
                .integer("moveType")
                .whenOneOf("moveType", Arrays.asList(1, 2), w -> w.integer("speed"));

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `moveType` | int |  |\n"
                        + "| `speed` | int | only when `moveType` is one of `1`, `2` |\n",
                PacketReference.body(schema));
    }

    @Test
    void saysNegativeForASignBitMask() {
        Schema<Animal> schema = Schema.of(Animal.class)
                .integer("typeId")
                .when("typeId", Integer.MIN_VALUE, Integer.MIN_VALUE, w -> w.string("className"));

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `typeId` | int |  |\n"
                        + "| `className` | string | only when `typeId` is negative |\n",
                PacketReference.body(schema));
    }

    @Test
    void rendersANegatedWhenWithTheEnumName() {
        Schema<Animal> schema = Schema.of(Animal.class)
                .enumInt("kind", UserType.class)
                .whenNot("kind", UserType.PLAYER, w -> w.integer("ownerId"))
                .doubleValue("weight");

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `kind` | int → `UserType` |  |\n"
                        + "| `ownerId` | int | only when `kind` is not `1` (`PLAYER`) |\n"
                        + "| `weight` | double |  |\n"
                        + "\n`kind` (`UserType`): `PLAYER` = `1`, `PET` = `2`, `OLD_BOT` = `3`, `BOT` = `4`\n",
                PacketReference.body(schema));
    }

    @Test
    void pageShowsDirectionClassAndAnInterceptSnippet() {
        assertTrue(PacketReference.page(Chat.TYPE).startsWith("# Chat\n\n"
                + "- Direction: outgoing (to server)\n"
                + "- Class: `me.roboroads.gearth.gpackets.outgoing.Chat`\n"
                + "- Checked against client build `" + Chat.TYPE.checkedAgainst() + "`\n\n"
                + "```java\n@Intercept\nvoid onChat(Chat packet) {\n    // ...\n}\n```\n\n"
                + "## Parameters\n\n"));
    }

    @Test
    void indexListsPacketsByDirection() {
        assertEquals("# Packet reference\n\n"
                        + "Every packet G-Packets implements, generated from the packet schemas. "
                        + "Each page lists the packet's parameters in the order they appear on the wire.\n"
                        + "\n## Incoming (to client)\n\n| Header | Class |\n|---|---|\n"
                        + "| [Users](incoming/Users.md) | `me.roboroads.gearth.gpackets.incoming.Users` |\n"
                        + "\n## Outgoing (to server)\n\n| Header | Class |\n|---|---|\n"
                        + "| [Chat](outgoing/Chat.md) | `me.roboroads.gearth.gpackets.outgoing.Chat` |\n",
                PacketReference.index(Arrays.<PacketType<?>>asList(Users.TYPE, Chat.TYPE)));
    }

    @Test
    void writesAnIndexAndAPagePerPacket(@TempDir Path directory) throws IOException {
        PacketReference.write(PacketTypes.all(), directory);

        assertTrue(Files.exists(directory.resolve("index.md")));
        for (PacketType<?> type : PacketTypes.all()) {
            assertTrue(Files.exists(directory.resolve(PacketReference.path(type))), PacketReference.path(type) + " is missing");
        }
        String catalogIndex = read(directory.resolve("incoming/CatalogIndex.md"));
        assertEquals(1, occurrences(catalogIndex, "\n## CatalogNode\n"));
        assertTrue(catalogIndex.contains("| `children` | list of [CatalogNode](#catalognode) |  |"));
        String catalogPage = read(directory.resolve("incoming/CatalogPage.md"));
        assertTrue(catalogPage.contains("When `productType` is one of `\"i\"` (`ITEM`), `\"s\"` (`STUFF`)"), catalogPage);
        assertEquals(1, occurrences(catalogPage, ": `FurniProduct`\n"));
        assertTrue(catalogPage.contains("| `unknownBoolean12` | boolean | Unused by the client: The client stores it but never reads it |"),
                catalogPage);
        String chat = read(directory.resolve("outgoing/Chat.md"));
        assertTrue(chat.contains("`NFT_MONEY` = `1027`, `RECYCLED` = `10000`. Other ids pass through.\n"), chat);
        String tradingItems = read(directory.resolve("incoming/TradingItemList.md"));
        assertTrue(tradingItems.contains("When `typeAndFlags & 255` is `0`: `LegacyStuffData`"), tradingItems);
        assertTrue(tradingItems.contains("| `uniqueSerialNumber` | int | only when `typeAndFlags & 256` is `256` |"), tradingItems);
        assertEquals(8, occurrences(tradingItems, "When `typeAndFlags & 255` is"));
        String index = read(directory.resolve("index.md"));
        assertTrue(index.contains("| [RoomSettingsError](incoming/RoomSettingsError.md) (unused) |"), index);
    }

    @Test
    void aHeaderUsedInBothDirectionsGetsTwoPages(@TempDir Path directory) throws IOException {
        PacketType<NoTypePacket> incomingChat = PacketType.of("Chat", HMessage.Direction.TOCLIENT, Schema.of(NoTypePacket.class));

        PacketReference.write(Arrays.<PacketType<?>>asList(Chat.TYPE, incomingChat), directory);

        assertTrue(read(directory.resolve("outgoing/Chat.md")).contains("- Direction: outgoing (to server)"));
        assertTrue(read(directory.resolve("incoming/Chat.md")).contains("- Direction: incoming (to client)"));
        String index = read(directory.resolve("index.md"));
        assertTrue(index.contains("| [Chat](incoming/Chat.md) |"), index);
        assertTrue(index.contains("| [Chat](outgoing/Chat.md) |"), index);
    }

    private static String read(Path file) throws IOException {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private static int occurrences(String text, String part) {
        int count = 0;
        for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + 1)) {
            count++;
        }
        return count;
    }
}
