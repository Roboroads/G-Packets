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
 * The name of a furni owner, from the list Objects and Items send before their furni. The client puts
 * the pairs in a map and gives each furni the name of its ownerId.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FurniOwner implements SubPacket, JsonSerializable {
    public static final Schema<FurniOwner> SCHEMA = Schema.of(FurniOwner.class)
            .integer("userId")
            .string("name");

    // The owner's account id: the ownerId of their furni.
    private Integer userId;
    private String name;

    public static FurniOwner fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
