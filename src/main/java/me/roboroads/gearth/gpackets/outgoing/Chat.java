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

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Packet, JsonSerializable {
    public static final PacketType<Chat> TYPE = PacketType.of("Chat", HMessage.Direction.TOSERVER, Schema.of(Chat.class)
            .string("text", notEmpty())
            .enumInt("style", ChatBarStyle.class)
            .integer("trackingId"));

    // The client never sends an empty text (ChatInputWidgetHandler:149). Its input caps typing at 100
    // characters, but habbicons and :command name substitution go past that, so there's no max length.
    private String text;
    private ChatBarStyle style;
    @Builder.Default
    private int trackingId = -1;

    public static Chat fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Chat fromJson(String json) {
        return Json.parse(Chat.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
