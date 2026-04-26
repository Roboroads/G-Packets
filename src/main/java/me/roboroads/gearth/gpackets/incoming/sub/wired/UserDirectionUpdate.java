package me.roboroads.gearth.gpackets.incoming.sub.wired;

import gearth.protocol.HPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.model.enums.WiredMovementType;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class UserDirectionUpdate extends WiredMovement {
    private Integer userIndex;
    private Direction bodyDirection;
    private Direction headDirection;

    public static UserDirectionUpdate fromPacket(HPacket packet, WiredMovementType movementType) {
        return UserDirectionUpdate.builder()
                .movementType(movementType)
                .userIndex(packet.readInteger())
                .bodyDirection(Direction.fromValue(packet.readInteger()))
                .headDirection(Direction.fromValue(packet.readInteger()))
                .build();
    }

    @Override
    public void appendPacket(HPacket packet) {
        super.appendPacket(packet);
        packet.appendInt(userIndex);
        packet.appendInt(bodyDirection != null ? bodyDirection.value() : -1);
        packet.appendInt(headDirection != null ? headDirection.value() : -1);
    }
}
