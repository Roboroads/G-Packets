package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketReplaceTest {

    private static HMessage messageWithHeaderId(int headerId, HMessage.Direction direction) {
        // Original body belongs to some other packet shape; replaceIn should overwrite it.
        HPacket original = new HPacket(headerId, new byte[]{0, 0, 0, 5});
        return new HMessage(original, direction, 0);
    }

    private static HMessage messageWithBodyOf(HPacket packet, HMessage.Direction direction) {
        return new HMessage(new HPacket(1234, body(packet)), direction, 0);
    }

    private static byte[] body(HPacket packet) {
        byte[] bytes = packet.toBytes();
        return Arrays.copyOfRange(bytes, 6, bytes.length);
    }

    @Test
    void typeReplaceInKeepsBytesTheSchemaDoesNotKnow() {
        HMessage message = messageWithBodyOf(new Chat("original", ChatBarStyle.of(0), 9).toPacket().appendInt(42), HMessage.Direction.TOSERVER);
        Map<String, Object> values = Chat.TYPE.read(message.getPacket());
        values.put("text", "edited");

        Chat.TYPE.replaceIn(message, values);

        assertArrayEquals(body(new Chat("edited", ChatBarStyle.of(0), 9).toPacket().appendInt(42)), body(message.getPacket()));
    }

    @Test
    void replaceInKeepsBytesTheSchemaDoesNotKnow() {
        HMessage message = messageWithBodyOf(new Chat("original", ChatBarStyle.of(0), 9).toPacket().appendInt(42), HMessage.Direction.TOSERVER);

        new Chat("edited", ChatBarStyle.of(0), 9).replaceIn(message);

        assertArrayEquals(body(new Chat("edited", ChatBarStyle.of(0), 9).toPacket().appendInt(42)), body(message.getPacket()));
    }

    @Test
    void replaceInKeepsTheBytesAfterAnOptionalPart() {
        HMessage message = messageWithBodyOf(new CatalogPublished(true, "abc").toPacket().appendInt(42), HMessage.Direction.TOCLIENT);
        Map<String, Object> values = CatalogPublished.TYPE.read(message.getPacket());
        values.put("newFurniDataHash", "def");

        CatalogPublished.TYPE.replaceIn(message, values);

        assertArrayEquals(body(new CatalogPublished(true, "def").toPacket().appendInt(42)), body(message.getPacket()));
    }

    @Test
    void replaceInDropsTheTrailingBytesWhenTheOptionalPartBeforeThemIsLeftOut() {
        HMessage message = messageWithBodyOf(new CatalogPublished(true, "abc").toPacket().appendInt(42), HMessage.Direction.TOCLIENT);
        Map<String, Object> values = CatalogPublished.TYPE.read(message.getPacket());
        values.remove("newFurniDataHash");

        CatalogPublished.TYPE.replaceIn(message, values);

        assertArrayEquals(new byte[]{1}, body(message.getPacket()));
    }

    @Test
    void replaceInKeepsNothingWhenTheOriginalBodyDoesNotParse() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);

        new Chat("edited", ChatBarStyle.of(0), 9).replaceIn(message);

        assertArrayEquals(body(new Chat("edited", ChatBarStyle.of(0), 9).toPacket()), body(message.getPacket()));
    }

    @Test
    void typeReplaceInWritesEditedValuesAndKeepsTheHeaderId() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);
        Map<String, Object> values = Chat.TYPE.read(new Chat("original", ChatBarStyle.of(0), 9).toPacket());
        values.put("text", "edited");

        Chat.TYPE.replaceIn(message, values);

        assertEquals(1234, message.getPacket().headerId());
        assertEquals(new Chat("edited", ChatBarStyle.of(0), 9), Chat.TYPE.parse(message.getPacket()));
    }

    @Test
    void typeReplaceInRejectsDirectionMismatch() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOCLIENT);

        assertThrows(IllegalArgumentException.class, () -> Chat.TYPE.replaceIn(message, new HashMap<>()));
    }

    @Test
    void replaceInKeepsTheOriginalHeaderId() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);

        new Chat("edited", ChatBarStyle.of(0), 9).replaceIn(message);

        assertEquals(1234, message.getPacket().headerId());
    }

    @Test
    void replaceInSwapsTheBody() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);
        Chat replacement = new Chat("edited", ChatBarStyle.of(0), 9);

        replacement.replaceIn(message);

        assertEquals(replacement, Chat.TYPE.parse(message.getPacket()));
    }

    @Test
    void replaceInMarksThePacketEdited() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);

        new Chat("edited", ChatBarStyle.of(0), 9).replaceIn(message);

        assertTrue(message.getPacket().isReplaced());
    }

    @Test
    void replaceInRejectsDirectionMismatch() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOCLIENT);

        assertThrows(IllegalArgumentException.class,
                () -> new Chat("edited", ChatBarStyle.of(0), 9).replaceIn(message));
    }
}
