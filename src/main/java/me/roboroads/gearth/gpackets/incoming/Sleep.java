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

/** A user in the room falls asleep (idles) or wakes up. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Sleep implements Packet, JsonSerializable {
    public static final PacketType<Sleep> TYPE = PacketType.of("Sleep", HMessage.Direction.TOCLIENT, Schema.of(Sleep.class)
            .integer("userIndex")
            .bool("sleeping"));

    // The user's room index (User.userIndex), not their account id. The client calls it userId.
    private Integer userIndex;
    private Boolean sleeping;

    public static Sleep fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Sleep fromJson(String json) {
        return Json.parse(Sleep.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
