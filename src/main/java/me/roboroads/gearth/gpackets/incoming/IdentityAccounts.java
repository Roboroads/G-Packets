package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.user.IdentityAccount;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** The accounts you can log in with; the client's login screen lists them to pick from. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class IdentityAccounts implements Packet, JsonSerializable {
    public static final PacketType<IdentityAccounts> TYPE = PacketType.of("IdentityAccounts", HMessage.Direction.TOCLIENT, Schema.of(IdentityAccounts.class)
            .list("accounts", IdentityAccount.SCHEMA));

    private List<IdentityAccount> accounts;

    public static IdentityAccounts fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static IdentityAccounts fromJson(String json) {
        return Json.parse(IdentityAccounts.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
