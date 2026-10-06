package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.FloodControl;
import me.roboroads.gearth.gpackets.incoming.GetCustomFilterResult;
import me.roboroads.gearth.gpackets.incoming.ModifyCustomFilterResult;
import me.roboroads.gearth.gpackets.incoming.RemainingMutePeriod;
import me.roboroads.gearth.gpackets.incoming.RoomChatSettings;
import me.roboroads.gearth.gpackets.incoming.SpecialSystemChat;
import me.roboroads.gearth.gpackets.incoming.UserNftChatStyles;
import me.roboroads.gearth.gpackets.incoming.UserPurchasableChatStyleChanged;
import me.roboroads.gearth.gpackets.incoming.UserPurchasableChatStyles;
import me.roboroads.gearth.gpackets.incoming.UserTyping;
import me.roboroads.gearth.gpackets.incoming.sub.chat.ChatLink;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.ChatBubbleWidth;
import me.roboroads.gearth.gpackets.model.enums.ChatFloodSensitivity;
import me.roboroads.gearth.gpackets.model.enums.ChatFontSize;
import me.roboroads.gearth.gpackets.model.enums.CustomFilterResult;
import me.roboroads.gearth.gpackets.model.enums.Gesture;
import me.roboroads.gearth.gpackets.outgoing.AddToCustomFilter;
import me.roboroads.gearth.gpackets.outgoing.CancelTyping;
import me.roboroads.gearth.gpackets.outgoing.GetCustomFilter;
import me.roboroads.gearth.gpackets.outgoing.GetUserNftChatStyles;
import me.roboroads.gearth.gpackets.outgoing.RemoveFromCustomFilter;
import me.roboroads.gearth.gpackets.outgoing.SetChatStylePreference;
import me.roboroads.gearth.gpackets.outgoing.StartTyping;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Chat, Shout and Whisper exist in both directions: the incoming classes are written out in full
// and the outgoing Chat has its own ChatWireFormatTest.
@SuppressWarnings("deprecation") // tests the unused ChatLink and receiverRoomIndex parameters
class RoomChatWireFormatTest {

    // ---- shared samples ----

    static List<ChatLink> links() {
        return Collections.singletonList(new ChatLink("example.com", "https://example.com", true));
    }

    // userIndex 3, "hi {0}", gesture smile, style robot, one link, tracking id 7.
    static void appendChatStart(HPacket p) {
        p.appendInt(3).appendString("hi {0}").appendInt(1).appendInt(2)
                .appendInt(1).appendString("example.com").appendString("https://example.com").appendBoolean(true)
                .appendInt(7);
    }

    static HPacket incomingChatPacket(String header) {
        HPacket p = new HPacket(header, HMessage.Direction.TOCLIENT);
        appendChatStart(p);
        p.appendInt(5).appendInt(2);
        return p;
    }

    // ---- Chat (incoming) ----

    static me.roboroads.gearth.gpackets.incoming.Chat incomingChat() {
        return new me.roboroads.gearth.gpackets.incoming.Chat(3, "hi {0}", Gesture.SMILE, ChatBarStyle.ROBOT, links(), 7,
                5, ChatBubbleWidth.THIN);
    }

    @Test
    void incomingChatToPacket() {
        assertSameBytes(incomingChatPacket("Chat"), incomingChat().toPacket());
    }

    @Test
    void incomingChatFromPacket() {
        assertEquals(incomingChat(), me.roboroads.gearth.gpackets.incoming.Chat.fromPacket(incomingChatPacket("Chat")));
    }

    @Test
    void incomingChatWithoutTheOptionalTail() {
        HPacket p = new HPacket("Chat", HMessage.Direction.TOCLIENT);
        appendChatStart(p);

        me.roboroads.gearth.gpackets.incoming.Chat chat = me.roboroads.gearth.gpackets.incoming.Chat.fromPacket(p);

        assertNull(chat.receiverRoomIndex());
        assertNull(chat.chatBubbleWidthOverride());
        assertSameBytes(p, chat.toPacket());
    }

    @Test
    void incomingChatWithOnlyTheReceiverRoomIndex() {
        HPacket p = new HPacket("Chat", HMessage.Direction.TOCLIENT);
        appendChatStart(p);
        p.appendInt(5);

        me.roboroads.gearth.gpackets.incoming.Chat chat = me.roboroads.gearth.gpackets.incoming.Chat.fromPacket(p);

        assertEquals(5, chat.receiverRoomIndex());
        assertNull(chat.chatBubbleWidthOverride());
        assertSameBytes(p, chat.toPacket());
    }

    @Test
    void incomingChatKeepsNoWidthOverride() {
        HPacket p = new HPacket("Chat", HMessage.Direction.TOCLIENT);
        appendChatStart(p);
        p.appendInt(-1).appendInt(-1);

        me.roboroads.gearth.gpackets.incoming.Chat chat = me.roboroads.gearth.gpackets.incoming.Chat.fromPacket(p);

        assertFalse(chat.chatBubbleWidthOverride().known());
        assertEquals(-1, chat.chatBubbleWidthOverride().value());
        assertSameBytes(p, chat.toPacket());
    }

    @Test
    void incomingChatWithoutLinks() {
        HPacket p = new HPacket("Chat", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendString("hello").appendInt(0).appendInt(1028).appendInt(0).appendInt(-1);

        me.roboroads.gearth.gpackets.incoming.Chat chat = me.roboroads.gearth.gpackets.incoming.Chat.fromPacket(p);

        assertEquals(Collections.emptyList(), chat.links());
        assertSame(Gesture.NONE, chat.gesture());
        assertEquals(1028, chat.style().value());
        assertSameBytes(p, chat.toPacket());
    }

    // ---- Shout (incoming) ----

    static me.roboroads.gearth.gpackets.incoming.Shout incomingShout() {
        return new me.roboroads.gearth.gpackets.incoming.Shout(3, "hi {0}", Gesture.SMILE, ChatBarStyle.ROBOT, links(), 7,
                5, ChatBubbleWidth.THIN);
    }

    @Test
    void incomingShoutToPacket() {
        assertSameBytes(incomingChatPacket("Shout"), incomingShout().toPacket());
    }

    @Test
    void incomingShoutFromPacket() {
        assertEquals(incomingShout(), me.roboroads.gearth.gpackets.incoming.Shout.fromPacket(incomingChatPacket("Shout")));
    }

    // ---- Whisper (incoming) ----

    static me.roboroads.gearth.gpackets.incoming.Whisper incomingWhisper() {
        return new me.roboroads.gearth.gpackets.incoming.Whisper(3, "hi {0}", Gesture.SMILE, ChatBarStyle.ROBOT, links(), 7,
                5, ChatBubbleWidth.THIN);
    }

    @Test
    void incomingWhisperToPacket() {
        assertSameBytes(incomingChatPacket("Whisper"), incomingWhisper().toPacket());
    }

    @Test
    void incomingWhisperFromPacket() {
        assertEquals(incomingWhisper(), me.roboroads.gearth.gpackets.incoming.Whisper.fromPacket(incomingChatPacket("Whisper")));
    }

    // ---- FloodControl ----

    static HPacket floodControlPacket() {
        HPacket p = new HPacket("FloodControl", HMessage.Direction.TOCLIENT);
        p.appendInt(30);
        return p;
    }

    @Test
    void floodControlToPacket() {
        assertSameBytes(floodControlPacket(), new FloodControl(30).toPacket());
    }

    @Test
    void floodControlFromPacket() {
        assertEquals(new FloodControl(30), FloodControl.fromPacket(floodControlPacket()));
    }

    // ---- GetCustomFilterResult ----

    static HPacket getCustomFilterResultPacket() {
        HPacket p = new HPacket("GetCustomFilterResult", HMessage.Direction.TOCLIENT);
        p.appendInt(2).appendString("bobba").appendString("spam");
        return p;
    }

    @Test
    void getCustomFilterResultToPacket() {
        assertSameBytes(getCustomFilterResultPacket(), new GetCustomFilterResult(Arrays.asList("bobba", "spam")).toPacket());
    }

    @Test
    void getCustomFilterResultFromPacket() {
        assertEquals(new GetCustomFilterResult(Arrays.asList("bobba", "spam")),
                GetCustomFilterResult.fromPacket(getCustomFilterResultPacket()));
    }

    // ---- ModifyCustomFilterResult ----

    static HPacket modifyCustomFilterResultPacket() {
        HPacket p = new HPacket("ModifyCustomFilterResult", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendString("spam");
        return p;
    }

    @Test
    void modifyCustomFilterResultToPacket() {
        assertSameBytes(modifyCustomFilterResultPacket(), new ModifyCustomFilterResult(CustomFilterResult.REMOVED, "spam").toPacket());
    }

    @Test
    void modifyCustomFilterResultFromPacket() {
        assertEquals(new ModifyCustomFilterResult(CustomFilterResult.REMOVED, "spam"),
                ModifyCustomFilterResult.fromPacket(modifyCustomFilterResultPacket()));
    }

    @Test
    void modifyCustomFilterResultKeepsAnUnnamedResult() {
        HPacket p = new HPacket("ModifyCustomFilterResult", HMessage.Direction.TOCLIENT);
        p.appendInt(0).appendString("spam");

        ModifyCustomFilterResult result = ModifyCustomFilterResult.fromPacket(p);

        assertFalse(result.result().known());
        assertSameBytes(p, result.toPacket());
    }

    // ---- RemainingMutePeriod ----

    static HPacket remainingMutePeriodPacket() {
        HPacket p = new HPacket("RemainingMutePeriod", HMessage.Direction.TOCLIENT);
        p.appendInt(3600);
        return p;
    }

    @Test
    void remainingMutePeriodToPacket() {
        assertSameBytes(remainingMutePeriodPacket(), new RemainingMutePeriod(3600).toPacket());
    }

    @Test
    void remainingMutePeriodFromPacket() {
        assertEquals(new RemainingMutePeriod(3600), RemainingMutePeriod.fromPacket(remainingMutePeriodPacket()));
    }

    // ---- RoomChatSettings ----

    static HPacket roomChatSettingsPacket() {
        HPacket p = new HPacket("RoomChatSettings", HMessage.Direction.TOCLIENT);
        p.appendInt(2);
        return p;
    }

    @Test
    void roomChatSettingsToPacket() {
        assertSameBytes(roomChatSettingsPacket(), new RoomChatSettings(ChatFloodSensitivity.LOOSE).toPacket());
    }

    @Test
    void roomChatSettingsFromPacket() {
        assertEquals(new RoomChatSettings(ChatFloodSensitivity.LOOSE), RoomChatSettings.fromPacket(roomChatSettingsPacket()));
    }

    // ---- SpecialSystemChat ----

    static HPacket specialSystemChatPacket() {
        HPacket p = new HPacket("SpecialSystemChat", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(67);
        return p;
    }

    @Test
    void specialSystemChatToPacket() {
        assertSameBytes(specialSystemChatPacket(), new SpecialSystemChat(3, 67).toPacket());
    }

    @Test
    void specialSystemChatFromPacket() {
        assertEquals(new SpecialSystemChat(3, 67), SpecialSystemChat.fromPacket(specialSystemChatPacket()));
    }

    // ---- UserNftChatStyles ----

    static HPacket userNftChatStylesPacket() {
        HPacket p = new HPacket("UserNftChatStyles", HMessage.Direction.TOCLIENT);
        p.appendInt(2).appendInt(1001).appendInt(1027);
        return p;
    }

    @Test
    void userNftChatStylesToPacket() {
        assertSameBytes(userNftChatStylesPacket(), new UserNftChatStyles(Arrays.asList(1001, 1027)).toPacket());
    }

    @Test
    void userNftChatStylesFromPacket() {
        assertEquals(new UserNftChatStyles(Arrays.asList(1001, 1027)), UserNftChatStyles.fromPacket(userNftChatStylesPacket()));
    }

    // ---- UserPurchasableChatStyleChanged ----

    static HPacket userPurchasableChatStyleChangedPacket() {
        HPacket p = new HPacket("UserPurchasableChatStyleChanged", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true).appendInt(10000);
        return p;
    }

    @Test
    void userPurchasableChatStyleChangedToPacket() {
        assertSameBytes(userPurchasableChatStyleChangedPacket(),
                new UserPurchasableChatStyleChanged(true, ChatBarStyle.RECYCLED).toPacket());
    }

    @Test
    void userPurchasableChatStyleChangedFromPacket() {
        assertEquals(new UserPurchasableChatStyleChanged(true, ChatBarStyle.RECYCLED),
                UserPurchasableChatStyleChanged.fromPacket(userPurchasableChatStyleChangedPacket()));
    }

    // ---- UserPurchasableChatStyles ----

    static HPacket userPurchasableChatStylesPacket() {
        HPacket p = new HPacket("UserPurchasableChatStyles", HMessage.Direction.TOCLIENT);
        p.appendInt(1).appendInt(10000);
        return p;
    }

    @Test
    void userPurchasableChatStylesToPacket() {
        assertSameBytes(userPurchasableChatStylesPacket(), new UserPurchasableChatStyles(Collections.singletonList(10000)).toPacket());
    }

    @Test
    void userPurchasableChatStylesFromPacket() {
        assertEquals(new UserPurchasableChatStyles(Collections.singletonList(10000)),
                UserPurchasableChatStyles.fromPacket(userPurchasableChatStylesPacket()));
    }

    // ---- UserTyping ----

    static HPacket userTypingPacket() {
        HPacket p = new HPacket("UserTyping", HMessage.Direction.TOCLIENT);
        p.appendInt(3).appendInt(1);
        return p;
    }

    @Test
    void userTypingToPacket() {
        assertSameBytes(userTypingPacket(), new UserTyping(3, 1).toPacket());
    }

    @Test
    void userTypingFromPacket() {
        assertEquals(new UserTyping(3, 1), UserTyping.fromPacket(userTypingPacket()));
    }

    // ---- AddToCustomFilter ----

    static HPacket addToCustomFilterPacket() {
        HPacket p = new HPacket("AddToCustomFilter", HMessage.Direction.TOSERVER);
        p.appendString("spam");
        return p;
    }

    @Test
    void addToCustomFilterToPacket() {
        assertSameBytes(addToCustomFilterPacket(), new AddToCustomFilter("spam").toPacket());
    }

    @Test
    void addToCustomFilterFromPacket() {
        assertEquals(new AddToCustomFilter("spam"), AddToCustomFilter.fromPacket(addToCustomFilterPacket()));
    }

    @Test
    void anEmptyWordIsNotAdded() {
        assertEquals("AddToCustomFilter breaks 1 limit (use toPacketUnchecked() to send it anyway):\n  word: must not be empty",
                assertThrows(LimitException.class, new AddToCustomFilter("")::toPacket).getMessage());
    }

    // ---- CancelTyping ----

    static HPacket cancelTypingPacket() {
        return new HPacket("CancelTyping", HMessage.Direction.TOSERVER);
    }

    @Test
    void cancelTypingToPacket() {
        assertSameBytes(cancelTypingPacket(), new CancelTyping().toPacket());
    }

    @Test
    void cancelTypingFromPacket() {
        assertEquals(new CancelTyping(), CancelTyping.fromPacket(cancelTypingPacket()));
    }

    // ---- GetCustomFilter ----

    static HPacket getCustomFilterPacket() {
        return new HPacket("GetCustomFilter", HMessage.Direction.TOSERVER);
    }

    @Test
    void getCustomFilterToPacket() {
        assertSameBytes(getCustomFilterPacket(), new GetCustomFilter().toPacket());
    }

    @Test
    void getCustomFilterFromPacket() {
        assertEquals(new GetCustomFilter(), GetCustomFilter.fromPacket(getCustomFilterPacket()));
    }

    // ---- GetUserNftChatStyles ----

    static HPacket getUserNftChatStylesPacket() {
        return new HPacket("GetUserNftChatStyles", HMessage.Direction.TOSERVER);
    }

    @Test
    void getUserNftChatStylesToPacket() {
        assertSameBytes(getUserNftChatStylesPacket(), new GetUserNftChatStyles().toPacket());
    }

    @Test
    void getUserNftChatStylesFromPacket() {
        assertEquals(new GetUserNftChatStyles(), GetUserNftChatStyles.fromPacket(getUserNftChatStylesPacket()));
    }

    // ---- RemoveFromCustomFilter ----

    static HPacket removeFromCustomFilterPacket() {
        HPacket p = new HPacket("RemoveFromCustomFilter", HMessage.Direction.TOSERVER);
        p.appendString("spam");
        return p;
    }

    @Test
    void removeFromCustomFilterToPacket() {
        assertSameBytes(removeFromCustomFilterPacket(), new RemoveFromCustomFilter("spam").toPacket());
    }

    @Test
    void removeFromCustomFilterFromPacket() {
        assertEquals(new RemoveFromCustomFilter("spam"), RemoveFromCustomFilter.fromPacket(removeFromCustomFilterPacket()));
    }

    // ---- SetChatStylePreference ----

    static HPacket setChatStylePreferencePacket() {
        HPacket p = new HPacket("SetChatStylePreference", HMessage.Direction.TOSERVER);
        p.appendInt(1001).appendInt(3);
        return p;
    }

    @Test
    void setChatStylePreferenceToPacket() {
        assertSameBytes(setChatStylePreferencePacket(),
                new SetChatStylePreference(ChatBarStyle.NFT_AVATAR_GOLD, ChatFontSize.XL).toPacket());
    }

    @Test
    void setChatStylePreferenceFromPacket() {
        assertEquals(new SetChatStylePreference(ChatBarStyle.NFT_AVATAR_GOLD, ChatFontSize.XL),
                SetChatStylePreference.fromPacket(setChatStylePreferencePacket()));
    }

    // ---- Shout (outgoing) ----

    static HPacket outgoingShoutPacket() {
        HPacket p = new HPacket("Shout", HMessage.Direction.TOSERVER);
        p.appendString("HELLO").appendInt(2);
        return p;
    }

    @Test
    void outgoingShoutToPacket() {
        assertSameBytes(outgoingShoutPacket(), new me.roboroads.gearth.gpackets.outgoing.Shout("HELLO", ChatBarStyle.ROBOT).toPacket());
    }

    @Test
    void outgoingShoutFromPacket() {
        assertEquals(new me.roboroads.gearth.gpackets.outgoing.Shout("HELLO", ChatBarStyle.ROBOT),
                me.roboroads.gearth.gpackets.outgoing.Shout.fromPacket(outgoingShoutPacket()));
    }

    @Test
    void anEmptyShoutIsNotSent() {
        me.roboroads.gearth.gpackets.outgoing.Shout empty = new me.roboroads.gearth.gpackets.outgoing.Shout("", ChatBarStyle.DEFAULT);

        assertEquals("Shout breaks 1 limit (use toPacketUnchecked() to send it anyway):\n  text: must not be empty",
                assertThrows(LimitException.class, empty::toPacket).getMessage());
    }

    // ---- StartTyping ----

    static HPacket startTypingPacket() {
        return new HPacket("StartTyping", HMessage.Direction.TOSERVER);
    }

    @Test
    void startTypingToPacket() {
        assertSameBytes(startTypingPacket(), new StartTyping().toPacket());
    }

    @Test
    void startTypingFromPacket() {
        assertEquals(new StartTyping(), StartTyping.fromPacket(startTypingPacket()));
    }

    // ---- Whisper (outgoing) ----

    static HPacket outgoingWhisperPacket() {
        HPacket p = new HPacket("Whisper", HMessage.Direction.TOSERVER);
        p.appendString("Alice hello").appendInt(2);
        return p;
    }

    @Test
    void outgoingWhisperToPacket() {
        assertSameBytes(outgoingWhisperPacket(),
                new me.roboroads.gearth.gpackets.outgoing.Whisper("Alice hello", ChatBarStyle.ROBOT).toPacket());
    }

    @Test
    void outgoingWhisperFromPacket() {
        assertEquals(new me.roboroads.gearth.gpackets.outgoing.Whisper("Alice hello", ChatBarStyle.ROBOT),
                me.roboroads.gearth.gpackets.outgoing.Whisper.fromPacket(outgoingWhisperPacket()));
    }

    @Test
    void anEmptyWhisperIsNotSent() {
        me.roboroads.gearth.gpackets.outgoing.Whisper empty = new me.roboroads.gearth.gpackets.outgoing.Whisper("", ChatBarStyle.DEFAULT);

        assertEquals("Whisper breaks 1 limit (use toPacketUnchecked() to send it anyway):\n  text: must not be empty",
                assertThrows(LimitException.class, empty::toPacket).getMessage());
    }
}
