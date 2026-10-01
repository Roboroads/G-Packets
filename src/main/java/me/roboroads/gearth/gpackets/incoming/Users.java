package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.user.User;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

@Data
@Builder
@Jacksonized
public class Users implements Packet, JsonSerializable {
    public static final PacketType<Users> TYPE = PacketType.of("Users", HMessage.Direction.TOCLIENT, Schema.of(Users.class)
            .list("users", User.SCHEMA));

    List<User> users;

    public static Users fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Users fromJson(String json) {
        return Json.parse(Users.class, json);
    }

    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
