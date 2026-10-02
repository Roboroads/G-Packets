package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import org.junit.jupiter.api.Test;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChatWireFormatTest {

    private static Chat sample() {
        return new Chat("hello", ChatBarStyle.ROBOT, 5);
    }

    private static HPacket expectedPacket() {
        HPacket p = new HPacket("Chat", HMessage.Direction.TOSERVER);
        p.appendString("hello").appendInt(2).appendInt(5);
        return p;
    }

    @Test
    void toPacketWritesTheWireFormat() {
        assertSameBytes(expectedPacket(), sample().toPacket());
    }

    @Test
    void fromPacketReadsTheWireFormat() {
        assertEquals(sample(), Chat.fromPacket(expectedPacket()));
    }

    @Test
    void anEmptyTextIsNotSent() {
        Chat empty = new Chat("", ChatBarStyle.DEFAULT, -1);

        assertEquals("Chat breaks 1 limit (use toPacketUnchecked() to send it anyway):\n  text: must not be empty",
                assertThrows(LimitException.class, empty::toPacket).getMessage());
    }

    @Test
    void keepsAnUnlistedStyleIdThroughARoundTrip() {
        HPacket unlisted = new HPacket("Chat", HMessage.Direction.TOSERVER);
        unlisted.appendString("hello").appendInt(1028).appendInt(5);

        Chat chat = Chat.fromPacket(unlisted);

        assertFalse(chat.style().known());
        assertEquals(1028, chat.style().value());
        assertSameBytes(unlisted, chat.toPacket());
    }

    @Test
    void readsAnNftStyleAsItsConstant() {
        HPacket nft = new HPacket("Chat", HMessage.Direction.TOSERVER);
        nft.appendString("hello").appendInt(1001).appendInt(5);

        assertSame(ChatBarStyle.NFT_AVATAR_GOLD, Chat.fromPacket(nft).style());
    }

    @Test
    void namesEveryStyleInTheClientsList() {
        assertEquals(90, ChatBarStyle.values().size());
        assertEquals(ChatBarStyle.GOTHICROSE, ChatBarStyle.of(17));
        assertEquals(ChatBarStyle.RECYCLED, ChatBarStyle.of(10000));
    }
}
