package me.roboroads.gearth.gpackets.incoming.sub.furni;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A floor furni in the room, as the Objects, ObjectAdd and ObjectUpdate parsers read it (the client's
 * parseObjectData). Its owner's name isn't part of it: Objects sends the names in a list before the
 * furni, and ObjectAdd after it.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FloorItem implements SubPacket, JsonSerializable {
    /**
     * The sign bit of an int. {@code furniClassId & SIGN_BIT} is set for a negative
     * {@link #furniClassId}, and only then {@link #staticClass} is on the wire.
     */
    public static final int SIGN_BIT = Integer.MIN_VALUE;

    public static final Schema<FloorItem> SCHEMA = Schema.of(FloorItem.class)
            .integer("furniId")
            .integer("furniClassId")
            .integer("x")
            .integer("y")
            .enumInt("direction", Direction.class)
            .string("z")
            .string("sizeZ")
            .integer("extra")
            .struct("stuffData", StuffData.SCHEMA)
            .integer("secondsToExpiration")
            .integer("usagePolicy")
            .integer("ownerId")
            .when("furniClassId", SIGN_BIT, SIGN_BIT, s -> s.string("staticClass"));

    // The furni's id in the room. The client calls it id.
    private Integer furniId;
    // The furni's type in the furni data, like FurniProduct.furniClassId. The client calls it type. A
    // negative type means the furni is named by staticClass instead.
    private Integer furniClassId;
    private Integer x;
    private Integer y;
    // The client calls it dir and multiplies it by 45 to get degrees.
    private Direction direction;
    // The height the furni stands at, as a decimal string. The client reads it with Number().
    private String z;
    // The furni's own height, as a decimal string. The client passes it to
    // updateObjectFurnitureHeight.
    private String sizeZ;
    // A per-furni extra value. The client stores it as furniture_extra; CustomStackHeightWidget, for
    // example, checks it for 1.
    private Integer extra;
    // The furni's state. The client also reads the state number from its legacy string.
    private StuffData stuffData;
    // Seconds until a rented furni expires, negative when it doesn't expire. The client calls it
    // expiryTime here and secondsToExpiration on a wall item; the info stand counts it down for the
    // infostand.rent.expiration text.
    private Integer secondsToExpiration;
    // Who may use the furni. The client's info stand shows the use button for 2 to everyone and for 1
    // to users with rights; while a room is play tested, the client only uses furni with 2.
    private Integer usagePolicy;
    // The owner's account id (the info stand's ownerId).
    private Integer ownerId;
    // The furni's class name, only on the wire when furniClassId is negative. The client then adds the
    // furni by this name (addObjectFurnitureByName).
    private String staticClass;

    public static FloorItem fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
