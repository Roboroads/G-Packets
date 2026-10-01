package me.roboroads.gearth.gpackets.incoming;

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

/** A user in the room starts or stops dancing. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Dance implements Packet, JsonSerializable {
    public static final PacketType<Dance> TYPE = PacketType.of("Dance", HMessage.Direction.TOCLIENT, Schema.of(Dance.class)
            .integer("userIndex")
            .enumInt("danceStyle", DanceStyle.class));

    // The user's room index (User.userIndex), not their account id. The client calls it userId.
    private Integer userIndex;
    private DanceStyle danceStyle;

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
