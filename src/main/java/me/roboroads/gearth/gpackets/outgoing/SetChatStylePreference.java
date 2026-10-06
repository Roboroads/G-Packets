package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ChatBarStyle;
import me.roboroads.gearth.gpackets.model.enums.ChatFontSize;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Saves your chat style and chat text size. The free flow chat sends it when you chat with another
 * style than your saved one, and when you pick a text size in the chat style selector.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SetChatStylePreference implements Packet, JsonSerializable {
    public static final PacketType<SetChatStylePreference> TYPE = PacketType.of("SetChatStylePreference", HMessage.Direction.TOSERVER, Schema.of(SetChatStylePreference.class)
            .enumInt("style", ChatBarStyle.class)
            .enumInt("fontSize", ChatFontSize.class));

    // The client calls it preferedChatStyle.
    private ChatBarStyle style;
    // The client calls it chatFontSizeMode and clamps it to 0..4 before it sends it.
    private ChatFontSize fontSize;

    public static SetChatStylePreference fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SetChatStylePreference fromJson(String json) {
        return Json.parse(SetChatStylePreference.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
