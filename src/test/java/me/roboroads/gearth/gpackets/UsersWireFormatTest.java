package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.sub.user.Bot;
import me.roboroads.gearth.gpackets.incoming.sub.user.OldBot;
import me.roboroads.gearth.gpackets.incoming.sub.user.Pet;
import me.roboroads.gearth.gpackets.incoming.sub.user.Player;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UsersWireFormatTest {

    static Users sample() {
        return Users.builder().users(Arrays.asList(
                new Player(1, "Alice", "motto", "hd-180-1", 0, 3, 4, "0.0", Direction.EAST, UserType.PLAYER,
                        Gender.FEMALE, 7, 1, "Group", "swim", 120, true, 5),
                new Pet(2, "Rex", "", "pet-fig", 1, 5, 6, "0.5", Direction.SOUTH, UserType.PET,
                        3, 1, "Alice", 2, true, false, true, false, true, false, 10, "std"),
                new OldBot(3, "Old", "", "bot-fig", 2, 7, 8, "1.0", Direction.WEST, UserType.OLD_BOT),
                new Bot(4, "Frank", "hi", "bot-fig", 3, 9, 10, "0.0", Direction.NORTH, UserType.BOT,
                        Gender.MALE, 1, "Alice", Arrays.asList((short) 1, (short) 5))
        )).build();
    }

    static HPacket expectedPacket() {
        HPacket p = new HPacket("Users", HMessage.Direction.TOCLIENT);
        p.appendInt(4);
        // Player
        p.appendInt(1).appendString("Alice").appendString("motto").appendString("hd-180-1")
                .appendInt(0).appendInt(3).appendInt(4).appendString("0.0").appendInt(2).appendInt(1);
        p.appendString("F").appendInt(7).appendInt(1).appendString("Group").appendString("swim")
                .appendInt(120).appendBoolean(true).appendInt(5);
        // Pet
        p.appendInt(2).appendString("Rex").appendString("").appendString("pet-fig")
                .appendInt(1).appendInt(5).appendInt(6).appendString("0.5").appendInt(4).appendInt(2);
        p.appendInt(3).appendInt(1).appendString("Alice").appendInt(2)
                .appendBoolean(true).appendBoolean(false).appendBoolean(true).appendBoolean(false)
                .appendBoolean(true).appendBoolean(false).appendInt(10).appendString("std");
        // OldBot
        p.appendInt(3).appendString("Old").appendString("").appendString("bot-fig")
                .appendInt(2).appendInt(7).appendInt(8).appendString("1.0").appendInt(6).appendInt(3);
        // Bot
        p.appendInt(4).appendString("Frank").appendString("hi").appendString("bot-fig")
                .appendInt(3).appendInt(9).appendInt(10).appendString("0.0").appendInt(0).appendInt(4);
        p.appendString("M").appendInt(1).appendString("Alice")
                .appendInt(2).appendShort((short) 1).appendShort((short) 5);
        return p;
    }

    @Test
    void toPacketWritesTheWireFormat() {
        assertSameBytes(expectedPacket(), sample().toPacket());
    }

    @Test
    void fromPacketReadsTheWireFormat() {
        assertEquals(sample(), Users.fromPacket(expectedPacket()));
    }

    @Test
    void valuesCanBeEditedAndWrittenBack() {
        Map<String, Object> values = Users.TYPE.read(expectedPacket());
        @SuppressWarnings("unchecked")
        Map<String, Object> first = (Map<String, Object>) ((List<?>) values.get("users")).get(0);
        first.put("name", "Bob");

        Users edited = Users.TYPE.parse(Users.TYPE.write(values));

        assertEquals("Bob", edited.users().get(0).name());
        assertEquals(sample().users().get(1), edited.users().get(1));
    }
}
