package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.SpecialRoomEffectType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** Plays a visual effect on the whole room: rotate, shake, flip or disco. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SpecialRoomEffect implements Packet, JsonSerializable {
    public static final PacketType<SpecialRoomEffect> TYPE = PacketType.of("SpecialRoomEffect", HMessage.Direction.TOCLIENT, Schema.of(SpecialRoomEffect.class)
            .enumInt("effectType", SpecialRoomEffectType.class));

    // The client calls it effectId.
    private SpecialRoomEffectType effectType;

    public static SpecialRoomEffect fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SpecialRoomEffect fromJson(String json) {
        return Json.parse(SpecialRoomEffect.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
