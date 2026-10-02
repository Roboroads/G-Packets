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
 * Confirms the trade after the TradingConfirmation countdown: the accept button in the confirm
 * step. It has no parameters.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class ConfirmAcceptTrading implements Packet, JsonSerializable {
    public static final PacketType<ConfirmAcceptTrading> TYPE = PacketType.of("ConfirmAcceptTrading", HMessage.Direction.TOSERVER, Schema.of(ConfirmAcceptTrading.class));

    public static ConfirmAcceptTrading fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ConfirmAcceptTrading fromJson(String json) {
        return Json.parse(ConfirmAcceptTrading.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
