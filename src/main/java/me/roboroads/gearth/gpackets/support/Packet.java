package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;

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
        type.replaceBody(message, this::toPacket);
    }
}
