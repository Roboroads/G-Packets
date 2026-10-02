package me.roboroads.gearth.gpackets.outgoing;

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

/** Tells the server you clicked a user, pet or bot in the room. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ClickCharacter implements Packet, JsonSerializable {
    public static final PacketType<ClickCharacter> TYPE = PacketType.of("ClickCharacter", HMessage.Direction.TOSERVER, Schema.of(ClickCharacter.class)
            .integer("userIndex"));

    // The clicked avatar's room index (User.userIndex), not an account id: the room view's
    // clickRoomObject sends the room object id of anything in the avatar category.
    private Integer userIndex;

    public static ClickCharacter fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ClickCharacter fromJson(String json) {
        return Json.parse(ClickCharacter.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
