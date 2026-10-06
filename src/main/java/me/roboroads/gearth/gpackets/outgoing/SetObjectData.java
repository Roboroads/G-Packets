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
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * Saves key/value data on a floor furni. The client sends it from the info stand (its set_values button
 * through InfoStandWidgetHandler.setObjectData, and RWFAM_SAVE_STUFF_DATA) and from VimeoDisplayWidget,
 * which sets videoId.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SetObjectData implements Packet, JsonSerializable {
    public static final PacketType<SetObjectData> TYPE = PacketType.of("SetObjectData", HMessage.Direction.TOSERVER, Schema.of(SetObjectData.class)
            .integer("furniId")
            .list("data", WireType.STRING)
            .rule("data holds keys and values in pairs (an even number of strings)",
                    values -> ((List<?>) values.get("data")).size() % 2 == 0));

    // The furni's id in the room (FloorItem.furniId).
    private Integer furniId;
    // Keys and values in turn: key, value, key, value. The client writes a map's size times two as the
    // count, then each key and its value, so the count is always even. G-Rust reads it as a map.
    private List<String> data;

    public static SetObjectData fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SetObjectData fromJson(String json) {
        return Json.parse(SetObjectData.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
