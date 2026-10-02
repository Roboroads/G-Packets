package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WireTypeTest {

    @Test
    void defaultsAreZeroEmptyOrFalse() {
        assertEquals(0, WireType.INT.defaultValue());
        assertEquals("", WireType.STRING.defaultValue());
        assertEquals(false, WireType.BOOLEAN.defaultValue());
        assertEquals((short) 0, WireType.SHORT.defaultValue());
        assertEquals(0L, WireType.LONG.defaultValue());
        assertEquals((byte) 0, WireType.BYTE.defaultValue());
        assertEquals(0f, WireType.FLOAT.defaultValue());
    }

    @Test
    void coerceTurnsNullIntoTheDefault() {
        assertEquals("", WireType.STRING.coerce(null));
    }

    @Test
    void coerceNarrowsAnyNumber() {
        assertEquals((short) 7, WireType.SHORT.coerce(7));
        assertEquals(7, WireType.INT.coerce(7L));
        assertEquals(7L, WireType.LONG.coerce(7));
        assertEquals((byte) 7, WireType.BYTE.coerce(7));
        assertEquals(7.5f, WireType.FLOAT.coerce(7.5d));
    }

    @Test
    void coerceRejectsTheWrongJavaType() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> WireType.INT.coerce("7"));
        assertEquals("expected INT, got java.lang.String", e.getMessage());
        assertThrows(IllegalArgumentException.class, () -> WireType.STRING.coerce(7));
        assertThrows(IllegalArgumentException.class, () -> WireType.BOOLEAN.coerce(1));
    }

    @Test
    void writeThenReadRoundTripsEveryType() {
        HPacket packet = new HPacket("Test", HMessage.Direction.TOCLIENT);
        WireType.INT.write(packet, 7);
        WireType.STRING.write(packet, "hi");
        WireType.BOOLEAN.write(packet, true);
        WireType.SHORT.write(packet, (short) 3);
        WireType.LONG.write(packet, 9L);
        WireType.BYTE.write(packet, (byte) 1);
        WireType.FLOAT.write(packet, 2.5f);
        packet.resetReadIndex();

        assertEquals(7, WireType.INT.read(packet));
        assertEquals("hi", WireType.STRING.read(packet));
        assertEquals(true, WireType.BOOLEAN.read(packet));
        assertEquals((short) 3, WireType.SHORT.read(packet));
        assertEquals(9L, WireType.LONG.read(packet));
        assertEquals((byte) 1, WireType.BYTE.read(packet));
        assertEquals(2.5f, WireType.FLOAT.read(packet));
    }
}
