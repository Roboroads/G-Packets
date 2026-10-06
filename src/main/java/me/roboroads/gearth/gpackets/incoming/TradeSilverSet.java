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

/** How much silver each user put toward the trade's fee (see TradeSilverFee and SilverFee). */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class TradeSilverSet implements Packet, JsonSerializable {
    public static final PacketType<TradeSilverSet> TYPE = PacketType.of("TradeSilverSet", HMessage.Direction.TOCLIENT, Schema.of(TradeSilverSet.class)
            .integer("ownSilver")
            .integer("otherUserSilver"));

    // Your part; the client shows it in your_silver. The client calls it playerSilver.
    private Integer ownSilver;
    // The other user's part; the client shows it in other_silver. The client calls it
    // otherPlayerSilver.
    private Integer otherUserSilver;

    public static TradeSilverSet fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static TradeSilverSet fromJson(String json) {
        return Json.parse(TradeSilverSet.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
