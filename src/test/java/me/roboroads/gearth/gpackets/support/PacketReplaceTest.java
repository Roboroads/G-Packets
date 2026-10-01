package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import org.junit.jupiter.api.Test;

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
