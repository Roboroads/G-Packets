package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * Saves your wired account preferences (WiredMenuController's sendPreferences): from the wired menu's
 * settings tab, the toolbar's other settings and switchPlayTestMode. The server sends them back in
 * AccountPreferences.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class WiredSetPreferences implements Packet, JsonSerializable {
    public static final PacketType<WiredSetPreferences> TYPE = PacketType.of("WiredSetPreferences", HMessage.Direction.TOSERVER, Schema.of(WiredSetPreferences.class)
            .bool("wiredMenuButton")
            .bool("wiredInspectButton")
            .bool("playTestMode")
            .integer("unknownInt4")
            .bool("wiredWhisperDisabled")
            .bool("showAllNotifications")
            .string("uiStyle"));

    // wiredmenu.settings.preferences.toolbar: "Show wired menu in toolbar"
    private Boolean wiredMenuButton;
    // wiredmenu.settings.preferences.inspect_button: "Furni/user inspect button"
    private Boolean wiredInspectButton;
    // wiredmenu.settings.preferences.playtest: "Enable playtesting mode"
    private Boolean playTestMode;
    // No evidence: the client always sends 0. AccountPreferences has an int at the same spot, which
    // its parser reads and drops.
    @Builder.Default
    private Integer unknownInt4 = 0;
    // The disable_wired_whisper_checkbox of the toolbar's other settings (OtherSettingsView).
    private Boolean wiredWhisperDisabled;
    // wiredmenu.settings.preferences.show_all_errors: "Show all system notifications"
    private Boolean showAllNotifications;
    // The wired UI style: "" for the default (illumina), or a name from the style picker, "illumina"
    // or "volter" (UserDefinedRoomEventsCtrl.STYLE_OPTIONS). The client also knows volter_yellow,
    // volter_blue, volter_green and ubuntu, and sends back whatever AccountPreferences said.
    private String uiStyle;

    public static WiredSetPreferences fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static WiredSetPreferences fromJson(String json) {
        return Json.parse(WiredSetPreferences.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
