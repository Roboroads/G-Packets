package me.roboroads.gearth.gpackets.incoming;

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
 * The other user in the trade can't offer items. The client shows
 * inventory.trading.warning.others_account_disabled. It has no parameters.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class TradingOtherNotAllowed implements Packet, JsonSerializable {
    public static final PacketType<TradingOtherNotAllowed> TYPE = PacketType.of("TradingOtherNotAllowed", HMessage.Direction.TOCLIENT, Schema.of(TradingOtherNotAllowed.class));

    public static TradingOtherNotAllowed fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingOtherNotAllowed fromJson(String json) {
        return Json.parse(TradingOtherNotAllowed.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
