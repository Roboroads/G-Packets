package me.roboroads.gearth.gpackets.outgoing;

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

/** Answers the doorbell of your room: lets the user in or turns them away. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class LetUserIn implements Packet, JsonSerializable {
    public static final PacketType<LetUserIn> TYPE = PacketType.of("LetUserIn", HMessage.Direction.TOSERVER, Schema.of(LetUserIn.class)
            .string("userName")
            .bool("canEnter"));

    // The name from Doorbell.userName; the client takes both values from RoomWidgetLetUserInMessage.
    private String userName;
    private Boolean canEnter;

    public static LetUserIn fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static LetUserIn fromJson(String json) {
        return Json.parse(LetUserIn.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
