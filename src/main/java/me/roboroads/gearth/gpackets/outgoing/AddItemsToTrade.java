package me.roboroads.gearth.gpackets.outgoing;

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

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxSize;

/** Puts several furni of one stack from your inventory in the trade. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AddItemsToTrade implements Packet, JsonSerializable {
    public static final PacketType<AddItemsToTrade> TYPE = PacketType.of("AddItemsToTrade", HMessage.Direction.TOSERVER, Schema.of(AddItemsToTrade.class)
            .list("itemIds", WireType.INT, maxSize(1500)));

    // Inventory item ids (the inventory's FurnitureItem.id), like TradeItem.itemId. The client sends
    // AddItemToTrade for a single item, so this list has two or more. FurniModel's
    // requestSelectedFurniToTrading only sends when the items already in the trade plus these come
    // to 1500 or fewer; otherwise it shows trading.items.too_many_items.
    private List<Integer> itemIds;

    public static AddItemsToTrade fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AddItemsToTrade fromJson(String json) {
        return Json.parse(AddItemsToTrade.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
