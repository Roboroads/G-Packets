package me.roboroads.gearth.gpackets.incoming.sub.user;

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
 * One entry of the list in {@code UserChange} that the client reads and throws away. None of its
 * three ints has a name in the client.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UnknownUserChangeEntry implements SubPacket, JsonSerializable {
    public static final Schema<UnknownUserChangeEntry> SCHEMA = Schema.of(UnknownUserChangeEntry.class)
            .integer("unknownInt1")
            .integer("unknownInt2")
            .integer("unknownInt3");

    private Integer unknownInt1;
    private Integer unknownInt2;
    private Integer unknownInt3;

    public static UnknownUserChangeEntry fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
