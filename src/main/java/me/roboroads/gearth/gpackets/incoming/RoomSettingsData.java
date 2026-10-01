package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.incoming.sub.room.RoomModerationSettings;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.List;

/** The settings of a room you can edit, the answer to {@code GetRoomSettings}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsData implements Packet, JsonSerializable {
    public static final PacketType<RoomSettingsData> TYPE = PacketType.of("RoomSettingsData", HMessage.Direction.TOCLIENT, Schema.of(RoomSettingsData.class)
            .integer("roomId")
            .string("name")
            .string("description")
            .integer("doorMode")
            .integer("categoryId")
            .integer("maximumVisitors")
            .integer("maximumVisitorsLimit")
            .list("tags", WireType.STRING)
            .integer("tradeMode")
            .integer("allowPets")
            .integer("allowFoodConsume")
            .integer("allowWalkThrough")
            .integer("hideWalls")
            .integer("wallThickness")
            .integer("floorThickness")
            .integer("chatFloodSensitivity")
            .bool("leaveOnDoorTileEnabled")
            .bool("idleSleepEnabled")
            .integer("idleSleepTimeoutSeconds")
            .bool("idleAutokickEnabled")
            .integer("idleAutokickTimeoutSeconds")
            .bool("muteAllPets")
            .struct("roomModerationSettings", RoomModerationSettings.SCHEMA)
            .bool("hiddenByBc"));

    private Integer roomId;
    private String name;
    private String description;
    // 0 open, 1 doorbell, 2 password, 3 invisible (the client's doormode_* radio buttons).
    // The client also knows 4, which has no button.
    private Integer doorMode;
    private Integer categoryId;
    private Integer maximumVisitors;
    private Integer maximumVisitorsLimit;
    private List<String> tags;
    // Index into the client's trade dropdown: 0 trade_not_allowed, 1 trade_not_with_Controller,
    // 2 trade_allowed (localization keys under navigator.roomsettings).
    private Integer tradeMode;
    // The four flags below are sent as ints: 1 is on, 0 is off. SaveRoomSettings sends them as booleans.
    private Integer allowPets;
    private Integer allowFoodConsume;
    private Integer allowWalkThrough;
    private Integer hideWalls;
    // -2 to 1; the client's thickness dropdown.
    private Integer wallThickness;
    private Integer floorThickness;
    // The client builds its chat settings from this alone (fromFloodSensitivity).
    private Integer chatFloodSensitivity;
    private Boolean leaveOnDoorTileEnabled;
    private Boolean idleSleepEnabled;
    private Integer idleSleepTimeoutSeconds;
    private Boolean idleAutokickEnabled;
    private Integer idleAutokickTimeoutSeconds;
    private Boolean muteAllPets;
    private RoomModerationSettings roomModerationSettings;
    // The client shows doormode_override_info when this is set.
    private Boolean hiddenByBc;

    public static RoomSettingsData fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static RoomSettingsData fromJson(String json) {
        return Json.parse(RoomSettingsData.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
