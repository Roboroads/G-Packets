package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.FurniPlacementType;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Picks up a furni from the room into your inventory, or ejects someone else's. The client sends it for
 * the room object operations OBJECT_PICKUP and OBJECT_EJECT (the info stand, the present widget), and
 * again after you confirm an ObjectRemoveConfirm dialog.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class PickupObject implements Packet, JsonSerializable {
    public static final PacketType<PickupObject> TYPE = PacketType.of("PickupObject", HMessage.Direction.TOSERVER, Schema.of(PickupObject.class)
            .enumInt("placementType", FurniPlacementType.class)
            .integer("furniId")
            .bool("confirmed"));

    // G-Rust's name. The composer turns the room object category into it.
    private FurniPlacementType placementType;
    // The furni's id in the room (the room object's objectId).
    private Integer furniId;
    // True only when the client sends it from onObjectRemoveConfirm, after you click OK in the confirm
    // dialog; false (the composer's default) for OBJECT_PICKUP and OBJECT_EJECT.
    @Builder.Default
    private Boolean confirmed = false;

    public static PickupObject fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static PickupObject fromJson(String json) {
        return Json.parse(PickupObject.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
