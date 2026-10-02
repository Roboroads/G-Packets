package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * You can't offer items in the trade. The client shows
 * inventory.trading.warning.own_account_disabled. It has no parameters.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class TradingYouAreNotAllowed implements Packet, JsonSerializable {
    public static final PacketType<TradingYouAreNotAllowed> TYPE = PacketType.of("TradingYouAreNotAllowed", HMessage.Direction.TOCLIENT, Schema.of(TradingYouAreNotAllowed.class));

    public static TradingYouAreNotAllowed fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingYouAreNotAllowed fromJson(String json) {
        return Json.parse(TradingYouAreNotAllowed.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
