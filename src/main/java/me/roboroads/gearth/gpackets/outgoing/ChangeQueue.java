package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomQueueTarget;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Moves you to the spectator or the visitor queue of the room you wait to enter. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ChangeQueue implements Packet, JsonSerializable {
    public static final PacketType<ChangeQueue> TYPE = PacketType.of("ChangeQueue", HMessage.Direction.TOSERVER, Schema.of(ChangeQueue.class)
            .enumInt("target", RoomQueueTarget.class));

    // RoomQueueWidgetHandler sends 1 for RWRQM_CHANGE_TO_SPECTATOR_QUEUE and 2 for
    // RWRQM_CHANGE_TO_VISITOR_QUEUE: the same values as RoomQueueSet.target. G-Rust calls it
    // queue_type, but the client's queue type is the "c"/"d" code in RoomQueue.queueType.
    private RoomQueueTarget target;

    public static ChangeQueue fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ChangeQueue fromJson(String json) {
        return Json.parse(ChangeQueue.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
