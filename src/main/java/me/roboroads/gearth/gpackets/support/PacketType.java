package me.roboroads.gearth.gpackets.support;

import gearth.extensions.ExtensionBase;
import gearth.extensions.IExtension;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Describes a packet type: its header name, direction and how to parse it.
 * Each packet class exposes one as {@code public static final PacketType<X> TYPE},
 * the single source of truth for that packet's header and direction.
 */
public final class PacketType<T extends Packet> {
    private final String header;
    private final HMessage.Direction direction;
    private final Function<HPacket, T> parser;

    public PacketType(String header, HMessage.Direction direction, Function<HPacket, T> parser) {
        this.header = header;
        this.direction = direction;
        this.parser = parser;
    }

    public String header() {
        return header;
    }

    public HMessage.Direction direction() {
        return direction;
    }

    /**
     * Parses the packet from the start of its body and restores the read index afterwards,
     * so parsing is safe when several listeners, or the caller's own code, read the same packet.
     */
    public T parse(HPacket packet) {
        int previous = packet.getReadIndex();
        try {
            packet.resetReadIndex();
            return parser.apply(packet);
        } finally {
            packet.setReadIndex(previous);
        }
    }

    /**
     * Wraps a handler as a G-Earth listener that parses the message's packet before calling it.
     */
    public ExtensionBase.MessageListener listen(BiConsumer<T, HMessage> handler) {
        return message -> handler.accept(parse(message.getPacket()), message);
    }

    /**
     * Registers a handler for this packet type on the given extension.
     */
    public void intercept(IExtension ext, BiConsumer<T, HMessage> handler) {
        ext.intercept(direction, header, listen(handler));
    }
}
