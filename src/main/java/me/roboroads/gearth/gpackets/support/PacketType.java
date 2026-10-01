package me.roboroads.gearth.gpackets.support;

import gearth.extensions.ExtensionBase;
import gearth.extensions.IExtension;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Describes a packet type: its header name, direction and wire format.
 * Each packet class exposes one as {@code public static final PacketType<X> TYPE},
 * the single source of truth for that packet's header, direction and parameters.
 */
public final class PacketType<T extends Packet> {
    private final String header;
    private final HMessage.Direction direction;
    private final Schema<T> schema;

    private PacketType(String header, HMessage.Direction direction, Schema<T> schema) {
        this.header = header;
        this.direction = direction;
        this.schema = schema;
    }

    public static <T extends Packet> PacketType<T> of(String header, HMessage.Direction direction, Schema<T> schema) {
        return new PacketType<>(Objects.requireNonNull(header, "header"), Objects.requireNonNull(direction, "direction"), Objects.requireNonNull(schema, "schema"));
    }

    public String header() {
        return header;
    }

    public HMessage.Direction direction() {
        return direction;
    }

    /** The packet's parameters in wire order. */
    public Schema<T> schema() {
        return schema;
    }

    /**
     * Why the current client ignores this packet, from the {@link Unused} on its class, or null
     * when the client handles it.
     */
    public String unused() {
        Unused unused = schema.type().getAnnotation(Unused.class);
        return unused == null ? null : unused.value();
    }

    /**
     * Parses the packet from the start of its body and restores the read index afterwards,
     * so parsing is safe when several listeners, or the caller's own code, read the same packet.
     */
    public T parse(HPacket packet) {
        int previous = packet.getReadIndex();
        try {
            packet.resetReadIndex();
            return schema.parse(packet);
        } finally {
            packet.setReadIndex(previous);
        }
    }

    /**
     * Reads the packet's body into named values, in wire order, and restores the read index
     * afterwards, like {@link #parse}.
     */
    public Map<String, Object> read(HPacket packet) {
        int previous = packet.getReadIndex();
        try {
            packet.resetReadIndex();
            return schema.read(packet);
        } finally {
            packet.setReadIndex(previous);
        }
    }

    /** Builds a packet of this type from named values. A missing or null value writes the wire default. */
    public HPacket write(Map<String, Object> values) {
        HPacket packet = new HPacket(header, direction);
        schema.write(values, packet);
        return packet;
    }

    /**
     * Replaces the intercepted message's body with a packet built from named values, for example
     * values from {@link #read} that a user edited. The message keeps its original header id, so
     * G-Earth still recognises the packet.
     *
     * @throws IllegalArgumentException if the message's destination does not match this type's direction.
     */
    public void replaceIn(HMessage message, Map<String, Object> values) {
        replaceBody(message, () -> write(values));
    }

    void replaceBody(HMessage message, Supplier<HPacket> replacement) {
        if (message.getDestination() != direction) {
            throw new IllegalArgumentException(
                    "Cannot replace a " + message.getDestination() + " message with a " + direction + " packet (" + header + ")");
        }

        byte[] built = replacement.get().toBytes();
        // Body = everything after the 4-byte length and 2-byte header.
        byte[] body = Arrays.copyOfRange(built, 6, built.length);
        int headerId = message.getPacket().headerId();
        message.getPacket().setBytes(new HPacket(headerId, body).toBytes());
    }

    /** Builds a packet of this type from a typed packet object. */
    public HPacket toPacket(T value) {
        HPacket packet = new HPacket(header, direction);
        schema.append(value, packet);
        return packet;
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
