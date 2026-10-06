package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Posture;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Sits down or stands up. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ChangePosture implements Packet, JsonSerializable {
    public static final PacketType<ChangePosture> TYPE = PacketType.of("ChangePosture", HMessage.Direction.TOSERVER, Schema.of(ChangePosture.class)
            .enumInt("posture", Posture.class));

    private Posture posture;

    public static ChangePosture fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ChangePosture fromJson(String json) {
        return Json.parse(ChangePosture.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
