package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
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
 * Someone rang the doorbell of the room you're in, or, with an empty name, you rang a doorbell and
 * now wait for an answer.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Doorbell implements Packet, JsonSerializable {
    public static final PacketType<Doorbell> TYPE = PacketType.of("Doorbell", HMessage.Direction.TOCLIENT, Schema.of(Doorbell.class)
            .string("userName"));

    // The user who rang. Empty means you are the one waiting: the navigator then shows its
    // "waiting" doorbell view, and the room only shows the bell for a name.
    private String userName;

    public static Doorbell fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Doorbell fromJson(String json) {
        return Json.parse(Doorbell.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
