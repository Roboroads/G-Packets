package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * The NFT chat styles you own, the answer to {@code GetUserNftChatStyles}. The chat style selector
 * only offers an NFT style (ids 1000 to 9999) when it's in this list.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserNftChatStyles implements Packet, JsonSerializable {
    public static final PacketType<UserNftChatStyles> TYPE = PacketType.of("UserNftChatStyles", HMessage.Direction.TOCLIENT, Schema.of(UserNftChatStyles.class)
            .list("styleIds", WireType.INT));

    // ChatBarStyle ids: ChatBarStyle.of(id) gives the style. The client calls it chatStyleIds.
    private List<Integer> styleIds;

    public static UserNftChatStyles fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static UserNftChatStyles fromJson(String json) {
        return Json.parse(UserNftChatStyles.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
