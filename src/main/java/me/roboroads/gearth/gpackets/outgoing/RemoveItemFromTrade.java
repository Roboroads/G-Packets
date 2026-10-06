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

/** Takes one furni back out of the trade: clicking it in your offer. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemoveItemFromTrade implements Packet, JsonSerializable {
    public static final PacketType<RemoveItemFromTrade> TYPE = PacketType.of("RemoveItemFromTrade", HMessage.Direction.TOSERVER, Schema.of(RemoveItemFromTrade.class)
            .integer("itemId"));

    // The inventory item id of one furni in your offer (TradeItem.itemId).
    private Integer itemId;

    public static RemoveItemFromTrade fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemoveItemFromTrade fromJson(String json) {
        return Json.parse(RemoveItemFromTrade.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
