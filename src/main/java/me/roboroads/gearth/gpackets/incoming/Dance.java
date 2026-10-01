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

/** A user in the room starts or stops dancing. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Dance implements Packet, JsonSerializable {
    public static final PacketType<Dance> TYPE = PacketType.of("Dance", HMessage.Direction.TOCLIENT, Schema.of(Dance.class)
            .integer("userId")
            .integer("danceStyle"));

    // The user's room index (User.roomIndex), not their account id.
    private Integer userId;
    // 0 when the user stops dancing (AvatarInfoWidgetHandler: isDancing = danceStyle != 0).
    private Integer danceStyle;

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
