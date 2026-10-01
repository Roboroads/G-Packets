package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.wired.WiredMovement;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

@Data
@Builder
@Jacksonized
public class WiredMovements implements Packet, JsonSerializable {
    public static final PacketType<WiredMovements> TYPE = PacketType.of("WiredMovements", HMessage.Direction.TOCLIENT, Schema.of(WiredMovements.class)
            .list("movements", WiredMovement.SCHEMA));

    List<WiredMovement> movements;

    public static WiredMovements fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredMovements fromJson(String json) {
        return Json.parse(WiredMovements.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
