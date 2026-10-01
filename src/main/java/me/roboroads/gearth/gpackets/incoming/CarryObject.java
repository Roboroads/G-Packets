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

/** The hand item a user in the room carries. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CarryObject implements Packet, JsonSerializable {
    public static final PacketType<CarryObject> TYPE = PacketType.of("CarryObject", HMessage.Direction.TOCLIENT, Schema.of(CarryObject.class)
            .integer("userIndex")
            .integer("itemType"));

    // The user's room index (User.roomIndex), not their account id. The client calls it userId.
    private Integer userIndex;
    private Integer itemType;

    public static CarryObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static CarryObject fromJson(String json) {
        return Json.parse(CarryObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
