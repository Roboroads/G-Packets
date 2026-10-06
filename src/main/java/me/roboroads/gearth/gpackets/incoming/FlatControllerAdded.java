package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.FlatController;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** A user got rights in a room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FlatControllerAdded implements Packet, JsonSerializable {
    public static final PacketType<FlatControllerAdded> TYPE = PacketType.of("FlatControllerAdded", HMessage.Direction.TOCLIENT, Schema.of(FlatControllerAdded.class)
            .integer("roomId")
            .struct("controller", FlatController.SCHEMA));

    // The client calls it flatId; the room settings ignore the packet unless it's for the room they
    // show.
    private Integer roomId;
    // The client calls it data.
    private FlatController controller;

    public static FlatControllerAdded fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FlatControllerAdded fromJson(String json) {
        return Json.parse(FlatControllerAdded.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
