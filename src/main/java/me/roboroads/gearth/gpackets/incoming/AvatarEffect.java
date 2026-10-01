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

/** The avatar effect a user in the room shows. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AvatarEffect implements Packet, JsonSerializable {
    public static final PacketType<AvatarEffect> TYPE = PacketType.of("AvatarEffect", HMessage.Direction.TOCLIENT, Schema.of(AvatarEffect.class)
            .integer("userIndex")
            .integer("effectId")
            .integer("delayMilliSeconds"));

    // The user's room index (User.userIndex), not their account id. The client calls it userId.
    private Integer userIndex;
    private Integer effectId;
    private Integer delayMilliSeconds;

    public static AvatarEffect fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AvatarEffect fromJson(String json) {
        return Json.parse(AvatarEffect.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
