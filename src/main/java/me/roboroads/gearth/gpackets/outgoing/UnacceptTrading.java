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
 * Takes back your acceptance of the offers: the accept button after you accepted. It has no
 * parameters.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
public class UnacceptTrading implements Packet, JsonSerializable {
    public static final PacketType<UnacceptTrading> TYPE = PacketType.of("UnacceptTrading", HMessage.Direction.TOSERVER, Schema.of(UnacceptTrading.class));

    public static UnacceptTrading fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UnacceptTrading fromJson(String json) {
        return Json.parse(UnacceptTrading.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
