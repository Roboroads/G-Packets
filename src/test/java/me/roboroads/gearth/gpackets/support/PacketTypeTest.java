package me.roboroads.gearth.gpackets.support;

import gearth.extensions.FakeExtension;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PacketTypeTest {

    @Test
    void schemaListsTheParametersInWireOrder() {
        List<String> names = new ArrayList<>();
        for (Parameter parameter : Chat.TYPE.schema().parameters()) {
            names.add(parameter.name());
        }

        assertEquals(Arrays.asList("text", "style", "trackingId"), names);
    }

    @Test
    void readReturnsNamedValuesAndRestoresTheReadIndex() {
        HPacket packet = new Chat("hi", ChatBarStyle.ROBOT, 7).toPacket();
        packet.setReadIndex(11);

        Map<String, Object> values = Chat.TYPE.read(packet);

        assertEquals("hi", values.get("text"));
        assertEquals(2, values.get("style"));
        assertEquals(7, values.get("trackingId"));
        assertEquals(11, packet.getReadIndex());
    }

    @Test
    void readThenParseOnTheSamePacketBothSeeTheWholeBody() {
        HPacket packet = new Chat("hi", ChatBarStyle.ROBOT, 7).toPacket();

        Chat.TYPE.read(packet);

        assertEquals(new Chat("hi", ChatBarStyle.ROBOT, 7), Chat.TYPE.parse(packet));
    }

    @Test
    void writeBuildsAPacketFromValues() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("text", "built");
        values.put("style", ChatBarStyle.ROBOT);

        HPacket packet = Chat.TYPE.write(values);

        assertEquals(new Chat("built", ChatBarStyle.ROBOT, 0), Chat.TYPE.parse(packet));
    }

    @Test
    void writeTakesAStyleTheLibraryDoesNotName() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("text", "built");
        values.put("style", ChatBarStyle.of(1028));

        HPacket packet = Chat.TYPE.write(values);

        assertEquals(ChatBarStyle.of(1028), Chat.TYPE.parse(packet).style());
    }

    @Test
    void readThenWriteReproducesTheBytesIncludingUnknownEnumValues() {
        HPacket original = new HPacket("Chat", HMessage.Direction.TOSERVER);
        original.appendString("hi").appendInt(999).appendInt(7);

        HPacket copy = Chat.TYPE.write(Chat.TYPE.read(original));

        assertEquals(Arrays.toString(original.toBytes()), Arrays.toString(copy.toBytes()));
    }

    @Test
    void parseRoundTripsThroughToPacket() {
        Chat chat = new Chat("hello world", ChatBarStyle.of(0), 7);

        Chat parsed = Chat.TYPE.parse(chat.toPacket());

        assertEquals(chat, parsed);
    }

    @Test
    void parseRestoresReadIndex() {
        HPacket packet = new Chat("hi", ChatBarStyle.of(0), -1).toPacket();
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

        ext.fire(new Chat("routed", ChatBarStyle.of(0), 3).toPacket(), HMessage.Direction.TOSERVER);
        assertNotNull(seen.get());
        assertEquals("routed", seen.get().text());
    }

    @Test
    void headerAndDirectionComeFromTheType() {
        assertEquals("Chat", Chat.TYPE.header());
        assertSame(HMessage.Direction.TOSERVER, Chat.TYPE.direction());
    }
}
