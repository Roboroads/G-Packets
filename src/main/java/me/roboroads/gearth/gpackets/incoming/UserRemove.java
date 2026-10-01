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

/** A user, pet or bot left the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserRemove implements Packet, JsonSerializable {
    public static final PacketType<UserRemove> TYPE = PacketType.of("UserRemove", HMessage.Direction.TOCLIENT, Schema.of(UserRemove.class)
            .string("userIndex"));

    // The room index (User.userIndex) as a string: the client turns it into an int and passes it to
    // UserDataManager.removeUserDataByRoomIndex.
    private String userIndex;

    public static UserRemove fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserRemove fromJson(String json) {
        return Json.parse(UserRemove.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
