package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.incoming.AvatarEffect;
import me.roboroads.gearth.gpackets.incoming.CarryObject;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.CatalogPage;
import me.roboroads.gearth.gpackets.incoming.CatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.incoming.Expression;
import me.roboroads.gearth.gpackets.incoming.HandItemReceived;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsData;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaveError;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaved;
import me.roboroads.gearth.gpackets.incoming.Sleep;
import me.roboroads.gearth.gpackets.incoming.UseObject;
import me.roboroads.gearth.gpackets.incoming.UserChange;
import me.roboroads.gearth.gpackets.incoming.UserRemove;
import me.roboroads.gearth.gpackets.incoming.UserUpdate;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.WiredMovements;
import me.roboroads.gearth.gpackets.incoming.WiredRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.AvatarExpression;
import me.roboroads.gearth.gpackets.outgoing.ChangeMotto;
import me.roboroads.gearth.gpackets.outgoing.ChangePosture;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.outgoing.ClickCharacter;
import me.roboroads.gearth.gpackets.outgoing.CustomizeAvatarWithFurni;
import me.roboroads.gearth.gpackets.outgoing.DropCarryItem;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogIndex;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPage;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.outgoing.GetRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.LookTo;
import me.roboroads.gearth.gpackets.outgoing.MoveAvatar;
import me.roboroads.gearth.gpackets.outgoing.PassCarryItem;
import me.roboroads.gearth.gpackets.outgoing.PassCarryItemToPet;
import me.roboroads.gearth.gpackets.outgoing.SaveRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.Sign;
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
    // Also lists packets the client ignores. Those are written out in full instead of imported,
    // because Java 8 warns on the import of a deprecated class and @SuppressWarnings can't reach it.
    // Dance exists in both directions, so both are written out in full too.
    @SuppressWarnings("deprecation")
    private static final List<PacketType<?>> ALL = Collections.unmodifiableList(Arrays.<PacketType<?>>asList(
            AvatarEffect.TYPE,
            CarryObject.TYPE,
            CatalogIndex.TYPE,
            CatalogPage.TYPE,
            CatalogPageWithEarliestExpiry.TYPE,
            CatalogPublished.TYPE,
            me.roboroads.gearth.gpackets.incoming.Dance.TYPE,
            Expression.TYPE,
            HandItemReceived.TYPE,
            RoomSettingsData.TYPE,
            me.roboroads.gearth.gpackets.incoming.RoomSettingsError.TYPE,
            RoomSettingsSaved.TYPE,
            RoomSettingsSaveError.TYPE,
            Sleep.TYPE,
            UseObject.TYPE,
            UserChange.TYPE,
            UserRemove.TYPE,
            UserUpdate.TYPE,
            Users.TYPE,
            WiredMovements.TYPE,
            WiredRoomSettings.TYPE,
            AvatarExpression.TYPE,
            ChangeMotto.TYPE,
            ChangePosture.TYPE,
            Chat.TYPE,
            ClickCharacter.TYPE,
            CustomizeAvatarWithFurni.TYPE,
            me.roboroads.gearth.gpackets.outgoing.Dance.TYPE,
            DropCarryItem.TYPE,
            GetCatalogIndex.TYPE,
            GetCatalogPage.TYPE,
            GetCatalogPageWithEarliestExpiry.TYPE,
            GetRoomSettings.TYPE,
            LookTo.TYPE,
            MoveAvatar.TYPE,
            PassCarryItem.TYPE,
            PassCarryItemToPet.TYPE,
            SaveRoomSettings.TYPE,
            Sign.TYPE,
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
