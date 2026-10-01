package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SchemaOptionalTest {

    static class Sample {
    }

    private static final Schema<Sample> SCHEMA = Schema.of(Sample.class)
            .bool("flag")
            .optional(s -> s.string("hash").list("extra", WireType.INT));

    private static HPacket packet() {
        return new HPacket("Test", HMessage.Direction.TOCLIENT);
    }

    private static String bytes(HPacket packet) {
        return Arrays.toString(packet.toBytes());
    }

    @Test
    void readsTheTailWhenBytesRemain() {
        HPacket p = packet();
        p.appendBoolean(true).appendString("abc").appendInt(1).appendInt(5);

        Map<String, Object> values = SCHEMA.read(p);

        assertEquals("abc", values.get("hash"));
        assertEquals(Collections.singletonList(5), values.get("extra"));
    }

    @Test
    void leavesTheTailOutAtTheEndOfThePacket() {
        HPacket p = packet();
        p.appendBoolean(true);

        assertEquals(Collections.singletonList("flag"), new ArrayList<>(SCHEMA.read(p).keySet()));
    }

    @Test
    void writesNothingWhenEveryTailValueIsUnset() {
        Map<String, Object> values = new HashMap<>();
        values.put("flag", true);
        HPacket out = packet();

        SCHEMA.write(values, out);

        HPacket expected = packet();
        expected.appendBoolean(true);
        assertEquals(bytes(expected), bytes(out));
    }

    @Test
    void writesTheWholeTailWhenAnyValueIsSet() {
        Map<String, Object> values = new HashMap<>();
        values.put("flag", false);
        values.put("extra", Collections.emptyList());
        HPacket out = packet();

        SCHEMA.write(values, out);

        HPacket expected = packet();
        expected.appendBoolean(false).appendString("").appendInt(0);
        assertEquals(bytes(expected), bytes(out));
    }

    @Test
    void optionalsExposeTheirSchema() {
        OptionalParameter optional = (OptionalParameter) SCHEMA.parameters().get(1);

        assertNull(optional.name());
        assertEquals("hash", optional.schema().parameters().get(0).name());
        assertEquals("extra", optional.schema().parameters().get(1).name());
    }
}
