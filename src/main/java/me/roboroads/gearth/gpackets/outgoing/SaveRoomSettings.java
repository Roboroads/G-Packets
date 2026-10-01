package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.RoomModerationPermission;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/**
 * Saves a room's settings; the server answers with {@code RoomSettingsSaved} or
 * {@code RoomSettingsSaveError}. The order differs from {@code RoomSettingsData}: the maximum
 * visitors come before the category, the moderation settings sit before the flood sensitivity,
 * and the four room flags are booleans here.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SaveRoomSettings implements Packet, JsonSerializable {
    public static final PacketType<SaveRoomSettings> TYPE = PacketType.of("SaveRoomSettings", HMessage.Direction.TOSERVER, Schema.of(SaveRoomSettings.class)
            .integer("roomId")
            .string("name")
            .string("description")
            .integer("doorMode")
            .string("password")
            .integer("maximumVisitors")
            .integer("categoryId")
            .list("tags", WireType.STRING)
            .integer("tradeMode")
            .bool("allowPets")
            .bool("allowFoodConsume")
            .bool("allowWalkThrough")
            .bool("hideWalls")
            .integer("wallThickness")
            .integer("floorThickness")
            .enumInt("whoCanMute", RoomModerationPermission.class)
            .enumInt("whoCanKick", RoomModerationPermission.class)
            .enumInt("whoCanBan", RoomModerationPermission.class)
            .integer("chatFloodSensitivity")
            .bool("leaveOnDoorTileEnabled")
            .bool("idleSleepEnabled")
            .integer("idleSleepTimeoutSeconds")
            .bool("idleAutokickEnabled")
            .integer("idleAutokickTimeoutSeconds")
            .bool("muteAllPets"));

    private Integer roomId;
    private String name;
    private String description;
    // 0 open, 1 doorbell, 2 password, 3 invisible (the client's doormode_* radio buttons).
    private Integer doorMode;
    // The client sends "" unless the door mode is password.
    private String password;
    private Integer maximumVisitors;
    private Integer categoryId;
    private List<String> tags;
    // 0 trade_not_allowed, 1 trade_not_with_Controller, 2 trade_allowed (localization keys under navigator.roomsettings).
    private Integer tradeMode;
    private Boolean allowPets;
    private Boolean allowFoodConsume;
    private Boolean allowWalkThrough;
    private Boolean hideWalls;
    // -2 to 1; the client's thickness dropdown.
    private Integer wallThickness;
    private Integer floorThickness;
    private RoomModerationPermission whoCanMute;
    private RoomModerationPermission whoCanKick;
    private RoomModerationPermission whoCanBan;
    private Integer chatFloodSensitivity;
    private Boolean leaveOnDoorTileEnabled;
    private Boolean idleSleepEnabled;
    private Integer idleSleepTimeoutSeconds;
    private Boolean idleAutokickEnabled;
    private Integer idleAutokickTimeoutSeconds;
    private Boolean muteAllPets;

    public static SaveRoomSettings fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static SaveRoomSettings fromJson(String json) {
        return Json.parse(SaveRoomSettings.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
