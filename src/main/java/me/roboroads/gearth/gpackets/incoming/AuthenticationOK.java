package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * The server accepted your login ticket. The client answers with {@code InfoRetrieve} and closes
 * the login screen.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationOK implements Packet, JsonSerializable {
    public static final PacketType<AuthenticationOK> TYPE = PacketType.of("AuthenticationOK", HMessage.Direction.TOCLIENT, Schema.of(AuthenticationOK.class)
            .integer("userId")
            .list("suggestedLoginActions", WireType.SHORT)
            .integer("identityId"));

    // Your account id. The client calls it accountId, and nothing in it reads the value.
    private Integer userId;
    // What a new user still has to do. When the list holds 0 or 1, the landing view starts the new
    // user onboarding instead (OnBoardingHcFlow): 0 asks for a name first, 1 to pick a starting room.
    private List<Short> suggestedLoginActions;
    // Nothing in the client reads it.
    private Integer identityId;

    public static AuthenticationOK fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AuthenticationOK fromJson(String json) {
        return Json.parse(AuthenticationOK.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
