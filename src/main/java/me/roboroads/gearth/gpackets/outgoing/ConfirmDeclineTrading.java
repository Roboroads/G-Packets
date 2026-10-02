package me.roboroads.gearth.gpackets.outgoing;

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
 * Declines the trade in the confirm step: the cancel button after the TradingConfirmation
 * countdown. It has no parameters.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class ConfirmDeclineTrading implements Packet, JsonSerializable {
    public static final PacketType<ConfirmDeclineTrading> TYPE = PacketType.of("ConfirmDeclineTrading", HMessage.Direction.TOSERVER, Schema.of(ConfirmDeclineTrading.class));

    public static ConfirmDeclineTrading fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ConfirmDeclineTrading fromJson(String json) {
        return Json.parse(ConfirmDeclineTrading.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
