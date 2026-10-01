package me.roboroads.gearth.gpackets.docs;

import gearth.extensions.FakeExtension;
import gearth.protocol.HMessage;
import gearth.services.packet_info.PacketInfo;
import me.roboroads.gearth.gpackets.GPackets;
import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.sub.user.Player;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketTypes;
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.ListParameter;
import me.roboroads.gearth.gpackets.support.schema.OptionalParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.StructParameter;
import me.roboroads.gearth.gpackets.support.schema.ValueParameter;
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

        private void print(String line) {
            printed.add(line);
        }
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

    // intercepting.md: "Handlers in other classes"
    static class ChatLogger {
        final List<String> said = new ArrayList<>();

        @Intercept
        void onChat(Chat chat) {
            said.add(chat.text());
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
                    .instantlyRefreshCatalogue(true)
                    .build();
            sendToClient(published.toPacket());
        }
    }

    @Test
    void annotatedHandlersRunAndBlock() {
        InterceptingExamples extension = new InterceptingExamples();
        ChatLogger logger = new ChatLogger();
        GPackets.init(extension, logger);

        extension.fire(oneUser().toPacket(), HMessage.Direction.TOCLIENT);
        HMessage chat = extension.fire(new Chat("no spoiler please", ChatBarStyle.DEFAULT, -1).toPacket(), HMessage.Direction.TOSERVER);

        assertEquals(1, extension.users);
        assertTrue(extension.blocked);
        assertTrue(chat.isBlocked());
        assertEquals(Collections.singletonList("no spoiler please"), logger.said);
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
}
