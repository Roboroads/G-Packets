package me.roboroads.gearth.gpackets.incoming.sub.furni;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * One floor furni a roller moves, in SlideObjectBundle. It moves from the bundle's oldX, oldY to its
 * newX, newY; only the heights differ per furni.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SlideObject implements SubPacket, JsonSerializable {
    public static final Schema<SlideObject> SCHEMA = Schema.of(SlideObject.class)
            .integer("furniId")
            .string("oldZ")
            .string("newZ");

    // The furni's id in the room (FloorItem.furniId). The client calls it id.
    private Integer furniId;
    // The height it starts at, as a decimal string (the z of the client's loc).
    private String oldZ;
    // The height it ends at, as a decimal string (the z of the client's target).
    private String newZ;

    public static SlideObject fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
