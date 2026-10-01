package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.CatalogPage;
import me.roboroads.gearth.gpackets.incoming.CatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsData;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsError;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaveError;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaved;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.WiredMovements;
import me.roboroads.gearth.gpackets.incoming.WiredRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogIndex;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPage;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.outgoing.GetRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.SaveRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.WiredGetRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.WiredSetRoomSettings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Every packet type G-Packets implements. A packet class only creates its {@code TYPE} once the
 * class is loaded, so this list is kept by hand; {@code PacketImplementationTest} fails when a
 * packet is missing from it.
 */
public final class PacketTypes {
    private static final List<PacketType<?>> ALL = Collections.unmodifiableList(Arrays.<PacketType<?>>asList(
            CatalogIndex.TYPE,
            CatalogPage.TYPE,
            CatalogPageWithEarliestExpiry.TYPE,
            CatalogPublished.TYPE,
            RoomSettingsData.TYPE,
            RoomSettingsError.TYPE,
            RoomSettingsSaved.TYPE,
            RoomSettingsSaveError.TYPE,
            Users.TYPE,
            WiredMovements.TYPE,
            WiredRoomSettings.TYPE,
            Chat.TYPE,
            GetCatalogIndex.TYPE,
            GetCatalogPage.TYPE,
            GetCatalogPageWithEarliestExpiry.TYPE,
            GetRoomSettings.TYPE,
            SaveRoomSettings.TYPE,
            WiredGetRoomSettings.TYPE,
            WiredSetRoomSettings.TYPE
    ));

    private PacketTypes() {
    }

    /** Every implemented packet type. */
    public static List<PacketType<?>> all() {
        return ALL;
    }

    /** The packet type with this direction and header name, if G-Packets implements it. */
    public static Optional<PacketType<?>> find(HMessage.Direction direction, String header) {
        for (PacketType<?> type : ALL) {
            if (type.direction() == direction && type.header().equals(header)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
