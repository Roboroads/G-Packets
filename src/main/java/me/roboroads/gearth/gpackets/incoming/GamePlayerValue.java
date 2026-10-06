package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** A game value for a user in the room, which the client puts on the user's avatar as a number. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayerValue implements Packet, JsonSerializable {
    public static final PacketType<GamePlayerValue> TYPE = PacketType.of("GamePlayerValue", HMessage.Direction.TOCLIENT, Schema.of(GamePlayerValue.class)
            .integer("userIndex")
            .integer("value"));

    // The user's room index (User.userIndex), not their account id. The client calls it userId and
    // passes it as the room object id to RoomEngine.updateObjectUserAction.
    private Integer userIndex;
    // Set on the avatar as its figure_number_value.
    private Integer value;

    public static GamePlayerValue fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GamePlayerValue fromJson(String json) {
        return Json.parse(GamePlayerValue.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
