package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.trading.TradeItem;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/**
 * The furni both users put in the trade, sent when either offer changes. Either user can come
 * first: compare the user ids with your own.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradingItemList implements Packet, JsonSerializable {
    public static final PacketType<TradingItemList> TYPE = PacketType.of("TradingItemList", HMessage.Direction.TOCLIENT, Schema.of(TradingItemList.class)
            .integer("firstUserId")
            .list("firstUserItems", TradeItem.SCHEMA)
            .integer("firstUserItemCount")
            .integer("firstUserCreditValue")
            .integer("secondUserId")
            .list("secondUserItems", TradeItem.SCHEMA)
            .integer("secondUserItemCount")
            .integer("secondUserCreditValue"));

    // An account id; the client compares it with your own user id.
    private Integer firstUserId;
    // The client calls it firstUserItemArray.
    private List<TradeItem> firstUserItems;
    // The number of furni offered, shown as "%value% items". The client calls it firstUserNumItems.
    private Integer firstUserItemCount;
    // What the credit furni in the offer are worth in credits, shown as "%value% credits". The
    // client calls it firstUserNumCredits.
    private Integer firstUserCreditValue;
    private Integer secondUserId;
    private List<TradeItem> secondUserItems;
    private Integer secondUserItemCount;
    private Integer secondUserCreditValue;

    public static TradingItemList fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingItemList fromJson(String json) {
        return Json.parse(TradingItemList.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
