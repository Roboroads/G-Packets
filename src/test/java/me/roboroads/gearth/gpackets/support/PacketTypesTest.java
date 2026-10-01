package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

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
}
