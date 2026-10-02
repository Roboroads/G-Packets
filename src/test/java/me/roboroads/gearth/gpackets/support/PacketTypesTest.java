package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacketTypesTest {

    @Test
    void findReturnsTheTypeForItsDirectionAndHeader() {
        assertSame(Users.TYPE, PacketTypes.find(HMessage.Direction.TOCLIENT, "Users").get());
        assertSame(Chat.TYPE, PacketTypes.find(HMessage.Direction.TOSERVER, "Chat").get());
    }

    @Test
    void findIsEmptyForAnUnknownHeader() {
        assertFalse(PacketTypes.find(HMessage.Direction.TOCLIENT, "NoSuchPacket").isPresent());
    }

    @Test
    void findIsEmptyForTheWrongDirection() {
        assertFalse(PacketTypes.find(HMessage.Direction.TOSERVER, "Users").isPresent());
    }

    @Test
    void allListsEveryTypeOnce() {
        Set<String> seen = new HashSet<>();
        for (PacketType<?> type : PacketTypes.all()) {
            assertTrue(seen.add(type.direction() + " " + type.header()), type.direction() + " " + type.header() + " is listed twice");
        }
    }
}
