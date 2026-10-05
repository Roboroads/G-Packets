package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.notEmpty;

/** Whispers to a user in the room; the server sends the incoming {@code Whisper}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Whisper implements Packet, JsonSerializable {
    public static final PacketType<Whisper> TYPE = PacketType.of("Whisper", HMessage.Direction.TOSERVER, Schema.of(Whisper.class)
            .string("text", notEmpty())
            .enumInt("style", ChatBarStyle.class));

    // The recipient's name, a space and the message in one string, like "Alice hello": the client's
    // Whisper composer joins RoomWidgetChatMessage.recipientName and the text. The client never sends
    // an empty message (ChatInputWidgetHandler:149, the same check as Chat), so the string is never
    // empty either.
    private String text;
    private ChatBarStyle style;

    public static Whisper fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Whisper fromJson(String json) {
        return Json.parse(Whisper.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
