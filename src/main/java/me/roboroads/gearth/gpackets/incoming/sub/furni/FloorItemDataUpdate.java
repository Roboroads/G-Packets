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

/** The new state of one floor furni in ObjectsDataUpdate. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FloorItemDataUpdate implements SubPacket, JsonSerializable {
    public static final Schema<FloorItemDataUpdate> SCHEMA = Schema.of(FloorItemDataUpdate.class)
            .integer("furniId")
            .struct("stuffData", StuffData.SCHEMA);

    // The furni's id in the room (FloorItem.furniId), an int here. The client calls it id.
    private Integer furniId;
    // The client calls it data, and reads the state number from its legacy string.
    private StuffData stuffData;

    public static FloorItemDataUpdate fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
