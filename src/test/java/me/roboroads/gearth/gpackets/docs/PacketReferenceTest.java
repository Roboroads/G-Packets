package me.roboroads.gearth.gpackets.docs;

import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
