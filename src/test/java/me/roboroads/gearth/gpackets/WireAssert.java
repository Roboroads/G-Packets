package me.roboroads.gearth.gpackets;

import gearth.protocol.HPacket;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Compares two packets byte for byte and prints both as arrays when they differ. */
final class WireAssert {
    private WireAssert() {
    }

    static void assertSameBytes(HPacket expected, HPacket actual) {
        assertEquals(Arrays.toString(expected.toBytes()), Arrays.toString(actual.toBytes()));
    }
}
