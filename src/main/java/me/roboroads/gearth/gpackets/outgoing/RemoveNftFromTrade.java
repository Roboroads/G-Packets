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

/** Takes one NFT asset back out of the trade: clicking it in your offer. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemoveNftFromTrade implements Packet, JsonSerializable {
    public static final PacketType<RemoveNftFromTrade> TYPE = PacketType.of("RemoveNftFromTrade", HMessage.Direction.TOSERVER, Schema.of(RemoveNftFromTrade.class)
            .integer("assetId"));

    // A TradeNftAsset.assetId from your offer. The client reads asset ids as longs but sends this
    // one as an int.
    private Integer assetId;

    public static RemoveNftFromTrade fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemoveNftFromTrade fromJson(String json) {
        return Json.parse(RemoveNftFromTrade.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
