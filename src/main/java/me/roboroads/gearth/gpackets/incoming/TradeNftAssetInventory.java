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

/** The NFT assets you can put in a trade, the answer to GetNftTradeInventory. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradeNftAssetInventory implements Packet, JsonSerializable {
    public static final PacketType<TradeNftAssetInventory> TYPE = PacketType.of("TradeNftAssetInventory", HMessage.Direction.TOCLIENT, Schema.of(TradeNftAssetInventory.class)
            .list("assets", TradeNftAsset.SCHEMA));

    // The client calls them items and keys them by assetId.
    private List<TradeNftAsset> assets;

    public static TradeNftAssetInventory fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradeNftAssetInventory fromJson(String json) {
        return Json.parse(TradeNftAssetInventory.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
