package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.trading.TradeNftAsset;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The NFT assets both users put in the trade, sent when either offer changes. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradeNftAssets implements Packet, JsonSerializable {
    public static final PacketType<TradeNftAssets> TYPE = PacketType.of("TradeNftAssets", HMessage.Direction.TOCLIENT, Schema.of(TradeNftAssets.class)
            .list("ownAssets", TradeNftAsset.SCHEMA)
            .list("otherUserAssets", TradeNftAsset.SCHEMA));

    // What you offer. The client calls it myItems.
    private List<TradeNftAsset> ownAssets;
    // What the other user offers. The client calls it theirItems.
    private List<TradeNftAsset> otherUserAssets;

    public static TradeNftAssets fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradeNftAssets fromJson(String json) {
        return Json.parse(TradeNftAssets.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
