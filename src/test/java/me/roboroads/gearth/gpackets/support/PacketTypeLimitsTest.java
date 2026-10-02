package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.CatalogType;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPage;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import org.junit.jupiter.api.Test;
import testfixtures.limits.LimitFixtures.Incoming;
import testfixtures.limits.LimitFixtures.Item;
import testfixtures.limits.LimitFixtures.Outgoing;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketTypeLimitsTest {

    private static Outgoing valid() {
        return Outgoing.builder().name("ok").tags(Collections.singletonList("ab"))
                .sleepEnabled(true).sleep(600).kickEnabled(true).kick(900)
                .items(Collections.singletonList(new Item("abc"))).build();
    }

    private static Outgoing breaking() {
        return valid().name("toolong").tags(Arrays.asList("a", "b", "c"));
    }

    private static final String BREAKING_MESSAGE = "LimitedOut breaks 2 limits (use toPacketUnchecked() to send it anyway):\n"
            + "  name: at most 5 characters, got 7\n"
            + "  tags: at most 2 items, got 3";

    @Test
    void aValidOutgoingPacketWrites() {
        assertEquals(valid(), Outgoing.TYPE.parse(valid().toPacket()));
    }

    @Test
    void anOutgoingPacketThatBreaksLimitsThrowsWithEveryViolation() {
        LimitException e = assertThrows(LimitException.class, () -> breaking().toPacket());

        assertEquals(BREAKING_MESSAGE, e.getMessage());
        assertEquals(2, e.violations().size());
        assertEquals("tags", e.violations().get(1).path());
    }

    @Test
    void uncheckedWritesItAnyway() {
        assertEquals("toolong", Outgoing.TYPE.parse(breaking().toPacketUnchecked()).name());
        assertEquals("toolong", Outgoing.TYPE.parse(Outgoing.TYPE.toPacketUnchecked(breaking())).name());
    }

    @Test
    void writingValuesIsCheckedToo() {
        Map<String, Object> values = Outgoing.TYPE.read(breaking().toPacketUnchecked());

        assertEquals(BREAKING_MESSAGE.replace("toPacketUnchecked()", "writeUnchecked()"),
                assertThrows(LimitException.class, () -> Outgoing.TYPE.write(values)).getMessage());
        assertEquals("toolong", Outgoing.TYPE.parse(Outgoing.TYPE.writeUnchecked(values)).name());
    }

    @Test
    void replacingIsCheckedToo() {
        HMessage message = new HMessage(valid().toPacket(), HMessage.Direction.TOSERVER, 0);
        Map<String, Object> values = Outgoing.TYPE.read(breaking().toPacketUnchecked());

        assertEquals(BREAKING_MESSAGE.replace("toPacketUnchecked()", "replaceInUnchecked()"),
                assertThrows(LimitException.class, () -> Outgoing.TYPE.replaceIn(message, values)).getMessage());
        assertEquals("ok", Outgoing.TYPE.parse(message.getPacket()).name());

        Outgoing.TYPE.replaceInUnchecked(message, values);
        assertEquals("toolong", Outgoing.TYPE.parse(message.getPacket()).name());
    }

    @Test
    void replacingWithAPacketIsCheckedAndCanSkipTheCheck() {
        HMessage message = new HMessage(valid().toPacket(), HMessage.Direction.TOSERVER, 0);

        assertEquals(BREAKING_MESSAGE.replace("toPacketUnchecked()", "replaceInUnchecked()"),
                assertThrows(LimitException.class, () -> breaking().replaceIn(message)).getMessage());
        assertEquals("ok", Outgoing.TYPE.parse(message.getPacket()).name());

        breaking().replaceInUnchecked(message);
        assertEquals("toolong", Outgoing.TYPE.parse(message.getPacket()).name());
    }

    @Test
    void aWrongDirectionFailsBeforeTheLimitCheck() {
        HMessage toClient = new HMessage(valid().toPacket(), HMessage.Direction.TOCLIENT, 0);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> breaking().replaceIn(toClient));

        assertFalse(e instanceof LimitException, e.getMessage());
    }

    @Test
    void anIncomingPacketIsNeverChecked() {
        Incoming incoming = Incoming.builder().name("toolong").tags(Arrays.asList("a", "b", "c"))
                .sleepEnabled(false).sleep(1).kickEnabled(false).kick(1).build();

        HPacket packet = incoming.toPacket();

        assertEquals("toolong", Incoming.TYPE.parse(packet).name());
        assertEquals("toolong", Incoming.TYPE.parse(Incoming.TYPE.write(Incoming.TYPE.read(packet))).name());
        assertEquals(4, Incoming.TYPE.violations(incoming).size());
    }

    @Test
    void violationsReportsWithoutThrowing() {
        assertTrue(Outgoing.TYPE.violations(valid()).isEmpty());
        assertEquals(2, Outgoing.TYPE.violations(breaking()).size());
        assertEquals(2, Outgoing.TYPE.violations(Outgoing.TYPE.read(breaking().toPacketUnchecked())).size());
    }

    @Test
    void aRuleIsChecked() {
        Outgoing tooSoon = valid().kick(620);

        LimitException e = assertThrows(LimitException.class, tooSoon::toPacket);

        assertEquals("LimitedOut breaks 1 limit (use toPacketUnchecked() to send it anyway):\n"
                + "  Outgoing: kick is at least sleep + 30 when both are enabled", e.getMessage());
        assertTrue(Outgoing.TYPE.violations(valid().kickEnabled(false).kick(0)).isEmpty());
    }

    @Test
    void aNestedLimitReportsItsPath() {
        Outgoing nested = valid().items(Arrays.asList(new Item("abc"), new Item("abcd")));

        assertEquals("items[1].label", assertThrows(LimitException.class, nested::toPacket).violations().get(0).path());
    }

    @Test
    void aPacketWithoutLimitsIsNotChecked() {
        assertFalse(GetCatalogPage.TYPE.schema().hasChecks());
        assertArrayEquals(GetCatalogPage.TYPE.toPacketUnchecked(new GetCatalogPage(1, -1, CatalogType.NORMAL)).toBytes(),
                new GetCatalogPage(1, -1, CatalogType.NORMAL).toPacket().toBytes());
    }
}
