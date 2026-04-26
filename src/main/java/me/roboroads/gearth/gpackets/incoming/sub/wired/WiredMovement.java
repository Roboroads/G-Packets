package me.roboroads.gearth.gpackets.incoming.sub.wired;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;

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
    private WiredMovementType movementType;

    public static WiredMovement fromPacket(HPacket packet) {
        WiredMovementType movementType = WiredMovementType.fromValue(packet.readInteger());

        if (movementType == null) {
            throw new IllegalArgumentException("Unknown wired movement type");
        }

        switch (movementType) {
            case USER_MOVE:
                return UserMove.fromPacket(packet, movementType);
            case FURNI_MOVE:
                return FurniMove.fromPacket(packet, movementType);
            case WALL_ITEM_MOVE:
                return WallItemMove.fromPacket(packet, movementType);
            case USER_DIRECTION_UPDATE:
                return UserDirectionUpdate.fromPacket(packet, movementType);
            default:
                throw new IllegalArgumentException("Unknown wired movement type: " + movementType);
        }
    }

    @Override
    public void appendPacket(HPacket packet) {
        packet.appendInt(movementType != null ? movementType.value() : -1);
    }
}
