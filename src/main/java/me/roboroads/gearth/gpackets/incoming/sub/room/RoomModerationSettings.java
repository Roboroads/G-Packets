package me.roboroads.gearth.gpackets.incoming.sub.room;

import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomModerationPermission;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomModerationSettings implements SubPacket, JsonSerializable {
    public static final Schema<RoomModerationSettings> SCHEMA = Schema.of(RoomModerationSettings.class)
            .enumInt("whoCanMute", RoomModerationPermission.class)
            .enumInt("whoCanKick", RoomModerationPermission.class)
            .enumInt("whoCanBan", RoomModerationPermission.class);

    private RoomModerationPermission whoCanMute;
    private RoomModerationPermission whoCanKick;
    private RoomModerationPermission whoCanBan;

    public static RoomModerationSettings fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }
}
