package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.furni.SlideObject;
import me.roboroads.gearth.gpackets.model.enums.SlideUserMoveType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import java.util.Arrays;
import java.util.List;

/**
 * A roller moves the furni on it, and maybe one user, from one tile to the next. The user part is
 * optional: the client reads it only when the packet has bytes left.
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SlideObjectBundle implements Packet, JsonSerializable {
    public static final PacketType<SlideObjectBundle> TYPE = PacketType.of("SlideObjectBundle", HMessage.Direction.TOCLIENT, Schema.of(SlideObjectBundle.class)
            .integer("oldX")
            .integer("oldY")
            .integer("newX")
            .integer("newY")
            .list("objects", SlideObject.SCHEMA)
            .integer("rollerId")
            .optional(o -> o
                    .enumInt("userMoveType", SlideUserMoveType.class)
                    .whenOneOf("userMoveType", Arrays.asList(SlideUserMoveType.WALK, SlideUserMoveType.SLIDE), u -> u
                            .integer("userIndex")
                            .string("userOldZ")
                            .string("userNewZ"))));

    // The tile everything moves from. G-Rust's name; the client builds each move's start from it.
    private Integer oldX;
    private Integer oldY;
    // The tile everything moves to. G-Rust's name; the client builds each move's target from it.
    private Integer newX;
    private Integer newY;
    // The furni that move. The client calls the list objectList.
    private List<SlideObject> objects;
    // The roller's furni id in the room. The client calls it id, and sets that furni's state to 1, then
    // 2, which plays the roller's animation.
    private Integer rollerId;
    // Whether a user moves along, and how. Only on the wire when the packet has bytes left.
    private SlideUserMoveType userMoveType;
    // The user's room index (User.userIndex), not their account id: the client moves the room object
    // with this id. Only on the wire for WALK and SLIDE.
    private Integer userIndex;
    // The height the user starts at, as a decimal string. Only on the wire for WALK and SLIDE.
    private String userOldZ;
    // The height the user ends at, as a decimal string. Only on the wire for WALK and SLIDE.
    private String userNewZ;

    public static SlideObjectBundle fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SlideObjectBundle fromJson(String json) {
        return Json.parse(SlideObjectBundle.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
