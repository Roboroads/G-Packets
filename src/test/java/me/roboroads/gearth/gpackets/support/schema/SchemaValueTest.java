package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import org.junit.jupiter.api.Test;
import testfixtures.openenum.OpenEnumFixtures.Shade;
import testfixtures.openenum.OpenEnumFixtures.Tone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaValueTest {

    static class Sample {
    }

    private static final Schema<Sample> SCHEMA = Schema.of(Sample.class)
            .integer("i")
            .string("s")
            .bool("b")
            .shortValue("sh")
            .longValue("l")
            .byteValue("by")
            .enumInt("dir", Direction.class)
            .enumString("sex", Gender.class);

    private static HPacket packet() {
        return new HPacket("Test", HMessage.Direction.TOCLIENT);
    }

    private static HPacket fullPacket() {
        HPacket p = packet();
        p.appendInt(7).appendString("x").appendBoolean(true).appendShort((short) 3).appendLong(9L)
                .appendByte((byte) 1).appendInt(2).appendString("F");
        return p;
    }

    private static String bytes(HPacket packet) {
        return Arrays.toString(packet.toBytes());
    }

    @Test
    void readsValuesInParameterOrder() {
        Map<String, Object> values = SCHEMA.read(fullPacket());

        assertEquals(Arrays.asList("i", "s", "b", "sh", "l", "by", "dir", "sex"), new ArrayList<>(values.keySet()));
        assertEquals(7, values.get("i"));
        assertEquals("x", values.get("s"));
        assertEquals(true, values.get("b"));
        assertEquals((short) 3, values.get("sh"));
        assertEquals(9L, values.get("l"));
        assertEquals((byte) 1, values.get("by"));
        assertEquals(2, values.get("dir"), "enums stay raw wire values");
        assertEquals("F", values.get("sex"));
    }

    @Test
    void writeThenReadReproducesTheBytes() {
        HPacket out = packet();
        SCHEMA.write(SCHEMA.read(fullPacket()), out);

        assertEquals(bytes(fullPacket()), bytes(out));
    }

    @Test
    void writeUsesDefaultsForMissingAndNullValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("s", null);
        HPacket out = packet();

        SCHEMA.write(values, out);

        HPacket expected = packet();
        expected.appendInt(0).appendString("").appendBoolean(false).appendShort((short) 0).appendLong(0L)
                .appendByte((byte) 0).appendInt(0).appendString("");
        assertEquals(bytes(expected), bytes(out));
    }

    @Test
    void writeAcceptsEnumConstantsAndNarrowsNumbers() {
        Map<String, Object> values = new HashMap<>();
        values.put("i", 7L);
        values.put("s", "x");
        values.put("b", true);
        values.put("sh", 3);
        values.put("l", 9);
        values.put("by", 1);
        values.put("dir", Direction.EAST);
        values.put("sex", Gender.FEMALE);
        HPacket out = packet();

        SCHEMA.write(values, out);

        assertEquals(bytes(fullPacket()), bytes(out));
    }

    @Test
    void writeRejectsTheWrongJavaTypeWithThePath() {
        Map<String, Object> values = new HashMap<>();
        values.put("i", "seven");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SCHEMA.write(values, packet()));

        assertEquals("Sample.i: expected INT, got java.lang.String", e.getMessage());
    }

    @Test
    void writeRejectsAnEnumConstantOfAnotherEnum() {
        Map<String, Object> values = new HashMap<>();
        values.put("dir", Gender.MALE);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SCHEMA.write(values, packet()));

        assertEquals("Sample.dir: expected INT, got " + Gender.class.getName(), e.getMessage());
    }

    @Test
    void readFailsWithThePathWhenThePacketEndsEarly() {
        Schema<Sample> two = Schema.of(Sample.class).integer("a").integer("b");
        HPacket p = packet();
        p.appendInt(7);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> two.read(p));

        assertEquals("Sample.b: packet ended before this INT could be read", e.getMessage());
    }

    @Test
    void enumParametersExposeTheirOptions() {
        ValueParameter dir = (ValueParameter) SCHEMA.parameters().get(6);

        assertEquals("dir", dir.name());
        assertEquals(WireType.INT, dir.wireType());
        assertEquals(Direction.class, dir.enumType());
        assertEquals(8, dir.enumOptions().size());
        assertEquals(0, dir.enumOptions().get("NORTH"));
        assertEquals(7, dir.enumOptions().get("NORTH_WEST"));
        assertEquals("F", ((ValueParameter) SCHEMA.parameters().get(7)).enumOptions().get("FEMALE"));
    }

    @Test
    void openEnumParametersListTheirNamedIdsByValue() {
        ValueParameter shade = (ValueParameter) Schema.of(Sample.class).enumInt("shade", Shade.class).parameters().get(0);

        assertEquals(Shade.class, shade.enumType());
        assertTrue(shade.openEnum());
        assertEquals(Arrays.asList("LIGHT", "DARK", "FADED"), new ArrayList<>(shade.enumOptions().keySet()));
        assertEquals(1, shade.enumOptions().get("LIGHT"));
        assertEquals(Collections.singletonMap("FADED", "Only old clients draw it"), shade.unusedOptions());
    }

    @Test
    void aRealEnumIsNotOpen() {
        assertFalse(((ValueParameter) SCHEMA.parameters().get(6)).openEnum());
        assertFalse(((ValueParameter) SCHEMA.parameters().get(0)).openEnum());
    }

    static class Odd implements IntEnum {
        @Override
        public int value() {
            return 1;
        }
    }

    @Test
    void enumIntRejectsAnIntEnumThatIsNeitherAnEnumNorOpen() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).enumInt("odd", Odd.class));

        assertEquals("odd: " + Odd.class.getName() + " is neither an enum nor an OpenIntEnum", e.getMessage());
    }

    @Test
    void writingAnOpenEnumWritesItsId() {
        Schema<Sample> schema = Schema.of(Sample.class).enumInt("shade", Shade.class);
        Map<String, Object> values = new HashMap<>();
        values.put("shade", Shade.of(9));
        HPacket out = packet();

        schema.write(values, out);

        assertEquals(bytes(packet().appendInt(9)), bytes(out));
    }

    @Test
    void writingTheWrongOpenEnumFailsWithThePath() {
        Schema<Sample> schema = Schema.of(Sample.class).enumInt("shade", Shade.class);
        Map<String, Object> values = new HashMap<>();
        values.put("shade", Tone.LOW);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> schema.write(values, packet()));

        assertEquals("Sample.shade: expected INT, got " + Tone.class.getName(), e.getMessage());
    }

    @Test
    void readingAnOpenEnumKeepsTheRawId() {
        Schema<Sample> schema = Schema.of(Sample.class).enumInt("shade", Shade.class);

        assertEquals(9, schema.read(packet().appendInt(9)).get("shade"));
    }

    @Test
    void dslMethodsReturnNewSchemas() {
        Schema<Sample> base = Schema.of(Sample.class).integer("a");
        Schema<Sample> extended = base.integer("b");

        assertEquals(1, base.parameters().size());
        assertEquals(2, extended.parameters().size());
        assertEquals(Sample.class, extended.type());
    }

    @Test
    void duplicateNamesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Schema.of(Sample.class).integer("a").string("a"));
    }

    @Test
    void parametersCannotBeModified() {
        assertThrows(UnsupportedOperationException.class, () -> SCHEMA.parameters().clear());
    }
}
