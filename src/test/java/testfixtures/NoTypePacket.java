package testfixtures;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Packet;

/**
 * A {@link Packet} with no {@code TYPE} field, for exercising GPackets validation. It lives outside
 * the {@code me.roboroads.gearth.gpackets} scan root so PacketImplementationTest does not gate it.
 */
public class NoTypePacket implements Packet {
    @Override
    public HPacket toPacket() {
        return null;
    }
}
