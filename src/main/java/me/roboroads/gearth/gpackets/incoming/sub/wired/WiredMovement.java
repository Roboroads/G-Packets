package me.roboroads.gearth.gpackets.incoming.sub.wired;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

@CheckedAgainst("WIN63-202609091217-117204808")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "movementType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserMove.class, name = "0"),
        @JsonSubTypes.Type(value = FurniMove.class, name = "1"),
        @JsonSubTypes.Type(value = WallItemMove.class, name = "2"),
        @JsonSubTypes.Type(value = UserDirectionUpdate.class, name = "3")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class WiredMovement implements SubPacket, JsonSerializable {
    public static final Schema<WiredMovement> SCHEMA = Schema.of(WiredMovement.class)
            .enumInt("movementType", WiredMovementType.class)
            .branch("movementType", cases -> cases
                    .on(WiredMovementType.USER_MOVE, UserMove.class, s -> s
                            .integer("sourceX")
                            .integer("sourceY")
                            .integer("targetX")
                            .integer("targetY")
                            .string("sourceZ")
                            .string("targetZ")
                            .integer("userIndex")
                            .integer("isSlide")
                            .integer("animationTime")
                            .enumInt("bodyDirection", Direction.class)
                            .enumInt("headDirection", Direction.class)
                            .bool("hasJump")
                            .when("hasJump", true, j -> j.integer("jumpPower")))
                    .on(WiredMovementType.FURNI_MOVE, FurniMove.class, s -> s
                            .integer("sourceX")
                            .integer("sourceY")
                            .integer("targetX")
                            .integer("targetY")
                            .string("sourceZ")
                            .string("targetZ")
                            .integer("furniId")
                            .integer("animationTime")
                            .enumInt("rotation", Direction.class)
                            .bool("hasOvershoot")
                            .when("hasOvershoot", true, o -> o.integer("overshootingDistance"))
                            .bool("hasCurve")
                            .when("hasCurve", true, c -> c.integer("curveStrength")))
                    .on(WiredMovementType.WALL_ITEM_MOVE, WallItemMove.class, s -> s
                            .integer("furniId")
                            .bool("isDirectionRight")
                            .integer("oldWallX")
                            .integer("oldWallY")
                            .integer("oldOffsetX")
                            .integer("oldOffsetY")
                            .integer("newWallX")
                            .integer("newWallY")
                            .integer("newOffsetX")
                            .integer("newOffsetY")
                            .integer("animationTime"))
                    .on(WiredMovementType.USER_DIRECTION_UPDATE, UserDirectionUpdate.class, s -> s
                            .integer("userIndex")
                            .enumInt("bodyDirection", Direction.class)
                            .enumInt("headDirection", Direction.class)));

    private WiredMovementType movementType;

    public static WiredMovement fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
