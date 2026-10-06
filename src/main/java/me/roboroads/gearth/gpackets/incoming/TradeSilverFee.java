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
 * How much silver the trade needs as a fee. The client shows the fee when it's above 0 or when
 * either user offers NFT assets.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradeSilverFee implements Packet, JsonSerializable {
    public static final PacketType<TradeSilverFee> TYPE = PacketType.of("TradeSilverFee", HMessage.Direction.TOCLIENT, Schema.of(TradeSilverFee.class)
            .integer("silverFee"));

    // The fee both users pay together; at 0 or less the client says it's free for now
    // (inventory.trading.note_silver_fee_free_temporarily). The fee is reached when TradeSilverSet's
    // ownSilver plus otherUserSilver is at least this.
    private Integer silverFee;

    public static TradeSilverFee fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradeSilverFee fromJson(String json) {
        return Json.parse(TradeSilverFee.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
