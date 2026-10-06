package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
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
 * Asks for the NFT assets you can trade ({@code TradeNftAssetInventory}). The client sends it once
 * per trade. It has no parameters.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class GetNftTradeInventory implements Packet, JsonSerializable {
    public static final PacketType<GetNftTradeInventory> TYPE = PacketType.of("GetNftTradeInventory", HMessage.Direction.TOSERVER, Schema.of(GetNftTradeInventory.class));

    public static GetNftTradeInventory fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetNftTradeInventory fromJson(String json) {
        return Json.parse(GetNftTradeInventory.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
