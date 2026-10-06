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

/** A chat bubble the client fills in itself, above a user in the room. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SpecialSystemChat implements Packet, JsonSerializable {
    public static final PacketType<SpecialSystemChat> TYPE = PacketType.of("SpecialSystemChat", HMessage.Direction.TOCLIENT, Schema.of(SpecialSystemChat.class)
            .integer("userIndex")
            .integer("specialSystemType"));

    // The user's room index (User.userIndex): the client looks it up with getUserDataByIndex.
    private Integer userIndex;
    // ChatBubbleFactory.applySpecialChatContent only fills in 67: a bold
    // "6666666...  77777777777777..." bubble. A plain int: the client names no type.
    private Integer specialSystemType;

    public static SpecialSystemChat fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SpecialSystemChat fromJson(String json) {
        return Json.parse(SpecialSystemChat.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
