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

/** Puts one furni from your inventory in the trade. For more than one, the client sends AddItemsToTrade. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AddItemToTrade implements Packet, JsonSerializable {
    public static final PacketType<AddItemToTrade> TYPE = PacketType.of("AddItemToTrade", HMessage.Direction.TOSERVER, Schema.of(AddItemToTrade.class)
            .integer("itemId"));

    // The inventory item id (the inventory's FurnitureItem.id), like TradeItem.itemId.
    private Integer itemId;

    public static AddItemToTrade fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AddItemToTrade fromJson(String json) {
        return Json.parse(AddItemToTrade.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
