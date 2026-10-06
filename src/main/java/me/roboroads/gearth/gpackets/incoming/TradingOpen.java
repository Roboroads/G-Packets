package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A trade opened between two users in the room. Either user can come first: the client swaps them
 * when otherUserId is you.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradingOpen implements Packet, JsonSerializable {
    public static final PacketType<TradingOpen> TYPE = PacketType.of("TradingOpen", HMessage.Direction.TOCLIENT, Schema.of(TradingOpen.class)
            .integer("userId")
            .integer("userCanTrade")
            .integer("otherUserId")
            .integer("otherUserCanTrade"));

    // An account id: the client looks the user up with getUserData and compares it with your own
    // user id.
    private Integer userId;
    // An int flag the client reads as == 1: whether the user can offer items. Otherwise the client
    // warns that the account doesn't have trading in use (inventory.trading.warning.*_disabled).
    private Integer userCanTrade;
    private Integer otherUserId;
    private Integer otherUserCanTrade;

    public static TradingOpen fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingOpen fromJson(String json) {
        return Json.parse(TradingOpen.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
