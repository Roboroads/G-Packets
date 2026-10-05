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

/** A user in the room started or stopped typing: the client shows a typing bubble above them. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserTyping implements Packet, JsonSerializable {
    public static final PacketType<UserTyping> TYPE = PacketType.of("UserTyping", HMessage.Direction.TOCLIENT, Schema.of(UserTyping.class)
            .integer("userIndex")
            .integer("isTyping"));

    // The user's room index (User.userIndex), not their account id: the room engine uses it as the
    // room object id. The client calls it userId.
    private Integer userIndex;
    // Sent as an int: the client reads 1 as typing and anything else as not typing.
    private Integer isTyping;

    public static UserTyping fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserTyping fromJson(String json) {
        return Json.parse(UserTyping.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
