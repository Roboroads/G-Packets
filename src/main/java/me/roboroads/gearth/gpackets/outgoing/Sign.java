package me.roboroads.gearth.gpackets.outgoing;

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

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.range;

/** Holds up a sign above your avatar. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Sign implements Packet, JsonSerializable {
    public static final PacketType<Sign> TYPE = PacketType.of("Sign", HMessage.Direction.TOSERVER, Schema.of(Sign.class)
            .integer("sign", range(0, 17)));

    // RoomSession.sendSignMessage only sends 0 to 17, both from the sign grid and from ":sign N".
    private Integer sign;

    public static Sign fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Sign fromJson(String json) {
        return Json.parse(Sign.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
