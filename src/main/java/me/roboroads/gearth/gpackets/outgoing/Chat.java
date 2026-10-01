package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Packet, JsonSerializable {
    public static final PacketType<Chat> TYPE = PacketType.of("Chat", HMessage.Direction.TOSERVER, Schema.of(Chat.class)
            .string("text")
            .integer("style")
            .integer("trackingId"));

    private String text;
    // The chat bubble style id. Static styles (below 1000) are the ChatBarStyle values. The client
    // also sends NFT styles (1000-9999) and purchasable styles (10000-99999), only if the user owns
    // them: see isNftChatStyle and isPurchasableStyle in
    // com/sulake/habbo/ui/widget/chatinput/RoomChatInputView.as, and the style list in
    // binaryData/455_chatstyles_xml. New styles ship as data, so this stays a plain int.
    private Integer style;
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
