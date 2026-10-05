package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomBanDuration;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Bans a user from a room for an hour, a day or for good: the avatar menu's ban buttons. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BanUserWithDuration implements Packet, JsonSerializable {
    public static final PacketType<BanUserWithDuration> TYPE = PacketType.of("BanUserWithDuration", HMessage.Direction.TOSERVER, Schema.of(BanUserWithDuration.class)
            .integer("userId")
            .integer("roomId")
            .enumString("duration", RoomBanDuration.class));

    // The user's account id (User.id), not their room index: the client sends the user data's
    // webID.
    private Integer userId;
    // The client sends the room session's roomId, the room you're in.
    private Integer roomId;
    // The client sends the avatar menu action's name. The composer takes it before roomId but
    // writes it last.
    private RoomBanDuration duration;

    public static BanUserWithDuration fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BanUserWithDuration fromJson(String json) {
        return Json.parse(BanUserWithDuration.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
