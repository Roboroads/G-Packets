package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.DanceStyle;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Starts or stops dancing; the server tells the room with the incoming {@code Dance}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Dance implements Packet, JsonSerializable {
    public static final PacketType<Dance> TYPE = PacketType.of("Dance", HMessage.Direction.TOSERVER, Schema.of(Dance.class)
            .enumInt("style", DanceStyle.class));

    // POGO_MOGO, DUCK_FUNK and THE_ROLLIE are only offered to club (HC) members.
    private DanceStyle style;

    public static Dance fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Dance fromJson(String json) {
        return Json.parse(Dance.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
