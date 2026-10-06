package me.roboroads.gearth.gpackets.outgoing;

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

/** Asks a user in the room to trade: the trade button in their info stand. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class OpenTrading implements Packet, JsonSerializable {
    public static final PacketType<OpenTrading> TYPE = PacketType.of("OpenTrading", HMessage.Direction.TOSERVER, Schema.of(OpenTrading.class)
            .integer("userIndex"));

    // The user's index in this room, as in UserUpdate: the info stand passes the user data's
    // roomObjectId (InfoStandWidgetHandler, RWUAM_START_TRADING). G-Rust calls it user_index too.
    private Integer userIndex;

    public static OpenTrading fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static OpenTrading fromJson(String json) {
        return Json.parse(OpenTrading.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
