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

/**
 * Sends an ambassador alert to a user in the room: the ambassador menu's "Send alert" button and
 * the {@code :aalert <name>} chat command. The client only offers it to ambassadors and staff.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AmbassadorAlert implements Packet, JsonSerializable {
    public static final PacketType<AmbassadorAlert> TYPE = PacketType.of("AmbassadorAlert", HMessage.Direction.TOSERVER, Schema.of(AmbassadorAlert.class)
            .integer("userId"));

    // The user's account id (User.id), not their room index: the client sends the user data's
    // webID.
    private Integer userId;

    public static AmbassadorAlert fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AmbassadorAlert fromJson(String json) {
        return Json.parse(AmbassadorAlert.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
