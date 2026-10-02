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

/**
 * One entry of {@code FurnitureAliases}: the room engine shows furni of class {@code name} with
 * the assets of {@code alias} ({@code setRoomObjectAlias(name, alias)}).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureAlias implements SubPacket, JsonSerializable {
    public static final Schema<FurnitureAlias> SCHEMA = Schema.of(FurnitureAlias.class)
            .string("name")
            .string("alias");

    private String name;
    private String alias;

    public static FurnitureAlias fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
