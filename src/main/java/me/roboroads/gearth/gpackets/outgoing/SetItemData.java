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
 * Saves a stickie's color and text. The stickie widget sends it when you close the stickie after
 * changing the text, and when you pick a color (RWSUM_STICKIE_SEND_UPDATE).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SetItemData implements Packet, JsonSerializable {
    public static final PacketType<SetItemData> TYPE = PacketType.of("SetItemData", HMessage.Direction.TOSERVER, Schema.of(SetItemData.class)
            .integer("furniId")
            .string("colorHex")
            .string("text"));

    // The stickie's id in the room (WallItem.furniId).
    private Integer furniId;
    // The color as six hex digits without "#", like 9CCEFF: the widget message's colorHex. A color
    // button sends its own color; otherwise the client sends back the color from ItemDataUpdate.
    private String colorHex;
    // The widget message's text. The stickie's text field takes at most 500 characters and 14 lines
    // (StickieFurniWidget FIELD_MAX_CHARS, FIELD_MAX_LINES), but a color change resends the text from
    // ItemDataUpdate as it came, so this isn't a checked limit.
    private String text;

    public static SetItemData fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SetItemData fromJson(String json) {
        return Json.parse(SetItemData.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
