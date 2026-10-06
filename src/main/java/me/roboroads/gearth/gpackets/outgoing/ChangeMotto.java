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

/** Changes your motto. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ChangeMotto implements Packet, JsonSerializable {
    public static final PacketType<ChangeMotto> TYPE = PacketType.of("ChangeMotto", HMessage.Direction.TOSERVER, Schema.of(ChangeMotto.class)
            .string("motto"));

    // The infostand's motto field (InfoStandUserView, user_view layout) sets no maximum length.
    private String motto;

    public static ChangeMotto fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ChangeMotto fromJson(String json) {
        return Json.parse(ChangeMotto.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
