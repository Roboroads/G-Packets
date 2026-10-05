package me.roboroads.gearth.gpackets.incoming;

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

/**
 * You got or lost a purchasable chat style. The client updates its list from
 * {@code UserPurchasableChatStyles} and shows notification.chatstyles.added ("You obtained a new
 * chatbubble!") or notification.chatstyles.removed ("You lost a chatbubble.").
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserPurchasableChatStyleChanged implements Packet, JsonSerializable {
    public static final PacketType<UserPurchasableChatStyleChanged> TYPE = PacketType.of("UserPurchasableChatStyleChanged", HMessage.Direction.TOCLIENT, Schema.of(UserPurchasableChatStyleChanged.class)
            .bool("added")
            .enumInt("style", ChatBarStyle.class));

    // True when you got the style, false when you lost it.
    private Boolean added;
    // The client calls it styleId.
    private ChatBarStyle style;

    public static UserPurchasableChatStyleChanged fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserPurchasableChatStyleChanged fromJson(String json) {
        return Json.parse(UserPurchasableChatStyleChanged.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
