package me.roboroads.gearth.gpackets.docs;

import gearth.extensions.FakeExtension;
import gearth.protocol.HMessage;
import gearth.services.packet_info.PacketInfo;
import me.roboroads.gearth.gpackets.GPackets;
import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.Offer;
import me.roboroads.gearth.gpackets.incoming.sub.user.Player;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.outgoing.SaveRoomSettings;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketTypes;
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.ListParameter;
import me.roboroads.gearth.gpackets.support.schema.OptionalParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.StructParameter;
import me.roboroads.gearth.gpackets.support.schema.ValueParameter;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import me.roboroads.gearth.gpackets.support.schema.limit.MaxLength;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The code examples from the documentation pages, compiled and run against the real API. */
class DocsExamplesTest {

    static class Examples extends FakeExtension {
        final List<String> printed = new ArrayList<>();

        // parameters.md: "Finding the type of any packet"
        void logEveryKnownPacket() {
            for (HMessage.Direction direction : HMessage.Direction.values()) {
                intercept(direction, message -> {
                    PacketInfo info = getPacketInfoManager()
                            .getPacketInfoFromHeaderId(message.getDestination(), message.getPacket().headerId());
                    if (info == null) {
                        return;
                    }
                    PacketTypes.find(message.getDestination(), info.getName()).ifPresent(type -> {
                        System.out.println(info.getName() + " " + type.read(message.getPacket()));
                    });
                });
            }
        }

        // parameters.md: "Changing values and putting them back"
        @SuppressWarnings("unchecked")
        void hideNames() {
            intercept(Users.TYPE.direction(), Users.TYPE.header(), message -> {
                Map<String, Object> values = Users.TYPE.read(message.getPacket());
                for (Object user : (List<?>) values.get("users")) {
                    ((Map<String, Object>) user).put("name", "Hidden");
                }
                Users.TYPE.replaceIn(message, values);
            });
        }

        // parameters.md: "Building a packet from values"
        void sayHello() {
            Map<String, Object> values = new HashMap<>();
            values.put("text", "Hello from values");
            values.put("style", ChatBarStyle.DEFAULT);
            sendToServer(Chat.TYPE.write(values));
        }

        // parameters.md: "Listing the parameters"
        void describe(Schema<?> schema, String indent, Set<Schema<?>> seen) {
            if (!seen.add(schema)) {
                print(indent + "(" + schema.type().getSimpleName() + ", see above)");
                return;
            }
            for (Parameter parameter : schema.parameters()) {
                if (parameter instanceof ValueParameter) {
                    ValueParameter value = (ValueParameter) parameter;
                    print(indent + value.name() + ": " + value.wireType() + " " + value.enumOptions());
                } else if (parameter instanceof ListParameter) {
                    ListParameter list = (ListParameter) parameter;
                    print(indent + list.name() + ": list");
                    if (list.elementSchema() != null) {
                        describe(list.elementSchema(), indent + "  ", seen);
                    }
                } else if (parameter instanceof StructParameter) {
                    print(indent + parameter.name() + ":");
                    describe(((StructParameter) parameter).schema(), indent + "  ", seen);
                } else if (parameter instanceof BranchParameter) {
                    BranchParameter branch = (BranchParameter) parameter;
                    branch.cases().forEach((value, c) -> {
                        print(indent + "when " + branch.on() + " = " + value + ":");
                        describe(c.schema(), indent + "  ", seen);
                    });
                } else if (parameter instanceof OptionalParameter) {
                    print(indent + "optional:");
                    describe(((OptionalParameter) parameter).schema(), indent + "  ", seen);
                }
            }
        }

        // parameters.md: "Unused parameters"
        List<String> unusedOfferParameters() {
            List<String> unused = new ArrayList<>();
            for (Parameter parameter : Offer.SCHEMA.parameters()) {
                if (parameter.unused() != null) {
                    unused.add(parameter.name() + ": " + parameter.unused());
                }
            }
            return unused;
        }

        // changing-and-sending.md: "Limits"
        void saveSettings(SaveRoomSettings settings) {
            try {
                sendToServer(settings.toPacket());
            } catch (LimitException e) {
                for (Violation violation : e.violations()) {
                    print(violation.path() + ": " + violation.message());
                }
            }
        }

        // changing-and-sending.md: "Limits", sending anyway
        void saveSettingsAnyway(SaveRoomSettings settings) {
            sendToServer(settings.toPacketUnchecked());
        }

        // parameters.md: "Limits and rules"
        void describeLimits() {
            for (Parameter parameter : SaveRoomSettings.TYPE.schema().parameters()) {
                for (Limit limit : parameter.limits()) {
                    print(parameter.name() + ": " + limit.describe());
                }
                for (Limit limit : parameter.limits()) {
                    if (limit instanceof MaxLength) {
                        print(parameter.name() + " fits in a field of " + ((MaxLength) limit).max() + " characters");
                    }
                }
            }
        }

        private void print(String line) {
            printed.add(line);
        }
    }

    @Test
    void unusedParametersListsWhatTheClientIgnores() {
        assertEquals(Collections.singletonList("unknownBoolean12: The client stores it but never reads it"),
                new Examples().unusedOfferParameters());
    }

    private static SaveRoomSettings settingsWithALongName() {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < 61; i++) {
            name.append('a');
        }
        return SaveRoomSettings.builder().roomId(1).name(name.toString()).build();
    }

    @Test
    void limitsReportWhatsWrongInsteadOfSending() {
        Examples extension = new Examples();

        extension.saveSettings(settingsWithALongName());

        assertTrue(extension.sentToServer.isEmpty());
        assertEquals(Collections.singletonList("name: at most 60 characters, got 61"), extension.printed);
    }

    @Test
    void uncheckedSendsAnyway() {
        Examples extension = new Examples();

        extension.saveSettingsAnyway(settingsWithALongName());

        assertEquals(1, extension.sentToServer.size());
    }

    @Test
    void toolsCanReadTheLimits() {
        Examples extension = new Examples();

        extension.describeLimits();

        assertTrue(extension.printed.contains("name: at most 60 characters"), extension.printed.toString());
        assertTrue(extension.printed.contains("name fits in a field of 60 characters"), extension.printed.toString());
        assertTrue(extension.printed.contains("idleSleepTimeoutSeconds: VIP only"), extension.printed.toString());
    }

    private static Users oneUser() {
        return Users.builder().users(Collections.singletonList(
                new Player(1, "Alice", "motto", "hd-180-1", 0, 3, 4, "0.0", Direction.EAST, UserType.PLAYER,
                        Gender.FEMALE, 7, 1, "Group", "swim", 120, true)
        )).build();
    }

    @Test
    void hideNamesRewritesTheInterceptedPacket() {
        Examples extension = new Examples();
        extension.hideNames();

        HMessage message = extension.fire(oneUser().toPacket(), HMessage.Direction.TOCLIENT);

        assertEquals("Hidden", Users.TYPE.parse(message.getPacket()).users().get(0).name());
    }

    @Test
    void describeStopsAtTheRecursiveCatalogNode() {
        Examples extension = new Examples();

        extension.describe(CatalogIndex.TYPE.schema(), "", Collections.newSetFromMap(new IdentityHashMap<>()));

        assertTrue(extension.printed.contains("    (CatalogNode, see above)"), extension.printed.toString());
    }

    @Test
    void logEveryKnownPacketListensBothWays() {
        Examples extension = new Examples();

        extension.logEveryKnownPacket();

        assertEquals(new HashSet<>(Arrays.asList(HMessage.Direction.values())), new HashSet<>(extension.everyPacketDirections));
    }

    @Test
    void theOtherParameterExamplesRun() {
        Examples extension = new Examples();
        extension.sayHello();
    }

    // intercepting.md: "Handlers in other classes"; GPackets.init finds and creates it
    static class ChatLogger {
        static final List<String> SAID = new ArrayList<>();

        @Intercept
        void onChat(Chat chat) {
            SAID.add(chat.text());
        }
    }

    // intercepting.md: "Handlers in other classes", a handler that takes the extension
    static class AutoReply {
        private final InterceptingExamples extension;

        AutoReply(InterceptingExamples extension) {
            this.extension = extension;
        }

        @Intercept
        void onChat(Chat chat) {
            if (chat.text().equals("ping")) {
                extension.sendToServer(new Chat("pong", ChatBarStyle.DEFAULT, -1).toPacket());
            }
        }
    }

    static class InterceptingExamples extends FakeExtension {
        int users;
        boolean blocked;

        // intercepting.md: "Intercepting with annotations"
        @Intercept
        void onUsers(Users users) {
            this.users = users.users().size();
        }

        // intercepting.md: Option 1, "Working with the HMessage"
        @Intercept(Chat.class)
        void onChat(Chat chat, HMessage message) {
            if (chat.text().contains("spoiler")) {
                message.setBlocked(true);
                blocked = true;
            }
        }

        // intercepting.md: "Intercepting several packets in one method"
        @Intercept({Users.class, Chat.class})
        void onEither(Packet packet, HMessage message) {
            if (packet instanceof Chat) {
                // ...
            }
        }

        // intercepting.md: "Using the TYPE descriptor" and "Raw interception"
        void registerByHand() {
            Users.TYPE.intercept(this, (users, message) -> {
                System.out.println(users.users().size() + " users");
            });
            intercept(Users.TYPE.direction(), Users.TYPE.header(), Users.TYPE.listen((users, message) -> {
                // ...
            }));
            intercept(Users.TYPE.direction(), Users.TYPE.header(), message -> {
                Users users = Users.fromPacket(message.getPacket());
                // ...
            });
        }

        // intercepting.md: Option 2, "Working with the HMessage"
        void blockSpoilersWithType() {
            Chat.TYPE.intercept(this, (chat, message) -> {
                if (chat.text().contains("spoiler")) {
                    message.setBlocked(true);
                }
            });
        }

        // intercepting.md: Option 3, "Working with the HMessage"
        void blockSpoilersRaw() {
            intercept(Chat.TYPE.direction(), Chat.TYPE.header(), message -> {
                Chat chat = Chat.fromPacket(message.getPacket());
                if (chat.text().contains("spoiler")) {
                    message.setBlocked(true);
                }
            });
        }

        // changing-and-sending.md: "Changing an intercepted packet"
        void shout() {
            Chat.TYPE.intercept(this, (chat, message) -> {
                chat.text(chat.text().toUpperCase());
                chat.replaceIn(message);
            });
        }

        // changing-and-sending.md: "Sending a packet"
        void send() {
            Chat chat = new Chat("Hello, world!", ChatBarStyle.DEFAULT, -1);
            Chat same = Chat.builder().text("Hello, world!").style(ChatBarStyle.DEFAULT).trackingId(-1).build();
            sendToServer(chat.toPacket());

            CatalogPublished published = CatalogPublished.builder()
                    .instantlyRefreshCatalog(true)
                    .build();
            sendToClient(published.toPacket());
        }
    }

    @Test
    void annotatedHandlersRunAndBlock() {
        InterceptingExamples extension = new InterceptingExamples();
        ChatLogger.SAID.clear();
        GPackets.init(extension);

        extension.fire(oneUser().toPacket(), HMessage.Direction.TOCLIENT);
        HMessage chat = extension.fire(new Chat("no spoiler please", ChatBarStyle.DEFAULT, -1).toPacket(), HMessage.Direction.TOSERVER);

        assertEquals(1, extension.users);
        assertTrue(extension.blocked);
        assertTrue(chat.isBlocked());
        assertEquals(Collections.singletonList("no spoiler please"), ChatLogger.SAID);
    }

    @Test
    void foundHandlersCanTakeTheExtension() {
        InterceptingExamples extension = new InterceptingExamples();
        GPackets.init(extension);

        extension.fire(new Chat("ping", ChatBarStyle.DEFAULT, -1).toPacket(), HMessage.Direction.TOSERVER);

        assertEquals(1, extension.sentToServer.size());
        assertEquals("pong", Chat.TYPE.parse(extension.sentToServer.get(0)).text());
    }

    @Test
    void everyInterceptingOptionCanBlock() {
        InterceptingExamples withType = new InterceptingExamples();
        withType.blockSpoilersWithType();
        InterceptingExamples raw = new InterceptingExamples();
        raw.blockSpoilersRaw();

        HMessage blockedByType = withType.fire(new Chat("spoiler!", ChatBarStyle.DEFAULT, -1).toPacket(), HMessage.Direction.TOSERVER);
        HMessage blockedRaw = raw.fire(new Chat("spoiler!", ChatBarStyle.DEFAULT, -1).toPacket(), HMessage.Direction.TOSERVER);
        HMessage passed = raw.fire(new Chat("hello", ChatBarStyle.DEFAULT, -1).toPacket(), HMessage.Direction.TOSERVER);

        assertTrue(blockedByType.isBlocked());
        assertTrue(blockedRaw.isBlocked());
        assertFalse(passed.isBlocked());
    }

    @Test
    void shoutReplacesTheText() {
        InterceptingExamples extension = new InterceptingExamples();
        extension.shout();

        HMessage message = extension.fire(new Chat("hi", ChatBarStyle.DEFAULT, -1).toPacket(), HMessage.Direction.TOSERVER);

        assertEquals("HI", Chat.TYPE.parse(message.getPacket()).text());
    }

    @Test
    void theOtherInterceptingExamplesRun() {
        InterceptingExamples extension = new InterceptingExamples();
        extension.registerByHand();
        extension.send();
    }

    // json.md
    @Test
    void chatSerializesToTheJsonShownOnThePage() {
        Chat chat = Chat.builder().text("hi").style(ChatBarStyle.ROBOT).trackingId(3).build();

        assertEquals("{\"text\":\"hi\",\"style\":2,\"trackingId\":3}", chat.toJson());
        assertEquals(chat, Chat.fromJson(chat.toJson()));
    }

    // changing-and-sending.md: "Creating a packet", a style newer than the library
    @Test
    void anUnnamedChatStyleKeepsItsId() {
        ChatBarStyle style = ChatBarStyle.of(1028);

        assertFalse(style.known());
        assertEquals(1028, style.value());

        Chat chat = Chat.fromPacket(new Chat("hi", ChatBarStyle.ROBOT, -1).toPacket());
        assertTrue(chat.style() == ChatBarStyle.ROBOT);
        assertTrue(ChatBarStyle.of(1028).equals(style));
    }
}
