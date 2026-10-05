package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.incoming.AuthenticationOK;
import me.roboroads.gearth.gpackets.incoming.AvailabilityStatus;
import me.roboroads.gearth.gpackets.incoming.AvatarEffect;
import me.roboroads.gearth.gpackets.incoming.BannedUsersFromRoom;
import me.roboroads.gearth.gpackets.incoming.BuildersClubFurniCount;
import me.roboroads.gearth.gpackets.incoming.BuildersClubPlacementWarning;
import me.roboroads.gearth.gpackets.incoming.BuildersClubSubscriptionStatus;
import me.roboroads.gearth.gpackets.incoming.CantConnect;
import me.roboroads.gearth.gpackets.incoming.CarryObject;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.CatalogPage;
import me.roboroads.gearth.gpackets.incoming.CatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.incoming.CloseConnection;
import me.roboroads.gearth.gpackets.incoming.ConfigurationItemStates;
import me.roboroads.gearth.gpackets.incoming.DisconnectReason;
import me.roboroads.gearth.gpackets.incoming.Doorbell;
import me.roboroads.gearth.gpackets.incoming.ErrorReport;
import me.roboroads.gearth.gpackets.incoming.Expression;
import me.roboroads.gearth.gpackets.incoming.FlatControllerAdded;
import me.roboroads.gearth.gpackets.incoming.FlatControllerRemoved;
import me.roboroads.gearth.gpackets.incoming.FlatControllers;
import me.roboroads.gearth.gpackets.incoming.FloodControl;
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
import me.roboroads.gearth.gpackets.incoming.GenericError;
import me.roboroads.gearth.gpackets.incoming.GetCustomFilterResult;
import me.roboroads.gearth.gpackets.incoming.IdentityAccounts;
import me.roboroads.gearth.gpackets.incoming.InfoHotelClosed;
import me.roboroads.gearth.gpackets.incoming.InfoHotelClosing;
import me.roboroads.gearth.gpackets.incoming.IsFirstLoginOfDay;
import me.roboroads.gearth.gpackets.incoming.LoginFailedHotelClosed;
import me.roboroads.gearth.gpackets.incoming.MaintenanceStatus;
import me.roboroads.gearth.gpackets.incoming.ModifyCustomFilterResult;
import me.roboroads.gearth.gpackets.incoming.NoobnessLevel;
import me.roboroads.gearth.gpackets.incoming.OpenConnection;
import me.roboroads.gearth.gpackets.incoming.Ping;
import me.roboroads.gearth.gpackets.incoming.RemainingMutePeriod;
import me.roboroads.gearth.gpackets.incoming.RoomChatSettings;
import me.roboroads.gearth.gpackets.incoming.RoomForward;
import me.roboroads.gearth.gpackets.incoming.RoomQueueStatus;
import me.roboroads.gearth.gpackets.incoming.RoomReady;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsData;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaveError;
import me.roboroads.gearth.gpackets.incoming.RoomSettingsSaved;
import me.roboroads.gearth.gpackets.incoming.RoomVisualizationSettings;
import me.roboroads.gearth.gpackets.incoming.Sleep;
import me.roboroads.gearth.gpackets.incoming.SpecialRoomEffect;
import me.roboroads.gearth.gpackets.incoming.SpecialSystemChat;
import me.roboroads.gearth.gpackets.incoming.TradeNftAssetInventory;
import me.roboroads.gearth.gpackets.incoming.TradeNftAssets;
import me.roboroads.gearth.gpackets.incoming.TradeOpenFailed;
import me.roboroads.gearth.gpackets.incoming.TradeSilverFee;
import me.roboroads.gearth.gpackets.incoming.TradeSilverSet;
import me.roboroads.gearth.gpackets.incoming.TradingAccept;
import me.roboroads.gearth.gpackets.incoming.TradingClose;
import me.roboroads.gearth.gpackets.incoming.TradingCompleted;
import me.roboroads.gearth.gpackets.incoming.TradingConfirmation;
import me.roboroads.gearth.gpackets.incoming.TradingItemList;
import me.roboroads.gearth.gpackets.incoming.TradingOpen;
import me.roboroads.gearth.gpackets.incoming.TradingOtherNotAllowed;
import me.roboroads.gearth.gpackets.incoming.TradingYouAreNotAllowed;
import me.roboroads.gearth.gpackets.incoming.UniqueMachineID;
import me.roboroads.gearth.gpackets.incoming.UseObject;
import me.roboroads.gearth.gpackets.incoming.UserChange;
import me.roboroads.gearth.gpackets.incoming.UserNftChatStyles;
import me.roboroads.gearth.gpackets.incoming.UserObject;
import me.roboroads.gearth.gpackets.incoming.UserPurchasableChatStyleChanged;
import me.roboroads.gearth.gpackets.incoming.UserPurchasableChatStyles;
import me.roboroads.gearth.gpackets.incoming.UserRemove;
import me.roboroads.gearth.gpackets.incoming.UserRights;
import me.roboroads.gearth.gpackets.incoming.UserTyping;
import me.roboroads.gearth.gpackets.incoming.UserUnbannedFromRoom;
import me.roboroads.gearth.gpackets.incoming.UserUpdate;
import me.roboroads.gearth.gpackets.incoming.Users;
import me.roboroads.gearth.gpackets.incoming.WiredMovements;
import me.roboroads.gearth.gpackets.incoming.WiredRoomSettings;
import me.roboroads.gearth.gpackets.incoming.YouAreController;
import me.roboroads.gearth.gpackets.incoming.YouAreNotController;
import me.roboroads.gearth.gpackets.incoming.YouAreNotSpectator;
import me.roboroads.gearth.gpackets.incoming.YouAreOwner;
import me.roboroads.gearth.gpackets.incoming.YouArePlayingGame;
import me.roboroads.gearth.gpackets.incoming.YouAreSpectator;
import me.roboroads.gearth.gpackets.outgoing.AcceptTrading;
import me.roboroads.gearth.gpackets.outgoing.AddItemToTrade;
import me.roboroads.gearth.gpackets.outgoing.AddItemsToTrade;
import me.roboroads.gearth.gpackets.outgoing.AddNftToTrade;
import me.roboroads.gearth.gpackets.outgoing.AddToCustomFilter;
import me.roboroads.gearth.gpackets.outgoing.AmbassadorAlert;
import me.roboroads.gearth.gpackets.outgoing.AssignRights;
import me.roboroads.gearth.gpackets.outgoing.AvatarExpression;
import me.roboroads.gearth.gpackets.outgoing.BanUserWithDuration;
import me.roboroads.gearth.gpackets.outgoing.BuildersClubPlaceRoomItem;
import me.roboroads.gearth.gpackets.outgoing.BuildersClubPlaceWallItem;
import me.roboroads.gearth.gpackets.outgoing.BuildersClubQueryFurniCount;
import me.roboroads.gearth.gpackets.outgoing.CancelTyping;
import me.roboroads.gearth.gpackets.outgoing.ChangeMotto;
import me.roboroads.gearth.gpackets.outgoing.ChangePosture;
import me.roboroads.gearth.gpackets.outgoing.ChangeQueue;
import me.roboroads.gearth.gpackets.outgoing.ClickCharacter;
import me.roboroads.gearth.gpackets.outgoing.ClientHello;
import me.roboroads.gearth.gpackets.outgoing.CloseTrading;
import me.roboroads.gearth.gpackets.outgoing.ConfirmAcceptTrading;
import me.roboroads.gearth.gpackets.outgoing.ConfirmDeclineTrading;
import me.roboroads.gearth.gpackets.outgoing.CustomizeAvatarWithFurni;
import me.roboroads.gearth.gpackets.outgoing.Disconnect;
import me.roboroads.gearth.gpackets.outgoing.DropCarryItem;
import me.roboroads.gearth.gpackets.outgoing.GetBannedUsersFromRoom;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogIndex;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPage;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.outgoing.GetCustomFilter;
import me.roboroads.gearth.gpackets.outgoing.GetFlatControllers;
import me.roboroads.gearth.gpackets.outgoing.GetFurnitureAliases;
import me.roboroads.gearth.gpackets.outgoing.GetNftTradeInventory;
import me.roboroads.gearth.gpackets.outgoing.GetOccupiedTiles;
import me.roboroads.gearth.gpackets.outgoing.GetRoomEntryTile;
import me.roboroads.gearth.gpackets.outgoing.GetRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.GetUserNftChatStyles;
import me.roboroads.gearth.gpackets.outgoing.InfoRetrieve;
import me.roboroads.gearth.gpackets.outgoing.KickUser;
import me.roboroads.gearth.gpackets.outgoing.LetUserIn;
import me.roboroads.gearth.gpackets.outgoing.LookTo;
import me.roboroads.gearth.gpackets.outgoing.MoveAvatar;
import me.roboroads.gearth.gpackets.outgoing.MuteUser;
import me.roboroads.gearth.gpackets.outgoing.OpenFlatConnection;
import me.roboroads.gearth.gpackets.outgoing.OpenTrading;
import me.roboroads.gearth.gpackets.outgoing.PassCarryItem;
import me.roboroads.gearth.gpackets.outgoing.PassCarryItemToPet;
import me.roboroads.gearth.gpackets.outgoing.Pong;
import me.roboroads.gearth.gpackets.outgoing.RemoveAllRights;
import me.roboroads.gearth.gpackets.outgoing.RemoveFromCustomFilter;
import me.roboroads.gearth.gpackets.outgoing.RemoveItemFromTrade;
import me.roboroads.gearth.gpackets.outgoing.RemoveNftFromTrade;
import me.roboroads.gearth.gpackets.outgoing.RemoveOwnRoomRightsRoom;
import me.roboroads.gearth.gpackets.outgoing.RemoveRights;
import me.roboroads.gearth.gpackets.outgoing.RequestRoomPropertySet;
import me.roboroads.gearth.gpackets.outgoing.Quit;
import me.roboroads.gearth.gpackets.outgoing.RoomNetworkOpenConnection;
import me.roboroads.gearth.gpackets.outgoing.SSOTicket;
import me.roboroads.gearth.gpackets.outgoing.SaveRoomSettings;
import me.roboroads.gearth.gpackets.outgoing.SetChatStylePreference;
import me.roboroads.gearth.gpackets.outgoing.Sign;
import me.roboroads.gearth.gpackets.outgoing.SilverFee;
import me.roboroads.gearth.gpackets.outgoing.StartTyping;
import me.roboroads.gearth.gpackets.outgoing.UnacceptTrading;
import me.roboroads.gearth.gpackets.outgoing.UnbanUserFromRoom;
import me.roboroads.gearth.gpackets.outgoing.UniqueID;
import me.roboroads.gearth.gpackets.outgoing.UnmuteUser;
import me.roboroads.gearth.gpackets.outgoing.UpdateFloorProperties;
import me.roboroads.gearth.gpackets.outgoing.VersionCheck;
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
    // Chat, Dance, CompleteDiffieHandshake, InitDiffieHandshake, MuteAllInRoom, Shout and Whisper
    // exist in both    // directions, so both are written out in full too.
    @SuppressWarnings("deprecation")
    private static final List<PacketType<?>> ALL = Collections.unmodifiableList(Arrays.<PacketType<?>>asList(
            AuthenticationOK.TYPE,
            AvailabilityStatus.TYPE,
            AvatarEffect.TYPE,
            BannedUsersFromRoom.TYPE,
            BuildersClubFurniCount.TYPE,
            BuildersClubPlacementWarning.TYPE,
            BuildersClubSubscriptionStatus.TYPE,
            CantConnect.TYPE,
            CarryObject.TYPE,
            CatalogIndex.TYPE,
            CatalogPage.TYPE,
            CatalogPageWithEarliestExpiry.TYPE,
            CatalogPublished.TYPE,
            me.roboroads.gearth.gpackets.incoming.Chat.TYPE,
            CloseConnection.TYPE,
            me.roboroads.gearth.gpackets.incoming.CompleteDiffieHandshake.TYPE,
            ConfigurationItemStates.TYPE,
            me.roboroads.gearth.gpackets.incoming.Dance.TYPE,
            DisconnectReason.TYPE,
            Doorbell.TYPE,
            ErrorReport.TYPE,
            Expression.TYPE,
            FlatControllerAdded.TYPE,
            FlatControllerRemoved.TYPE,
            FlatControllers.TYPE,
            FloodControl.TYPE,
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
            GenericError.TYPE,
            GetCustomFilterResult.TYPE,
            IdentityAccounts.TYPE,
            InfoHotelClosed.TYPE,
            InfoHotelClosing.TYPE,
            me.roboroads.gearth.gpackets.incoming.InitDiffieHandshake.TYPE,
            IsFirstLoginOfDay.TYPE,
            LoginFailedHotelClosed.TYPE,
            MaintenanceStatus.TYPE,
            ModifyCustomFilterResult.TYPE,
            me.roboroads.gearth.gpackets.incoming.MuteAllInRoom.TYPE,
            NoobnessLevel.TYPE,
            OpenConnection.TYPE,
            Ping.TYPE,
            RemainingMutePeriod.TYPE,
            RoomChatSettings.TYPE,
            RoomForward.TYPE,
            RoomQueueStatus.TYPE,
            RoomReady.TYPE,
            RoomSettingsData.TYPE,
            me.roboroads.gearth.gpackets.incoming.RoomSettingsError.TYPE,
            RoomSettingsSaved.TYPE,
            RoomSettingsSaveError.TYPE,
            RoomVisualizationSettings.TYPE,
            me.roboroads.gearth.gpackets.incoming.Shout.TYPE,
            Sleep.TYPE,
            SpecialRoomEffect.TYPE,
            SpecialSystemChat.TYPE,
            TradeNftAssetInventory.TYPE,
            TradeNftAssets.TYPE,
            TradeOpenFailed.TYPE,
            TradeSilverFee.TYPE,
            TradeSilverSet.TYPE,
            TradingAccept.TYPE,
            TradingClose.TYPE,
            TradingCompleted.TYPE,
            TradingConfirmation.TYPE,
            TradingItemList.TYPE,
            me.roboroads.gearth.gpackets.incoming.TradingNotOpen.TYPE,
            TradingOpen.TYPE,
            TradingOtherNotAllowed.TYPE,
            TradingYouAreNotAllowed.TYPE,
            UniqueMachineID.TYPE,
            UseObject.TYPE,
            UserChange.TYPE,
            UserNftChatStyles.TYPE,
            UserObject.TYPE,
            UserPurchasableChatStyleChanged.TYPE,
            UserPurchasableChatStyles.TYPE,
            UserRemove.TYPE,
            UserRights.TYPE,
            UserTyping.TYPE,
            UserUnbannedFromRoom.TYPE,
            UserUpdate.TYPE,
            Users.TYPE,
            me.roboroads.gearth.gpackets.incoming.Whisper.TYPE,
            WiredMovements.TYPE,
            WiredRoomSettings.TYPE,
            YouAreController.TYPE,
            YouAreNotController.TYPE,
            YouAreNotSpectator.TYPE,
            YouAreOwner.TYPE,
            YouArePlayingGame.TYPE,
            YouAreSpectator.TYPE,
            AcceptTrading.TYPE,
            AddItemToTrade.TYPE,
            AddItemsToTrade.TYPE,
            AddNftToTrade.TYPE,
            AddToCustomFilter.TYPE,
            AmbassadorAlert.TYPE,
            AssignRights.TYPE,
            AvatarExpression.TYPE,
            BanUserWithDuration.TYPE,
            BuildersClubPlaceRoomItem.TYPE,
            BuildersClubPlaceWallItem.TYPE,
            BuildersClubQueryFurniCount.TYPE,
            CancelTyping.TYPE,
            ChangeMotto.TYPE,
            ChangePosture.TYPE,
            ChangeQueue.TYPE,
            me.roboroads.gearth.gpackets.outgoing.Chat.TYPE,
            ClickCharacter.TYPE,
            ClientHello.TYPE,
            CloseTrading.TYPE,
            me.roboroads.gearth.gpackets.outgoing.CompleteDiffieHandshake.TYPE,
            ConfirmAcceptTrading.TYPE,
            ConfirmDeclineTrading.TYPE,
            CustomizeAvatarWithFurni.TYPE,
            me.roboroads.gearth.gpackets.outgoing.Dance.TYPE,
            Disconnect.TYPE,
            DropCarryItem.TYPE,
            GetBannedUsersFromRoom.TYPE,
            GetCatalogIndex.TYPE,
            GetCatalogPage.TYPE,
            GetCatalogPageWithEarliestExpiry.TYPE,
            GetCustomFilter.TYPE,
            GetFlatControllers.TYPE,
            GetFurnitureAliases.TYPE,
            GetNftTradeInventory.TYPE,
            GetOccupiedTiles.TYPE,
            GetRoomEntryTile.TYPE,
            GetRoomSettings.TYPE,
            GetUserNftChatStyles.TYPE,
            InfoRetrieve.TYPE,
            me.roboroads.gearth.gpackets.outgoing.InitDiffieHandshake.TYPE,
            KickUser.TYPE,
            LetUserIn.TYPE,
            LookTo.TYPE,
            MoveAvatar.TYPE,
            me.roboroads.gearth.gpackets.outgoing.MuteAllInRoom.TYPE,
            MuteUser.TYPE,
            OpenFlatConnection.TYPE,
            OpenTrading.TYPE,
            PassCarryItem.TYPE,
            PassCarryItemToPet.TYPE,
            Pong.TYPE,
            RemoveAllRights.TYPE,
            RemoveFromCustomFilter.TYPE,
            RemoveItemFromTrade.TYPE,
            RemoveNftFromTrade.TYPE,
            RemoveOwnRoomRightsRoom.TYPE,
            RemoveRights.TYPE,
            RequestRoomPropertySet.TYPE,
            Quit.TYPE,
            RoomNetworkOpenConnection.TYPE,
            SSOTicket.TYPE,
            SaveRoomSettings.TYPE,
            SetChatStylePreference.TYPE,
            me.roboroads.gearth.gpackets.outgoing.Shout.TYPE,
            Sign.TYPE,
            SilverFee.TYPE,
            StartTyping.TYPE,
            UnacceptTrading.TYPE,
            UnbanUserFromRoom.TYPE,
            UniqueID.TYPE,
            UnmuteUser.TYPE,
            UpdateFloorProperties.TYPE,
            VersionCheck.TYPE,
            me.roboroads.gearth.gpackets.outgoing.Whisper.TYPE,
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
