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

/**
 * The server asks you to confirm picking up a furni. The client shows a confirm dialog with the title
 * and body texts, and on OK sends PickupObject again for this furni with {@code confirmed} true.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ObjectRemoveConfirm implements Packet, JsonSerializable {
    public static final PacketType<ObjectRemoveConfirm> TYPE = PacketType.of("ObjectRemoveConfirm", HMessage.Direction.TOCLIENT, Schema.of(ObjectRemoveConfirm.class)
            .integer("placementType")
            .integer("furniId")
            .string("confirmTitle")
            .string("confirmBody"));

    // 1 for a wall item; the client takes any other value as a floor item (PickupObject sends 2, see
    // FurniPlacementType). The client turns it into its room object category (category: 20 or 10) and
    // passes that to PickupObject.
    private Integer placementType;
    // The furni's id in the room. The client calls it id.
    private Integer furniId;
    // A text key: the client shows "${confirmTitle}" as the dialog's title.
    private String confirmTitle;
    // A text key: the client shows "${confirmBody}" as the dialog's text.
    private String confirmBody;

    public static ObjectRemoveConfirm fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static ObjectRemoveConfirm fromJson(String json) {
        return Json.parse(ObjectRemoveConfirm.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
