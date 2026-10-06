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

import java.util.List;

/** The users with rights in a room, the answer to {@code GetFlatControllers}. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FlatControllers implements Packet, JsonSerializable {
    public static final PacketType<FlatControllers> TYPE = PacketType.of("FlatControllers", HMessage.Direction.TOCLIENT, Schema.of(FlatControllers.class)
            .integer("roomId")
            .list("controllers", FlatController.SCHEMA));

    // The room settings ignore the list unless it's for the room they show.
    private Integer roomId;
    private List<FlatController> controllers;

    public static FlatControllers fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static FlatControllers fromJson(String json) {
        return Json.parse(FlatControllers.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
