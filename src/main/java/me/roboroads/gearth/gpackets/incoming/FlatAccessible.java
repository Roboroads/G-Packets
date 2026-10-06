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
 * Someone let you in after you rang the doorbell, or, with a name, the user who rang your room's
 * doorbell was let in.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FlatAccessible implements Packet, JsonSerializable {
    public static final PacketType<FlatAccessible> TYPE = PacketType.of("FlatAccessible", HMessage.Direction.TOCLIENT, Schema.of(FlatAccessible.class)
            .integer("roomId")
            .string("userName"));

    // The client calls it flatId; it looks up the room session with it.
    private Integer roomId;
    // The user who was let in. Empty means it's you: the navigator then hides its doorbell view.
    private String userName;

    public static FlatAccessible fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FlatAccessible fromJson(String json) {
        return Json.parse(FlatAccessible.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
