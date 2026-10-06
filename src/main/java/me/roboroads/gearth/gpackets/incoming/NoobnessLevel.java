package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Noobness;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** How new your account is; the client changes its new user features to match. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class NoobnessLevel implements Packet, JsonSerializable {
    public static final PacketType<NoobnessLevel> TYPE = PacketType.of("NoobnessLevel", HMessage.Direction.TOCLIENT, Schema.of(NoobnessLevel.class)
            .enumInt("noobnessLevel", Noobness.class));

    private Noobness noobnessLevel;

    public static NoobnessLevel fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static NoobnessLevel fromJson(String json) {
        return Json.parse(NoobnessLevel.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
