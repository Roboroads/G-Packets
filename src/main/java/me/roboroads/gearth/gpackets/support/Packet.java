package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;

import java.util.Arrays;

public interface Packet {
    HPacket toPacket();

    /**
     * Replaces the intercepted message's contents with this packet. Nothing changes unless you
     * call it: editing a parsed packet never touches the message on its own.
     *
     * <p>The message keeps its original header id, so G-Earth still recognises the packet; only
     * the body is swapped for this packet's body.
     *
     * @throws IllegalArgumentException if the message's destination does not match this packet's direction.
     * @throws IllegalStateException    if this packet class has no {@code public static final PacketType TYPE}.
     */
    default void replaceIn(HMessage message) {
        PacketType<?> type;
        try {
            type = (PacketType<?>) getClass().getField("TYPE").get(null);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            throw new IllegalStateException(getClass().getName() + " must have a public static final PacketType TYPE to use replaceIn", e);
        }
        if (message.getDestination() != type.direction()) {
            throw new IllegalArgumentException(
                    "Cannot replace a " + message.getDestination() + " message with a " + type.direction() + " packet (" + type.header() + ")");
        }

        byte[] built = toPacket().toBytes();
        // Body = everything after the 4-byte length and 2-byte header.
        byte[] body = Arrays.copyOfRange(built, 6, built.length);
        int headerId = message.getPacket().headerId();
        message.getPacket().setBytes(new HPacket(headerId, body).toBytes());
    }
}
