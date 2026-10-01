package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.user.UserUpdateData;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** Where users in the room are, which way they face, and whether they walk, sit or lay. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdate implements Packet, JsonSerializable {
    public static final PacketType<UserUpdate> TYPE = PacketType.of("UserUpdate", HMessage.Direction.TOCLIENT, Schema.of(UserUpdate.class)
            .list("users", UserUpdateData.SCHEMA));

    private List<UserUpdateData> users;

    public static UserUpdate fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserUpdate fromJson(String json) {
        return Json.parse(UserUpdate.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
