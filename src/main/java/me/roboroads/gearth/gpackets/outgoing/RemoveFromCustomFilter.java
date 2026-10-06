package me.roboroads.gearth.gpackets.outgoing;

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

/**
 * Removes a word from your custom word filter; the server answers with
 * {@code ModifyCustomFilterResult}.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RemoveFromCustomFilter implements Packet, JsonSerializable {
    public static final PacketType<RemoveFromCustomFilter> TYPE = PacketType.of("RemoveFromCustomFilter", HMessage.Direction.TOSERVER, Schema.of(RemoveFromCustomFilter.class)
            .string("word"));

    // The word selected in the word filter settings' wordlist, as the server sent it.
    private String word;

    public static RemoveFromCustomFilter fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RemoveFromCustomFilter fromJson(String json) {
        return Json.parse(RemoveFromCustomFilter.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
