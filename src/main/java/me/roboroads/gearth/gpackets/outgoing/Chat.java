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

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Packet, JsonSerializable {
    public static final String HEADER = "Chat";

    private String text;
    private ChatBarStyle style;
    @Builder.Default
    private int trackingId = -1;

    public static Chat fromPacket(HPacket packet) {
        return Chat.builder()
                .text(packet.readString())
                .style(ChatBarStyle.fromValue(packet.readInteger()))
                .trackingId(packet.readInteger())
                .build();
    }

    public static Chat fromJson(String json) {
        return Json.parse(Chat.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket(HEADER, HMessage.Direction.TOSERVER);
        packet.appendString(text);
        packet.appendInt(style != null ? style.value() : 0);
        packet.appendInt(trackingId);
        return packet;
    }
}
