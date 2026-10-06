package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Your own account: name, figure, respects and account flags. Sent after login. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserObject implements Packet, JsonSerializable {
    public static final PacketType<UserObject> TYPE = PacketType.of("UserObject", HMessage.Direction.TOCLIENT, Schema.of(UserObject.class)
            .integer("userId")
            .string("name")
            .string("figure")
            .enumString("sex", Gender.class)
            .string("customData")
            .string("realName")
            .bool("directMail")
            .integer("respectTotal")
            .integer("respectLeft")
            .integer("petRespectLeft")
            .bool("streamPublishingAllowed")
            .string("lastAccessDate")
            .bool("nameChangeAllowed")
            .bool("accountSafetyLocked")
            .optional(a -> a
                    .bool("accountTradeLocked")
                    .string("nameColor")
                    .optional(b -> b
                            .integer("respectReplenishesLeft")
                            .integer("maxRespectPerDay"))));

    // Your account id. The client calls it id.
    private Integer userId;
    private String name;
    private String figure;
    private Gender sex;
    // Nothing in the client reads it. A room user's motto is called custom in the client, so this is
    // likely your motto, but that isn't verified.
    private String customData;
    // Your own infostand shows it.
    private String realName;
    // Nothing in the client reads it.
    private Boolean directMail;
    // The respects you have received.
    private Integer respectTotal;
    // The respects you can still give to users.
    private Integer respectLeft;
    // The respects you can still give to pets; the pet menu hides its respect button at 0.
    private Integer petRespectLeft;
    // Nothing in the client reads it.
    private Boolean streamPublishingAllowed;
    // Nothing in the client reads it.
    private String lastAccessDate;
    private Boolean nameChangeAllowed;
    private Boolean accountSafetyLocked;
    // Read only when bytes are left after accountSafetyLocked, together with nameColor. Nothing in
    // the client reads it.
    private Boolean accountTradeLocked;
    // Nothing in the client reads it.
    private String nameColor;
    // Read only when bytes are left after nameColor, together with maxRespectPerDay. The client
    // uses 0 and 3 when they're missing.
    private Integer respectReplenishesLeft;
    private Integer maxRespectPerDay;

    public static UserObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserObject fromJson(String json) {
        return Json.parse(UserObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
