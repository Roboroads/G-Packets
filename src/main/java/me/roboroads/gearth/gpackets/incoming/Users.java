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
import me.roboroads.gearth.gpackets.support.Utils;

import java.util.List;

@Data
@Builder
@Jacksonized
public class Users implements Packet, JsonSerializable {
    List<User> users;

    public static Users fromPacket(HPacket packet) {
        List<User> list = Utils.readList(packet, User::fromPacket);

        return new Users(list);
    }

    public static Users fromJson(String json) {
        return Json.parse(Users.class, json);
    }

    public HPacket toPacket() {
        HPacket packet = new HPacket("Users", HMessage.Direction.TOCLIENT);

        packet.appendInt(users.size());
        for (User user : users) {
            user.appendPacket(packet);
        }

        return packet;
    }
}
