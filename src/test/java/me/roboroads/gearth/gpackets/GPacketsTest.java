package me.roboroads.gearth.gpackets;

import gearth.extensions.FakeExtension;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.Packet;
import org.junit.jupiter.api.Test;
import testfixtures.NoTypePacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GPacketsTest {

    private static HPacket chatPacket(String text) {
        return new Chat(text, ChatBarStyle.fromValue(0), 1).toPacket();
    }

    private static HPacket usersPacket() {
        return Users.builder().users(Collections.emptyList()).build().toPacket();
    }

    // ---- the three method shapes ----

    static class ThreeShapes {
        String packetOnly;
        String packetAndMessage;
        boolean messageOnly;
        HMessage message;

        @Intercept(Chat.class)
        void onPacketOnly(Chat chat) {
            packetOnly = chat.text();
        }

        @Intercept(Chat.class)
        void onPacketAndMessage(Chat chat, HMessage msg) {
            packetAndMessage = chat.text();
            message = msg;
        }

        @Intercept(Chat.class)
        void onMessageOnly(HMessage msg) {
            messageOnly = true;
        }
    }

    @Test
    void registersAllThreeShapes() {
        FakeExtension ext = new FakeExtension();
        ThreeShapes handler = new ThreeShapes();

        GPackets.init(ext, handler);
        ext.fire(chatPacket("shapes"), HMessage.Direction.TOSERVER);

        assertEquals("shapes", handler.packetOnly);
        assertEquals("shapes", handler.packetAndMessage);
        assertTrue(handler.messageOnly);
        assertEquals(HMessage.Direction.TOSERVER, handler.message.getDestination());
    }

    // ---- multi-class handler with a Packet parameter ----

    static class MultiClass {
        final List<Packet> seen = new ArrayList<>();

        @Intercept({Users.class, Chat.class})
        void onAny(Packet packet, HMessage msg) {
            seen.add(packet);
        }
    }

    @Test
    void multiClassHandlerReceivesEachType() {
        FakeExtension ext = new FakeExtension();
        MultiClass handler = new MultiClass();

        GPackets.init(ext, handler);
        ext.fire(usersPacket(), HMessage.Direction.TOCLIENT);
        ext.fire(chatPacket("hi"), HMessage.Direction.TOSERVER);

        assertEquals(2, handler.seen.size());
        assertInstanceOf(Users.class, handler.seen.get(0));
        assertInstanceOf(Chat.class, handler.seen.get(1));
    }

    // ---- handler in a superclass ----

    static class Base {
        String seen;

        @Intercept(Chat.class)
        void onChat(Chat chat) {
            seen = chat.text();
        }
    }

    static class Sub extends Base {
    }

    @Test
    void registersHandlersDeclaredInASuperclass() {
        FakeExtension ext = new FakeExtension();
        Sub handler = new Sub();

        GPackets.init(ext, handler);
        ext.fire(chatPacket("inherited"), HMessage.Direction.TOSERVER);

        assertEquals("inherited", handler.seen);
    }

    // ---- extension itself is scanned ----

    static class ScanningExtension extends FakeExtension {
        String seen;

        @Intercept(Chat.class)
        void onChat(Chat chat) {
            seen = chat.text();
        }
    }

    @Test
    void scansTheExtensionItself() {
        ScanningExtension ext = new ScanningExtension();

        GPackets.init(ext);
        ext.fire(chatPacket("self"), HMessage.Direction.TOSERVER);

        assertEquals("self", ext.seen);
    }

    // ---- private methods ----

    static class PrivateHandler {
        String seen;

        @Intercept(Chat.class)
        private void onChat(Chat chat) {
            seen = chat.text();
        }
    }

    @Test
    void registersPrivateHandlers() {
        FakeExtension ext = new FakeExtension();
        PrivateHandler handler = new PrivateHandler();

        GPackets.init(ext, handler);
        ext.fire(chatPacket("secret"), HMessage.Direction.TOSERVER);

        assertEquals("secret", handler.seen);
    }

    // ---- inferred packet type (no value) ----

    static class Inferred {
        String fromPacketOnly;
        String fromPacketAndMessage;

        @Intercept
        void onUsers(Users users) {
            fromPacketOnly = "users:" + users.users().size();
        }

        @Intercept
        void onChat(Chat chat, HMessage message) {
            fromPacketAndMessage = chat.text();
        }
    }

    @Test
    void infersPacketTypeFromTheParameter() {
        FakeExtension ext = new FakeExtension();
        Inferred handler = new Inferred();

        GPackets.init(ext, handler);
        ext.fire(usersPacket(), HMessage.Direction.TOCLIENT);
        ext.fire(chatPacket("inferred"), HMessage.Direction.TOSERVER);

        assertEquals("users:0", handler.fromPacketOnly);
        assertEquals("inferred", handler.fromPacketAndMessage);
    }

    static class InferMessageOnly {
        @Intercept
        void onMessage(HMessage message) {
        }
    }

    static class InferPacketBase {
        @Intercept
        void onAny(Packet packet) {
        }
    }

    @Test
    void rejectsInferenceWithoutAPacketParameter() {
        assertThrows(IllegalStateException.class, () -> GPackets.init(new FakeExtension(), new InferMessageOnly()));
    }

    @Test
    void rejectsInferenceFromThePacketBaseType() {
        assertThrows(IllegalStateException.class, () -> GPackets.init(new FakeExtension(), new InferPacketBase()));
    }

    // ---- validation errors ----

    static class MissingType {
        @Intercept(NoTypePacket.class)
        void onNoType(NoTypePacket packet) {
        }
    }

    static class StaticHandler {
        @Intercept(Chat.class)
        static void onChat(Chat chat) {
        }
    }

    static class NonVoidHandler {
        @Intercept(Chat.class)
        String onChat(Chat chat) {
            return "";
        }
    }

    static class BadParams {
        @Intercept(Chat.class)
        void onChat(String notAPacket) {
        }
    }

    @Test
    void rejectsMissingType() {
        assertThrows(IllegalStateException.class, () -> GPackets.init(new FakeExtension(), new MissingType()));
    }

    @Test
    void rejectsStaticMethod() {
        assertThrows(IllegalStateException.class, () -> GPackets.init(new FakeExtension(), new StaticHandler()));
    }

    @Test
    void rejectsNonVoidReturn() {
        assertThrows(IllegalStateException.class, () -> GPackets.init(new FakeExtension(), new NonVoidHandler()));
    }

    @Test
    void rejectsBadParameters() {
        assertThrows(IllegalStateException.class, () -> GPackets.init(new FakeExtension(), new BadParams()));
    }

    // ---- nothing registered when init fails ----

    static class OneValidOneInvalid {
        @Intercept(Chat.class)
        void valid(Chat chat) {
        }

        @Intercept(Chat.class)
        String invalid(Chat chat) {
            return "";
        }
    }

    @Test
    void registersNothingWhenAnyMethodIsInvalid() {
        FakeExtension ext = new FakeExtension();

        assertThrows(IllegalStateException.class, () -> GPackets.init(ext, new OneValidOneInvalid()));
        assertTrue(ext.registrations.isEmpty());
    }
}
