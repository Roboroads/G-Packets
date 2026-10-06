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

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.notEmpty;

/** Adds a word to your custom word filter; the server answers with {@code ModifyCustomFilterResult}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AddToCustomFilter implements Packet, JsonSerializable {
    public static final PacketType<AddToCustomFilter> TYPE = PacketType.of("AddToCustomFilter", HMessage.Direction.TOSERVER, Schema.of(AddToCustomFilter.class)
            .string("word", notEmpty()));

    // The text of the add_word_input field. WordFilterSettingsView.onAddWordClick never sends an empty
    // word, or one already in its list. The field has no character cap.
    private String word;

    public static AddToCustomFilter fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AddToCustomFilter fromJson(String json) {
        return Json.parse(AddToCustomFilter.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
