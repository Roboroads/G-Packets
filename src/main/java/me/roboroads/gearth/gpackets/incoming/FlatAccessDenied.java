package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
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
 * Nobody let you in after you rang the doorbell, or, with a name, the user who rang your room's
 * doorbell was turned away.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FlatAccessDenied implements Packet, JsonSerializable {
    public static final PacketType<FlatAccessDenied> TYPE = PacketType.of("FlatAccessDenied", HMessage.Direction.TOCLIENT, Schema.of(FlatAccessDenied.class)
            .integer("roomId")
            .optional(s -> s.string("userName")));

    // The client calls it flatId; it looks up the room session with it.
    private Integer roomId;
    // The user who was turned away. Missing or empty means it's you: the client then closes the
    // room session.
    private String userName;

    public static FlatAccessDenied fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FlatAccessDenied fromJson(String json) {
        return Json.parse(FlatAccessDenied.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
