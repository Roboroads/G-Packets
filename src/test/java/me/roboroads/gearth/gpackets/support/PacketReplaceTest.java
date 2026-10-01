package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketReplaceTest {

    private static HMessage messageWithHeaderId(int headerId, HMessage.Direction direction) {
        // Original body belongs to some other packet shape; replaceIn should overwrite it.
        HPacket original = new HPacket(headerId, new byte[]{0, 0, 0, 5});
        return new HMessage(original, direction, 0);
    }

    @Test
    void typeReplaceInWritesEditedValuesAndKeepsTheHeaderId() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);
        Map<String, Object> values = Chat.TYPE.read(new Chat("original", ChatBarStyle.fromValue(0), 9).toPacket());
        values.put("text", "edited");

        Chat.TYPE.replaceIn(message, values);

        assertEquals(1234, message.getPacket().headerId());
        assertEquals(new Chat("edited", ChatBarStyle.fromValue(0), 9), Chat.TYPE.parse(message.getPacket()));
    }

    @Test
    void typeReplaceInRejectsDirectionMismatch() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOCLIENT);

        assertThrows(IllegalArgumentException.class, () -> Chat.TYPE.replaceIn(message, new HashMap<>()));
    }

    @Test
    void replaceInKeepsTheOriginalHeaderId() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);

        new Chat("edited", ChatBarStyle.fromValue(0), 9).replaceIn(message);

        assertEquals(1234, message.getPacket().headerId());
    }

    @Test
    void replaceInSwapsTheBody() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);
        Chat replacement = new Chat("edited", ChatBarStyle.fromValue(0), 9);

        replacement.replaceIn(message);

        assertEquals(replacement, Chat.TYPE.parse(message.getPacket()));
    }

    @Test
    void replaceInMarksThePacketEdited() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOSERVER);

        new Chat("edited", ChatBarStyle.fromValue(0), 9).replaceIn(message);

        assertTrue(message.getPacket().isReplaced());
    }

    @Test
    void replaceInRejectsDirectionMismatch() {
        HMessage message = messageWithHeaderId(1234, HMessage.Direction.TOCLIENT);

        assertThrows(IllegalArgumentException.class,
                () -> new Chat("edited", ChatBarStyle.fromValue(0), 9).replaceIn(message));
    }
}
