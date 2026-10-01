package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.user.UnknownUserChangeEntry;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.List;

/** A user in the room changed their figure or motto. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserChange implements Packet, JsonSerializable {
    public static final PacketType<UserChange> TYPE = PacketType.of("UserChange", HMessage.Direction.TOCLIENT, Schema.of(UserChange.class)
            .integer("id")
            .string("figure")
            .string("sex")
            .string("customInfo")
            .integer("achievementScore")
            .string("unknownString6")
            .list("unknownList7", UnknownUserChangeEntry.SCHEMA)
            .integer("badgesRank"));

    // The user's room index (User.roomIndex), not their account id: the client looks it up with
    // UserDataManager.getUserDataByIndex.
    private Integer id;
    private String figure;
    // "M" or "F". A plain string, not Gender: the client upper-cases it after reading, so the
    // server may send it in lower case, and an enum would change the bytes on a write.
    private String sex;
    // The motto.
    private String customInfo;
    private Integer achievementScore;
    @Unused("The client reads it and throws it away")
    @Deprecated
    private String unknownString6;
    @Unused("The client reads it and throws it away")
    @Deprecated
    private List<UnknownUserChangeEntry> unknownList7;
    private Integer badgesRank;

    public static UserChange fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserChange fromJson(String json) {
        return Json.parse(UserChange.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
