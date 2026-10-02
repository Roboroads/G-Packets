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
 * One account of {@code IdentityAccounts}. The client's parser reads the pairs into a dictionary
 * keyed by the id, and the login screen turns each into an {@code AvatarData} with that id and
 * name.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class IdentityAccount implements SubPacket, JsonSerializable {
    public static final Schema<IdentityAccount> SCHEMA = Schema.of(IdentityAccount.class)
            .integer("userId")
            .string("name");

    // The account id. The client stores it as AvatarData.id.
    private Integer userId;
    private String name;

    public static IdentityAccount fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
