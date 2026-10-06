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

/** A user in the trade accepted the offers, or took it back. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradingAccept implements Packet, JsonSerializable {
    public static final PacketType<TradingAccept> TYPE = PacketType.of("TradingAccept", HMessage.Direction.TOCLIENT, Schema.of(TradingAccept.class)
            .integer("userId")
            .integer("userAccepts"));

    // The user's account id; the client compares it with your own user id.
    private Integer userId;
    // An int flag: more than 0 means the user accepts, 0 that they took it back (UnacceptTrading).
    private Integer userAccepts;

    public static TradingAccept fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradingAccept fromJson(String json) {
        return Json.parse(TradingAccept.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
