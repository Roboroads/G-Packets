package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Saves the outfit a clothing change furni puts on boys or girls. The client sends it when a user with
 * rights saves the furni's avatar editor (FurnitureClothingChangeWidgetHandler.saveFigure, through
 * RoomSession.sendUpdateClothingChangeFurniture).
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SetClothingChangeData implements Packet, JsonSerializable {
    public static final PacketType<SetClothingChangeData> TYPE = PacketType.of("SetClothingChangeData", HMessage.Direction.TOSERVER, Schema.of(SetClothingChangeData.class)
            .integer("furniId")
            .enumString("gender", Gender.class)
            .string("figure"));

    // The furni's id in the room (the widget message's objectId).
    private Integer furniId;
    // Which of the furni's outfits: the editor's figureData.gender. The furni keeps one for boys
    // (furniture_clothing_boy) and one for girls (furniture_clothing_girl). G-Rust has the two strings
    // the other way around.
    private Gender gender;
    // The outfit, as a figure string: the editor's figureData.getFigureString().
    private String figure;

    public static SetClothingChangeData fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SetClothingChangeData fromJson(String json) {
        return Json.parse(SetClothingChangeData.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
