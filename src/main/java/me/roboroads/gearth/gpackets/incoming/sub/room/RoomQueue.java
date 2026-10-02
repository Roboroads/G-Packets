package me.roboroads.gearth.gpackets.incoming.sub.room;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** One queue in a {@link RoomQueueSet} and how many users wait in it. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomQueue implements SubPacket, JsonSerializable {
    public static final Schema<RoomQueue> SCHEMA = Schema.of(RoomQueue.class)
            .string("queueType")
            .integer("size");

    // The client knows "c" (the Habbo Club queue) and "d" (QUEUE_TYPE_NORMAL) in
    // RoomSessionQueueEvent. A plain string: the client keys the queues by it and reads any code.
    private String queueType;
    // The client calls it getQueueSize and shows it plus one as your place in the queue.
    private Integer size;

    public static RoomQueue fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
