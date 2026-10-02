package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.sub.furni.CrackableStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.EmptyStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.HighScoreData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.HighScoreStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.IntArrayStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.LegacyStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.MapStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.MapStuffDataEntry;
import me.roboroads.gearth.gpackets.incoming.sub.furni.StringArrayStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.StuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.VoteResultStuffData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Every stuff data format, with and without the serial of a limited edition. */
class StuffDataWireFormatTest {

    private static HPacket packet() {
        return new HPacket("Test", HMessage.Direction.TOCLIENT);
    }

    /** Reads the expected bytes into {@code sample}'s class and writes {@code sample} back to the same bytes. */
    private static void assertWireFormat(StuffData sample, HPacket expected) {
        StuffData parsed = StuffData.fromPacket(expected);
        assertInstanceOf(sample.getClass(), parsed);
        assertEquals(sample, parsed);

        HPacket written = packet();
        sample.appendPacket(written);
        assertSameBytes(expected, written);
    }

    @Test
    void legacy() {
        HPacket p = packet();
        p.appendInt(0).appendString("1");
        assertWireFormat(LegacyStuffData.builder().typeAndFlags(0).legacyString("1").build(), p);
    }

    @Test
    void legacyWithSerial() {
        HPacket p = packet();
        p.appendInt(256).appendString("1").appendInt(12).appendInt(500);
        assertWireFormat(LegacyStuffData.builder().typeAndFlags(256).legacyString("1")
                .uniqueSerialNumber(12).uniqueSeriesSize(500).build(), p);
    }

    @Test
    void map() {
        HPacket p = packet();
        p.appendInt(1).appendInt(2).appendString("state").appendString("0").appendString("rarity").appendString("3");
        assertWireFormat(MapStuffData.builder().typeAndFlags(1).entries(Arrays.asList(
                new MapStuffDataEntry("state", "0"), new MapStuffDataEntry("rarity", "3"))).build(), p);
    }

    @Test
    void mapWithSerial() {
        HPacket p = packet();
        p.appendInt(257).appendInt(0).appendInt(3).appendInt(100);
        assertWireFormat(MapStuffData.builder().typeAndFlags(257).entries(Collections.emptyList())
                .uniqueSerialNumber(3).uniqueSeriesSize(100).build(), p);
    }

    @Test
    void stringArray() {
        HPacket p = packet();
        p.appendInt(2).appendInt(2).appendString("0").appendString("42");
        assertWireFormat(StringArrayStuffData.builder().typeAndFlags(2).values(Arrays.asList("0", "42")).build(), p);
    }

    @Test
    void voteResult() {
        HPacket p = packet();
        p.appendInt(3).appendString("1").appendInt(9);
        assertWireFormat(VoteResultStuffData.builder().typeAndFlags(3).legacyString("1").result(9).build(), p);
    }

    @Test
    void empty() {
        HPacket p = packet();
        p.appendInt(4);
        assertWireFormat(EmptyStuffData.builder().typeAndFlags(4).build(), p);
    }

    @Test
    void emptyWithSerial() {
        HPacket p = packet();
        p.appendInt(260).appendInt(1).appendInt(10);
        assertWireFormat(EmptyStuffData.builder().typeAndFlags(260).uniqueSerialNumber(1).uniqueSeriesSize(10).build(), p);
    }

    @Test
    void intArray() {
        HPacket p = packet();
        p.appendInt(5).appendInt(3).appendInt(1).appendInt(2).appendInt(3);
        assertWireFormat(IntArrayStuffData.builder().typeAndFlags(5).values(Arrays.asList(1, 2, 3)).build(), p);
    }

    static HighScoreStuffData highScore(int typeAndFlags) {
        return HighScoreStuffData.builder().typeAndFlags(typeAndFlags).legacyString("0").scoreType(1).clearType(2)
                .entries(Collections.singletonList(new HighScoreData(300, Arrays.asList("Roboroads", "Friend"))))
                .build();
    }

    static HPacket highScorePacket(int typeAndFlags) {
        HPacket p = packet();
        p.appendInt(typeAndFlags).appendString("0").appendInt(1).appendInt(2)
                .appendInt(1).appendInt(300).appendInt(2).appendString("Roboroads").appendString("Friend");
        return p;
    }

    @Test
    void highScore() {
        assertWireFormat(highScore(6), highScorePacket(6));
    }

    @Test
    void highScoreNeverHasASerial() {
        assertWireFormat(highScore(262), highScorePacket(262));
    }

    @Test
    void crackable() {
        HPacket p = packet();
        p.appendInt(7).appendString("2").appendInt(4).appendInt(10);
        assertWireFormat(CrackableStuffData.builder().typeAndFlags(7).legacyString("2").hits(4).target(10).build(), p);
    }

    @Test
    void crackableWithSerial() {
        HPacket p = packet();
        p.appendInt(263).appendString("2").appendInt(4).appendInt(10).appendInt(7).appendInt(50);
        assertWireFormat(CrackableStuffData.builder().typeAndFlags(263).legacyString("2").hits(4).target(10)
                .uniqueSerialNumber(7).uniqueSeriesSize(50).build(), p);
    }

    @Test
    void withoutTheFlagTheSerialIsNotWritten() {
        HPacket expected = packet();
        expected.appendInt(0).appendString("1");

        HPacket written = packet();
        LegacyStuffData.builder().typeAndFlags(0).legacyString("1").uniqueSerialNumber(12).uniqueSeriesSize(500).build()
                .appendPacket(written);

        assertSameBytes(expected, written);
    }

    @Test
    void aMissingTypeIsTheFormatWithoutFlags() {
        HPacket expected = packet();
        expected.appendInt(2).appendInt(1).appendString("0");

        HPacket written = packet();
        StringArrayStuffData.builder().values(Collections.singletonList("0")).build().appendPacket(written);

        assertSameBytes(expected, written);
    }

    @Test
    void anUnknownFlagFailsToParse() {
        HPacket p = packet();
        p.appendInt(512).appendString("1");

        assertThrows(IllegalArgumentException.class, () -> StuffData.fromPacket(p));
    }
}
