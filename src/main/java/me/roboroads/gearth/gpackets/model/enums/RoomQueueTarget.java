package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.roboroads.gearth.gpackets.support.schema.IntEnum;

/**
 * Which room queue: the one for spectators or the one for visitors. The client declares these two
 * (RoomSessionQueueEvent), shows a queue set's status per target (RoomQueueWidgetHandler), and its
 * queue window sends the same values to switch queues.
 */
@RequiredArgsConstructor
public enum RoomQueueTarget implements IntEnum {
    // RWRQM_CHANGE_TO_SPECTATOR_QUEUE / RWRQUE_SPECTATOR_QUEUE_STATUS
    SPECTATOR(1),
    // RWRQM_CHANGE_TO_VISITOR_QUEUE / RWRQUE_VISITOR_QUEUE_STATUS
    VISITOR(2);

    @Getter
    @JsonValue
    private final int value;

    public static RoomQueueTarget fromValue(int value) {
        for (RoomQueueTarget target : values()) {
            if (target.value == value) {
                return target;
            }
        }
        return null;
    }
}
