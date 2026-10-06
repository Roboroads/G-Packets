package me.roboroads.gearth.gpackets.incoming.sub.room;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomQueueTarget;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The queues for one target (spectators or visitors) of a room you wait to enter. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomQueueSet implements SubPacket, JsonSerializable {
    public static final Schema<RoomQueueSet> SCHEMA = Schema.of(RoomQueueSet.class)
            .string("name")
            .enumInt("target", RoomQueueTarget.class)
            .list("queues", RoomQueue.SCHEMA);

    // RoomSessionHandler copies it into RoomSessionQueueEvent, and nothing calls that event's
    // queueSetName getter.
    @Unused("The client copies it into its queue event and never reads it")
    @Deprecated
    private String name;
    private RoomQueueTarget target;
    private List<RoomQueue> queues;

    public static RoomQueueSet fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
