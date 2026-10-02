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
 * Accepts the offers in the trade: the accept button while you haven't accepted yet. It has no
 * parameters.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class AcceptTrading implements Packet, JsonSerializable {
    public static final PacketType<AcceptTrading> TYPE = PacketType.of("AcceptTrading", HMessage.Direction.TOSERVER, Schema.of(AcceptTrading.class));

    public static AcceptTrading fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AcceptTrading fromJson(String json) {
        return Json.parse(AcceptTrading.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
