package me.roboroads.gearth.gpackets.outgoing;

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
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** Puts NFT assets of one product in the trade. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AddNftToTrade implements Packet, JsonSerializable {
    public static final PacketType<AddNftToTrade> TYPE = PacketType.of("AddNftToTrade", HMessage.Direction.TOSERVER, Schema.of(AddNftToTrade.class)
            .list("assetIds", WireType.INT));

    // TradeNftAsset.assetId values. The client reads asset ids as longs but sends them as ints
    // (TradingModel.requestAddNftsToTrading). It sends one or more, up to the unlocked assets you
    // have of the product.
    private List<Integer> assetIds;

    public static AddNftToTrade fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AddNftToTrade fromJson(String json) {
        return Json.parse(AddNftToTrade.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
