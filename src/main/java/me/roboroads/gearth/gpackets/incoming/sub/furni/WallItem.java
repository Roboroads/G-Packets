package me.roboroads.gearth.gpackets.incoming.sub.furni;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * A wall furni in the room, as the Items, ItemAdd and ItemUpdate parsers read it (the client's
 * parseItemData). Its owner's name isn't part of it: Items sends the names in a list before the furni,
 * and ItemAdd after it.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WallItem implements SubPacket, JsonSerializable {
    public static final Schema<WallItem> SCHEMA = Schema.of(WallItem.class)
            .string("furniId")
            .integer("furniClassId")
            .string("location")
            .string("data")
            .integer("secondsToExpiration")
            .integer("usagePolicy")
            .integer("ownerId");

    // The furni's id in the room, sent as a string; the client reads it with int(). The client logs it
    // as wallItemId.
    private String furniId;
    // The furni's type in the furni data, like FurniProduct.furniClassId. The client calls it type and
    // logs it as wallItemTypeId.
    private Integer furniClassId;
    // Where the furni hangs. ":w=3,5 l=12,30 r" is wall position 3,5, offset 12,30 on the wall, facing
    // "r" or "l". The client also reads an old format: "rightwall" or "frontwall" (then it faces "r",
    // otherwise "l"), a space, then comma-separated numbers.
    private String location;
    // The furni's state data. The client keeps it as a legacy stuff data string (like
    // LegacyStuffData.legacyString) and reads the state number from it when it is numeric.
    // ItemStateUpdate and ItemsStateUpdate change it. A stickie's text is separate: see ItemDataUpdate.
    private String data;
    // Seconds until a rented furni expires, negative when it doesn't expire.
    private Integer secondsToExpiration;
    // Who may use the furni. The client's info stand shows the use button for 2 to everyone and for 1
    // to users with rights.
    private Integer usagePolicy;
    // The owner's account id.
    private Integer ownerId;

    public static WallItem fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
