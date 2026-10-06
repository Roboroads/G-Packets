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

/** The new state data of one wall furni in ItemsStateUpdate. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WallItemStateUpdate implements SubPacket, JsonSerializable {
    public static final Schema<WallItemStateUpdate> SCHEMA = Schema.of(WallItemStateUpdate.class)
            .integer("furniId")
            .string("data");

    // The furni's id in the room (WallItem.furniId), an int here. The client calls it id.
    private Integer furniId;
    // The new WallItem.data. The client calls it itemData and reads the state number from it when it
    // is numeric.
    private String data;

    public static WallItemStateUpdate fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
