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

/** Another user in the room gave you a hand item. The client shows it as a chat notification. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class HandItemReceived implements Packet, JsonSerializable {
    public static final PacketType<HandItemReceived> TYPE = PacketType.of("HandItemReceived", HMessage.Direction.TOCLIENT, Schema.of(HandItemReceived.class)
            .integer("giverUserIndex")
            .integer("handItemType"));

    // The giver's room index (User.userIndex), not their account id. The client calls it giverUserId.
    private Integer giverUserIndex;
    private Integer handItemType;

    public static HandItemReceived fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static HandItemReceived fromJson(String json) {
        return Json.parse(HandItemReceived.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
