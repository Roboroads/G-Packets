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

/** A user in the room uses a hand item. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UseObject implements Packet, JsonSerializable {
    public static final PacketType<UseObject> TYPE = PacketType.of("UseObject", HMessage.Direction.TOCLIENT, Schema.of(UseObject.class)
            .integer("userIndex")
            .integer("handItemType"));

    // The user's room index (User.userIndex), not their account id. The client calls it userId.
    private Integer userIndex;
    // The client calls it itemType.
    private Integer handItemType;

    public static UseObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UseObject fromJson(String json) {
        return Json.parse(UseObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
