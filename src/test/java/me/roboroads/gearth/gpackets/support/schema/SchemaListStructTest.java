package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaListStructTest {

    static class Sample {
    }

    static class Item {
    }

    static class Node {
        static final Schema<Node> SCHEMA = Schema.of(Node.class)
                .string("name")
                .list("children", () -> Node.SCHEMA);
    }

    private static final Schema<Item> ITEM = Schema.of(Item.class).integer("a").string("b");

    private static final Schema<Sample> SCHEMA = Schema.of(Sample.class)
            .list("numbers", WireType.SHORT)
            .list("items", ITEM)
            .struct("inner", ITEM);

    private static HPacket packet() {
        return new HPacket("Test", HMessage.Direction.TOCLIENT);
    }

    private static HPacket fullPacket() {
        HPacket p = packet();
        p.appendInt(2).appendShort((short) 1).appendShort((short) 2);
        p.appendInt(1).appendInt(7).appendString("x");
        p.appendInt(8).appendString("y");
        return p;
    }

    private static Map<String, Object> map(Object... keysAndValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }

    private static String bytes(HPacket packet) {
        return Arrays.toString(packet.toBytes());
    }

    @Test
    void readsListsAsListsAndStructsAsMaps() {
        Map<String, Object> values = SCHEMA.read(fullPacket());

        assertEquals(Arrays.asList((short) 1, (short) 2), values.get("numbers"));
        assertEquals(Collections.singletonList(map("a", 7, "b", "x")), values.get("items"));
        assertEquals(map("a", 8, "b", "y"), values.get("inner"));
    }

    @Test
    void writeThenReadReproducesTheBytes() {
        HPacket out = packet();
        SCHEMA.write(SCHEMA.read(fullPacket()), out);

        assertEquals(bytes(fullPacket()), bytes(out));
    }

    @Test
    void nullListsAndStructsWriteDefaults() {
        HPacket out = packet();
        SCHEMA.write(new HashMap<>(), out);

        HPacket expected = packet();
        expected.appendInt(0).appendInt(0).appendInt(0).appendString("");
        assertEquals(bytes(expected), bytes(out));
    }

    @Test
    void aNegativeCountReadsAsAnEmptyList() {
        HPacket p = packet();
        p.appendInt(-1).appendInt(0).appendInt(0).appendString("");

        assertEquals(Collections.emptyList(), SCHEMA.read(p).get("numbers"));
    }

    @Test
    void garbageCountFailsWithAPathInsteadOfAllocating() {
        HPacket p = packet();
        p.appendInt(2_000_000_000);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SCHEMA.read(p));

        assertEquals("Sample.numbers[0]: packet ended before this SHORT could be read", e.getMessage());
    }

    @Test
    void errorsInListElementsNameTheIndex() {
        Map<String, Object> values = map("items", Arrays.asList(map("a", 1, "b", "x"), map("a", "seven", "b", "y")));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SCHEMA.write(values, packet()));

        assertEquals("Sample.items[1].a: expected INT, got java.lang.String", e.getMessage());
    }

    @Test
    void primitiveListElementsAreCoerced() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> SCHEMA.write(map("numbers", Collections.singletonList("x")), packet()));

        assertEquals("Sample.numbers[0]: expected SHORT, got java.lang.String", e.getMessage());
    }

    @Test
    void aListValueMustBeAList() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> SCHEMA.write(map("numbers", "nope"), packet()));

        assertEquals("Sample.numbers: expected a List, got java.lang.String", e.getMessage());
    }

    @Test
    void aStructValueMustBeAMap() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> SCHEMA.write(map("inner", 5), packet()));

        assertEquals("Sample.inner: expected a Map, got java.lang.Integer", e.getMessage());
    }

    @Test
    void aSelfReferencingSchemaReadsATree() {
        HPacket p = packet();
        p.appendString("root").appendInt(1).appendString("leaf").appendInt(0);

        Map<String, Object> values = Node.SCHEMA.read(p);

        assertEquals(map("name", "root", "children", Collections.singletonList(
                map("name", "leaf", "children", Collections.emptyList()))), values);
    }

    @Test
    void parametersExposeTheirElementsAndSchemas() {
        ListParameter numbers = (ListParameter) SCHEMA.parameters().get(0);
        ListParameter items = (ListParameter) SCHEMA.parameters().get(1);
        StructParameter inner = (StructParameter) SCHEMA.parameters().get(2);

        assertEquals(WireType.SHORT, numbers.elementType());
        assertNull(numbers.elementSchema());
        assertNull(items.elementType());
        assertSame(ITEM, items.elementSchema());
        assertSame(ITEM, inner.schema());
    }
}
