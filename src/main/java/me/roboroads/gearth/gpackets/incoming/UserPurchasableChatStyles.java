package me.roboroads.gearth.gpackets.incoming;

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
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * The purchasable chat styles you own. The chat style selector offers a style marked purchasable
 * only when it's in this list; {@code UserPurchasableChatStyleChanged} adds or removes one later.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserPurchasableChatStyles implements Packet, JsonSerializable {
    public static final PacketType<UserPurchasableChatStyles> TYPE = PacketType.of("UserPurchasableChatStyles", HMessage.Direction.TOCLIENT, Schema.of(UserPurchasableChatStyles.class)
            .list("styleIds", WireType.INT));

    // ChatBarStyle ids: ChatBarStyle.of(id) gives the style. The client calls it chatStyleIds.
    private List<Integer> styleIds;

    public static UserPurchasableChatStyles fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserPurchasableChatStyles fromJson(String json) {
        return Json.parse(UserPurchasableChatStyles.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
