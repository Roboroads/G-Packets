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
 * Both users accepted the offers: the client starts its countdown, then asks you to confirm with
 * ConfirmAcceptTrading or ConfirmDeclineTrading. It has no parameters.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class TradingConfirmation implements Packet, JsonSerializable {
    public static final PacketType<TradingConfirmation> TYPE = PacketType.of("TradingConfirmation", HMessage.Direction.TOCLIENT, Schema.of(TradingConfirmation.class));

    public static TradingConfirmation fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingConfirmation fromJson(String json) {
        return Json.parse(TradingConfirmation.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
