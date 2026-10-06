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

/** A user lost their rights in a room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FlatControllerRemoved implements Packet, JsonSerializable {
    public static final PacketType<FlatControllerRemoved> TYPE = PacketType.of("FlatControllerRemoved", HMessage.Direction.TOCLIENT, Schema.of(FlatControllerRemoved.class)
            .integer("roomId")
            .integer("userId"));

    // The client calls it flatId; the room settings ignore the packet unless it's for the room they
    // show.
    private Integer roomId;
    // The account id, as in FlatController.userId.
    private Integer userId;

    public static FlatControllerRemoved fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FlatControllerRemoved fromJson(String json) {
        return Json.parse(FlatControllerRemoved.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
