package me.roboroads.gearth.gpackets.docs;

import gearth.extensions.FakeExtension;
import gearth.protocol.HMessage;
import gearth.services.packet_info.PacketInfo;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.sub.user.Player;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import me.roboroads.gearth.gpackets.outgoing.Chat;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The code examples from the documentation pages, compiled and run against the real API. */
class DocsExamplesTest {

    static class Examples extends FakeExtension {
        final List<String> printed = new ArrayList<>();

        // parameters.md: "Finding the type of any packet"
        void logEveryKnownPacket() {
            intercept(HMessage.Direction.TOCLIENT, message -> {
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
    void theOtherParameterExamplesRun() {
        Examples extension = new Examples();
        extension.logEveryKnownPacket();
        extension.sayHello();
    }

    // json.md
    @Test
    void chatSerializesToTheJsonShownOnThePage() {
        Chat chat = Chat.builder().text("hi").style(ChatBarStyle.ROBOT).trackingId(3).build();

        assertEquals("{\"text\":\"hi\",\"style\":2,\"trackingId\":3}", chat.toJson());
        assertEquals(chat, Chat.fromJson(chat.toJson()));
    }
}
