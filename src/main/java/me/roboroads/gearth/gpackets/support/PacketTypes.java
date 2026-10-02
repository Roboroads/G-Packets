package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.incoming.AvatarEffect;
import me.roboroads.gearth.gpackets.incoming.CantConnect;
import me.roboroads.gearth.gpackets.incoming.CarryObject;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.CatalogPage;
import me.roboroads.gearth.gpackets.incoming.CatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.incoming.CloseConnection;
import me.roboroads.gearth.gpackets.incoming.ConfigurationItemStates;
import me.roboroads.gearth.gpackets.incoming.Doorbell;
import me.roboroads.gearth.gpackets.incoming.Expression;
import me.roboroads.gearth.gpackets.incoming.FloorHeightMap;
import me.roboroads.gearth.gpackets.incoming.FurnitureAliases;
import me.roboroads.gearth.gpackets.incoming.HandItemReceived;
import me.roboroads.gearth.gpackets.incoming.HeightMap;
import me.roboroads.gearth.gpackets.incoming.HeightMapUpdate;
import me.roboroads.gearth.gpackets.incoming.RoomEntryInfo;
import me.roboroads.gearth.gpackets.incoming.RoomEntryTile;
import me.roboroads.gearth.gpackets.incoming.RoomOccupiedTiles;
import me.roboroads.gearth.gpackets.incoming.RoomProperty;
import me.roboroads.gearth.gpackets.incoming.FlatAccessDenied;
import me.roboroads.gearth.gpackets.incoming.FlatAccessible;
import me.roboroads.gearth.gpackets.incoming.GamePlayerValue;
import me.roboroads.gearth.gpackets.incoming.HandItemReceived;
import me.roboroads.gearth.gpackets.incoming.OpenConnection;
import me.roboroads.gearth.gpackets.incoming.RoomForward;
import me.roboroads.gearth.gpackets.incoming.RoomQueueStatus;
import me.roboroads.gearth.gpackets.incoming.RoomReady;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsData;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaveError;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaved;
import me.roboroads.gearth.gpackets.incoming.RoomVisualizationSettings;
import me.roboroads.gearth.gpackets.incoming.Sleep;
import me.roboroads.gearth.gpackets.incoming.SpecialRoomEffect;
import me.roboroads.gearth.gpackets.incoming.UseObject;
import me.roboroads.gearth.gpackets.incoming.UserChange;
import me.roboroads.gearth.gpackets.incoming.UserRemove;
import me.roboroads.gearth.gpackets.incoming.UserUpdate;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.WiredMovements;
import me.roboroads.gearth.gpackets.incoming.WiredRoomSettings;
import me.roboroads.gearth.gpackets.incoming.YouAreNotSpectator;
import me.roboroads.gearth.gpackets.incoming.YouArePlayingGame;
import me.roboroads.gearth.gpackets.incoming.YouAreSpectator;
import me.roboroads.gearth.gpackets.outgoing.AvatarExpression;
import me.roboroads.gearth.gpackets.outgoing.ChangeMotto;
import me.roboroads.gearth.gpackets.outgoing.ChangePosture;
import me.roboroads.gearth.gpackets.outgoing.ChangeQueue;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.outgoing.ClickCharacter;
import me.roboroads.gearth.gpackets.outgoing.CustomizeAvatarWithFurni;
import me.roboroads.gearth.gpackets.outgoing.DropCarryItem;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogIndex;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPage;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.outgoing.GetFurnitureAliases;
import me.roboroads.gearth.gpackets.outgoing.GetOccupiedTiles;
import me.roboroads.gearth.gpackets.outgoing.GetRoomEntryTile;
import me.roboroads.gearth.gpackets.outgoing.GetRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.LetUserIn;
import me.roboroads.gearth.gpackets.outgoing.LookTo;
import me.roboroads.gearth.gpackets.outgoing.MoveAvatar;
import me.roboroads.gearth.gpackets.outgoing.OpenFlatConnection;
import me.roboroads.gearth.gpackets.outgoing.PassCarryItem;
import me.roboroads.gearth.gpackets.outgoing.PassCarryItemToPet;
import me.roboroads.gearth.gpackets.outgoing.RequestRoomPropertySet;
import me.roboroads.gearth.gpackets.outgoing.Quit;
import me.roboroads.gearth.gpackets.outgoing.RoomNetworkOpenConnection;
import me.roboroads.gearth.gpackets.outgoing.SaveRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.Sign;
import me.roboroads.gearth.gpackets.outgoing.UpdateFloorProperties;
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
            CantConnect.TYPE,
            CarryObject.TYPE,
            CatalogIndex.TYPE,
            CatalogPage.TYPE,
            CatalogPageWithEarliestExpiry.TYPE,
            CatalogPublished.TYPE,
            CloseConnection.TYPE,
            ConfigurationItemStates.TYPE,
            me.roboroads.gearth.gpackets.incoming.Dance.TYPE,
            Doorbell.TYPE,
            Expression.TYPE,
            FloorHeightMap.TYPE,
            FurnitureAliases.TYPE,
            HandItemReceived.TYPE,
            HeightMap.TYPE,
            HeightMapUpdate.TYPE,
            RoomEntryInfo.TYPE,
            RoomEntryTile.TYPE,
            RoomOccupiedTiles.TYPE,
            RoomProperty.TYPE,
            FlatAccessDenied.TYPE,
            FlatAccessible.TYPE,
            GamePlayerValue.TYPE,
            HandItemReceived.TYPE,
            OpenConnection.TYPE,
            RoomForward.TYPE,
            RoomQueueStatus.TYPE,
            RoomReady.TYPE,
            RoomSettingsData.TYPE,
            me.roboroads.gearth.gpackets.incoming.RoomSettingsError.TYPE,
            RoomSettingsSaved.TYPE,
            RoomSettingsSaveError.TYPE,
            RoomVisualizationSettings.TYPE,
            Sleep.TYPE,
            SpecialRoomEffect.TYPE,
            UseObject.TYPE,
            UserChange.TYPE,
            UserRemove.TYPE,
            UserUpdate.TYPE,
            Users.TYPE,
            WiredMovements.TYPE,
            WiredRoomSettings.TYPE,
            YouAreNotSpectator.TYPE,
            YouArePlayingGame.TYPE,
            YouAreSpectator.TYPE,
            AvatarExpression.TYPE,
            ChangeMotto.TYPE,
            ChangePosture.TYPE,
            ChangeQueue.TYPE,
            Chat.TYPE,
            ClickCharacter.TYPE,
            CustomizeAvatarWithFurni.TYPE,
            me.roboroads.gearth.gpackets.outgoing.Dance.TYPE,
            DropCarryItem.TYPE,
            GetCatalogIndex.TYPE,
            GetCatalogPage.TYPE,
            GetCatalogPageWithEarliestExpiry.TYPE,
            GetFurnitureAliases.TYPE,
            GetOccupiedTiles.TYPE,
            GetRoomEntryTile.TYPE,
            GetRoomSettings.TYPE,
            LetUserIn.TYPE,
            LookTo.TYPE,
            MoveAvatar.TYPE,
            OpenFlatConnection.TYPE,
            PassCarryItem.TYPE,
            PassCarryItemToPet.TYPE,
            RequestRoomPropertySet.TYPE,
            Quit.TYPE,
            RoomNetworkOpenConnection.TYPE,
            SaveRoomSettings.TYPE,
            Sign.TYPE,
            UpdateFloorProperties.TYPE,
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
