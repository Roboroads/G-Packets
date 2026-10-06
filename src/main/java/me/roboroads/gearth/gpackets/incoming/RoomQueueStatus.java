package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.RoomQueueSet;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** Where you are in the queues of a full room you wait to enter. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomQueueStatus implements Packet, JsonSerializable {
    public static final PacketType<RoomQueueStatus> TYPE = PacketType.of("RoomQueueStatus", HMessage.Direction.TOCLIENT, Schema.of(RoomQueueStatus.class)
            .integer("roomId")
            .list("queueSets", RoomQueueSet.SCHEMA));

    // The client calls it flatId; it looks up the room session with it.
    private Integer roomId;
    // The client treats the first set's target as the queue you're in (activeTarget).
    private List<RoomQueueSet> queueSets;

    public static RoomQueueStatus fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomQueueStatus fromJson(String json) {
        return Json.parse(RoomQueueStatus.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
