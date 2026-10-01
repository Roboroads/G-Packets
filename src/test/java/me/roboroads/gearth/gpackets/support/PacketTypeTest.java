package me.roboroads.gearth.gpackets.support;

import gearth.extensions.FakeExtension;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PacketTypeTest {

    @Test
    void parseRoundTripsThroughToPacket() {
        Chat chat = new Chat("hello world", ChatBarStyle.fromValue(0), 7);

        Chat parsed = Chat.TYPE.parse(chat.toPacket());

        assertEquals(chat, parsed);
    }

    @Test
    void parseRestoresReadIndex() {
        HPacket packet = new Chat("hi", ChatBarStyle.fromValue(0), -1).toPacket();
        packet.setReadIndex(11);

        Chat.TYPE.parse(packet);

        assertEquals(11, packet.getReadIndex());
    }

    @Test
    void interceptRegistersHeaderAndDirection() {
        FakeExtension ext = new FakeExtension();
        AtomicReference<Chat> seen = new AtomicReference<>();

        Chat.TYPE.intercept(ext, (chat, msg) -> seen.set(chat));

        assertEquals(1, ext.registrations.size());
        FakeExtension.Registration registration = ext.registrations.get(0);
        assertEquals("Chat", registration.header);
        assertEquals(HMessage.Direction.TOSERVER, registration.direction);

        ext.fire(new Chat("routed", ChatBarStyle.fromValue(0), 3).toPacket(), HMessage.Direction.TOSERVER);
        assertNotNull(seen.get());
        assertEquals("routed", seen.get().text());
    }

    @Test
    void headerAndDirectionComeFromTheType() {
        assertEquals("Chat", Chat.TYPE.header());
        assertSame(HMessage.Direction.TOSERVER, Chat.TYPE.direction());
    }
}
