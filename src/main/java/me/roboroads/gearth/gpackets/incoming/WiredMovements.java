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
import me.roboroads.gearth.gpackets.support.Utils;

import java.util.List;

@Data
@Builder
@Jacksonized
public class WiredMovements implements Packet, JsonSerializable {
    List<WiredMovement> movements;

    public static WiredMovements fromPacket(HPacket packet) {
        return new WiredMovements(Utils.readList(packet, WiredMovement::fromPacket));
    }

    public static WiredMovements fromJson(String json) {
        return Json.parse(WiredMovements.class, json);
    }

    @Override
    public HPacket toPacket() {
        HPacket packet = new HPacket("WiredMovements", HMessage.Direction.TOCLIENT);
        packet.appendInt(movements != null ? movements.size() : 0);
        if (movements != null) {
            for (WiredMovement movement : movements) {
                movement.appendPacket(packet);
            }
        }
        return packet;
    }
}
