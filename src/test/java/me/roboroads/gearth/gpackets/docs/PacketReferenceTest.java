package me.roboroads.gearth.gpackets.docs;

import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.PacketTypes;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketReferenceTest {

    static class Plain {
    }

    static class Tree {
    }

    static class Leaf {
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
    void marksOptionalParameters() {
        Schema<Plain> schema = Schema.of(Plain.class).bool("flag").optional(o -> o.string("hash"));

        assertEquals("## Parameters\n\n" + TABLE_HEAD
                        + "| `flag` | boolean |  |\n"
                        + "| `hash` | string | optional: only present if the packet has bytes left |\n",
                PacketReference.body(schema));
    }

    @Test
    void saysSoWhenThereAreNoParameters() {
        assertEquals("## Parameters\n\nNo parameters.\n", PacketReference.body(Schema.of(Plain.class)));
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
    void pageShowsDirectionClassAndAnInterceptSnippet() {
        assertTrue(PacketReference.page(Chat.TYPE).startsWith("# Chat\n\n"
                + "- Direction: outgoing (to server)\n"
                + "- Class: `me.roboroads.gearth.gpackets.outgoing.Chat`\n\n"
                + "```java\n@Intercept\nvoid onChat(Chat packet) {\n    // ...\n}\n```\n\n"
                + "## Parameters\n\n"));
    }

    @Test
    void indexListsPacketsByDirection() {
        assertEquals("# Packet reference\n\n"
                        + "Every packet G-Packets implements, generated from the packet schemas. "
                        + "Each page lists the packet's parameters in the order they appear on the wire.\n"
                        + "\n## Incoming (to client)\n\n| Header | Class |\n|---|---|\n"
                        + "| [Users](Users.md) | `me.roboroads.gearth.gpackets.incoming.Users` |\n"
                        + "\n## Outgoing (to server)\n\n| Header | Class |\n|---|---|\n"
                        + "| [Chat](Chat.md) | `me.roboroads.gearth.gpackets.outgoing.Chat` |\n",
                PacketReference.index(Arrays.<PacketType<?>>asList(Users.TYPE, Chat.TYPE)));
    }

    @Test
    void writesAnIndexAndAPagePerPacket(@TempDir Path directory) throws IOException {
        PacketReference.write(PacketTypes.all(), directory);

        assertTrue(Files.exists(directory.resolve("index.md")));
        for (PacketType<?> type : PacketTypes.all()) {
            assertTrue(Files.exists(directory.resolve(type.header() + ".md")), type.header() + ".md is missing");
        }
        String catalogIndex = read(directory.resolve("CatalogIndex.md"));
        assertEquals(1, occurrences(catalogIndex, "\n## CatalogNode\n"));
        assertTrue(catalogIndex.contains("| `children` | list of [CatalogNode](#catalognode) |  |"));
        String catalogPage = read(directory.resolve("CatalogPage.md"));
        assertTrue(catalogPage.contains("When `productType` is one of `\"i\"` (`ITEM`), `\"s\"` (`STUFF`)"), catalogPage);
        assertEquals(1, occurrences(catalogPage, ": `FurniProduct`\n"));
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
