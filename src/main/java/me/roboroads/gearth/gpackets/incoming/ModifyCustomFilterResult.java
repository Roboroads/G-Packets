package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.CustomFilterResult;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A word was added to or removed from your custom word filter, the answer to
 * {@code AddToCustomFilter} and {@code RemoveFromCustomFilter}.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCustomFilterResult implements Packet, JsonSerializable {
    public static final PacketType<ModifyCustomFilterResult> TYPE = PacketType.of("ModifyCustomFilterResult", HMessage.Direction.TOCLIENT, Schema.of(ModifyCustomFilterResult.class)
            .enumInt("result", CustomFilterResult.class)
            .string("word"));

    private CustomFilterResult result;
    private String word;

    public static ModifyCustomFilterResult fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ModifyCustomFilterResult fromJson(String json) {
        return Json.parse(ModifyCustomFilterResult.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
