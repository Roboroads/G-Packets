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
 * The words in your custom word filter, the answer to {@code GetCustomFilter}. The word filter
 * settings in the toolbar list them.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomFilterResult implements Packet, JsonSerializable {
    public static final PacketType<GetCustomFilterResult> TYPE = PacketType.of("GetCustomFilterResult", HMessage.Direction.TOCLIENT, Schema.of(GetCustomFilterResult.class)
            .list("words", WireType.STRING));

    private List<String> words;

    public static GetCustomFilterResult fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static GetCustomFilterResult fromJson(String json) {
        return Json.parse(GetCustomFilterResult.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
