package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;

public interface Packet {
    /**
     * Builds this packet.
     *
     * @throws me.roboroads.gearth.gpackets.support.schema.limit.LimitException for an outgoing packet
     *         that breaks the client's limits; see {@link #toPacketUnchecked()}.
     */
    HPacket toPacket();

    /**
     * Replaces the intercepted message's contents with this packet. Nothing changes unless you
     * call it: editing a parsed packet never touches the message on its own.
     *
     * <p>The message keeps its original header id, so G-Earth still recognises the packet; only
     * the body is swapped for this packet's body. Bytes at the end that the schema doesn't know
     * stay, see {@link PacketType#trailingBytes}.
     *
     * @throws IllegalArgumentException if the message's destination does not match this packet's direction.
     * @throws IllegalStateException    if this packet class has no {@code public static final PacketType TYPE}.
     */
    default void replaceIn(HMessage message) {
        PacketType.typeOf(this, "replaceIn").replaceBody(message, this::toPacket);
    }

    /**
     * Builds this packet like {@link #toPacket()}, without checking the limits the client keeps
     * it within, for when you mean to send something the client wouldn't.
     *
     * @throws IllegalStateException if this packet class has no {@code public static final PacketType TYPE}.
     */
    @SuppressWarnings("unchecked")
    default HPacket toPacketUnchecked() {
        return ((PacketType<Packet>) PacketType.typeOf(this, "toPacketUnchecked")).toPacketUnchecked(this);
    }
}
