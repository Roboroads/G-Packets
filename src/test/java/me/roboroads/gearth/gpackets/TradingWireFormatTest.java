package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
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
import me.roboroads.gearth.gpackets.incoming.sub.furni.LegacyStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.MapStuffData;
import me.roboroads.gearth.gpackets.incoming.sub.furni.MapStuffDataEntry;
import me.roboroads.gearth.gpackets.incoming.sub.furni.StuffData;
import me.roboroads.gearth.gpackets.incoming.sub.trading.TradeItem;
import me.roboroads.gearth.gpackets.incoming.sub.trading.TradeNftAsset;
import me.roboroads.gearth.gpackets.model.enums.TradeOpenFailedReason;
import me.roboroads.gearth.gpackets.model.enums.TradingCloseReason;
import me.roboroads.gearth.gpackets.outgoing.AcceptTrading;
import me.roboroads.gearth.gpackets.outgoing.AddItemToTrade;
import me.roboroads.gearth.gpackets.outgoing.AddItemsToTrade;
import me.roboroads.gearth.gpackets.outgoing.AddNftToTrade;
import me.roboroads.gearth.gpackets.outgoing.CloseTrading;
import me.roboroads.gearth.gpackets.outgoing.ConfirmAcceptTrading;
import me.roboroads.gearth.gpackets.outgoing.ConfirmDeclineTrading;
import me.roboroads.gearth.gpackets.outgoing.GetNftTradeInventory;
import me.roboroads.gearth.gpackets.outgoing.OpenTrading;
import me.roboroads.gearth.gpackets.outgoing.RemoveItemFromTrade;
import me.roboroads.gearth.gpackets.outgoing.RemoveNftFromTrade;
import me.roboroads.gearth.gpackets.outgoing.SilverFee;
import me.roboroads.gearth.gpackets.outgoing.UnacceptTrading;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("deprecation") // tests the unused TradingNotOpen
class TradingWireFormatTest {

    // ---- shared samples ----

    static TradeNftAsset nftAsset() {
        return new TradeNftAsset(9000000001L, (short) 1, "4013", 7, "", Arrays.asList(3, 4), "nft_chair_gold", "rare");
    }

    static void appendNftAsset(HPacket p) {
        p.appendLong(9000000001L).appendShort((short) 1).appendString("4013").appendInt(7).appendString("")
                .appendInt(2).appendInt(3).appendInt(4)
                .appendString("nft_chair_gold").appendString("rare");
    }

    // A limited edition floor furni with a state string.
    static TradeItem floorItem() {
        return new TradeItem(101, "S", 5001, 230, 1, true,
                LegacyStuffData.builder().typeAndFlags(StuffData.UNIQUE_SERIAL_FLAG).legacyString("1")
                        .uniqueSerialNumber(12).uniqueSeriesSize(500).build(),
                2, 10, 2026, 0);
    }

    // A wall furni with map data: no extra on the wire.
    static TradeItem wallItem() {
        return new TradeItem(102, "I", 5002, 4001, 1, false,
                MapStuffData.builder().typeAndFlags(1)
                        .entries(Collections.singletonList(new MapStuffDataEntry("state", "0"))).build(),
                3, 9, 2025, null);
    }

    static void appendFloorItem(HPacket p) {
        p.appendInt(101).appendString("S").appendInt(5001).appendInt(230).appendInt(1).appendBoolean(true)
                .appendInt(256).appendString("1").appendInt(12).appendInt(500)
                .appendInt(2).appendInt(10).appendInt(2026)
                .appendInt(0);
    }

    static void appendWallItem(HPacket p) {
        p.appendInt(102).appendString("I").appendInt(5002).appendInt(4001).appendInt(1).appendBoolean(false)
                .appendInt(1).appendInt(1).appendString("state").appendString("0")
                .appendInt(3).appendInt(9).appendInt(2025);
    }

    // ---- TradeNftAssetInventory ----

    static HPacket tradeNftAssetInventoryPacket() {
        HPacket p = new HPacket("TradeNftAssetInventory", HMessage.Direction.TOCLIENT);
        p.appendInt(1);
        appendNftAsset(p);
        return p;
    }

    @Test
    void tradeNftAssetInventoryToPacket() {
        assertSameBytes(tradeNftAssetInventoryPacket(), new TradeNftAssetInventory(Collections.singletonList(nftAsset())).toPacket());
    }

    @Test
    void tradeNftAssetInventoryFromPacket() {
        assertEquals(new TradeNftAssetInventory(Collections.singletonList(nftAsset())),
                TradeNftAssetInventory.fromPacket(tradeNftAssetInventoryPacket()));
    }

    // ---- TradeNftAssets ----

    static HPacket tradeNftAssetsPacket() {
        HPacket p = new HPacket("TradeNftAssets", HMessage.Direction.TOCLIENT);
        p.appendInt(1);
        appendNftAsset(p);
        p.appendInt(0);
        return p;
    }

    @Test
    void tradeNftAssetsToPacket() {
        assertSameBytes(tradeNftAssetsPacket(),
                new TradeNftAssets(Collections.singletonList(nftAsset()), Collections.emptyList()).toPacket());
    }

    @Test
    void tradeNftAssetsFromPacket() {
        assertEquals(new TradeNftAssets(Collections.singletonList(nftAsset()), Collections.emptyList()),
                TradeNftAssets.fromPacket(tradeNftAssetsPacket()));
    }

    // ---- TradeOpenFailed ----

    static HPacket tradeOpenFailedPacket() {
        HPacket p = new HPacket("TradeOpenFailed", HMessage.Direction.TOCLIENT);
        p.appendInt(8).appendString("Roboroads");
        return p;
    }

    @Test
    void tradeOpenFailedToPacket() {
        assertSameBytes(tradeOpenFailedPacket(),
                new TradeOpenFailed(TradeOpenFailedReason.OTHER_USER_ALREADY_TRADING, "Roboroads").toPacket());
    }

    @Test
    void tradeOpenFailedFromPacket() {
        assertEquals(new TradeOpenFailed(TradeOpenFailedReason.OTHER_USER_ALREADY_TRADING, "Roboroads"),
                TradeOpenFailed.fromPacket(tradeOpenFailedPacket()));
    }

    @Test
    void tradeOpenFailedKeepsAnUnnamedReason() {
        HPacket p = new HPacket("TradeOpenFailed", HMessage.Direction.TOCLIENT);
        p.appendInt(5).appendString("Roboroads");
        TradeOpenFailed parsed = TradeOpenFailed.fromPacket(p);
        assertEquals(5, parsed.reason().value());
        assertFalse(parsed.reason().known());
        assertSameBytes(p, parsed.toPacket());
    }

    // ---- TradeSilverFee ----

    static HPacket tradeSilverFeePacket() {
        HPacket p = new HPacket("TradeSilverFee", HMessage.Direction.TOCLIENT);
        p.appendInt(25);
        return p;
    }

    @Test
    void tradeSilverFeeToPacket() {
        assertSameBytes(tradeSilverFeePacket(), new TradeSilverFee(25).toPacket());
    }

    @Test
    void tradeSilverFeeFromPacket() {
        assertEquals(new TradeSilverFee(25), TradeSilverFee.fromPacket(tradeSilverFeePacket()));
    }

    // ---- TradeSilverSet ----

    static HPacket tradeSilverSetPacket() {
        HPacket p = new HPacket("TradeSilverSet", HMessage.Direction.TOCLIENT);
        p.appendInt(10).appendInt(15);
        return p;
    }

    @Test
    void tradeSilverSetToPacket() {
        assertSameBytes(tradeSilverSetPacket(), new TradeSilverSet(10, 15).toPacket());
    }

    @Test
    void tradeSilverSetFromPacket() {
        assertEquals(new TradeSilverSet(10, 15), TradeSilverSet.fromPacket(tradeSilverSetPacket()));
    }

    // ---- TradingAccept ----

    static HPacket tradingAcceptPacket() {
        HPacket p = new HPacket("TradingAccept", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendInt(1);
        return p;
    }

    @Test
    void tradingAcceptToPacket() {
        assertSameBytes(tradingAcceptPacket(), new TradingAccept(12345, 1).toPacket());
    }

    @Test
    void tradingAcceptFromPacket() {
        assertEquals(new TradingAccept(12345, 1), TradingAccept.fromPacket(tradingAcceptPacket()));
    }

    // ---- TradingClose ----

    static HPacket tradingClosePacket() {
        HPacket p = new HPacket("TradingClose", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendInt(1);
        return p;
    }

    @Test
    void tradingCloseToPacket() {
        assertSameBytes(tradingClosePacket(), new TradingClose(12345, TradingCloseReason.COMMIT_ERROR).toPacket());
    }

    @Test
    void tradingCloseFromPacket() {
        assertEquals(new TradingClose(12345, TradingCloseReason.COMMIT_ERROR), TradingClose.fromPacket(tradingClosePacket()));
    }

    @Test
    void tradingCloseKeepsAnUnnamedReason() {
        HPacket p = new HPacket("TradingClose", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendInt(0);
        TradingClose parsed = TradingClose.fromPacket(p);
        assertEquals(0, parsed.reason().value());
        assertFalse(parsed.reason().known());
        assertSameBytes(p, parsed.toPacket());
    }

    // ---- TradingCompleted ----

    static HPacket tradingCompletedPacket() {
        return new HPacket("TradingCompleted", HMessage.Direction.TOCLIENT);
    }

    @Test
    void tradingCompletedToPacket() {
        assertSameBytes(tradingCompletedPacket(), new TradingCompleted().toPacket());
    }

    @Test
    void tradingCompletedFromPacket() {
        assertEquals(new TradingCompleted(), TradingCompleted.fromPacket(tradingCompletedPacket()));
    }

    // ---- TradingConfirmation ----

    static HPacket tradingConfirmationPacket() {
        return new HPacket("TradingConfirmation", HMessage.Direction.TOCLIENT);
    }

    @Test
    void tradingConfirmationToPacket() {
        assertSameBytes(tradingConfirmationPacket(), new TradingConfirmation().toPacket());
    }

    @Test
    void tradingConfirmationFromPacket() {
        assertEquals(new TradingConfirmation(), TradingConfirmation.fromPacket(tradingConfirmationPacket()));
    }

    // ---- TradingItemList ----

    static TradingItemList tradingItemList() {
        return new TradingItemList(12345, Arrays.asList(floorItem(), wallItem()), 2, 0,
                67890, Collections.emptyList(), 0, 50);
    }

    static HPacket tradingItemListPacket() {
        HPacket p = new HPacket("TradingItemList", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendInt(2);
        appendFloorItem(p);
        appendWallItem(p);
        p.appendInt(2).appendInt(0);
        p.appendInt(67890).appendInt(0).appendInt(0).appendInt(50);
        return p;
    }

    @Test
    void tradingItemListToPacket() {
        assertSameBytes(tradingItemListPacket(), tradingItemList().toPacket());
    }

    @Test
    void tradingItemListFromPacket() {
        assertEquals(tradingItemList(), TradingItemList.fromPacket(tradingItemListPacket()));
    }

    @Test
    void aTradeItemOnlySendsExtraForFloorFurni() {
        TradeItem wallWithExtra = wallItem().extra(7);
        HPacket out = new HPacket("Test", HMessage.Direction.TOCLIENT);
        wallWithExtra.appendPacket(out);

        HPacket expected = new HPacket("Test", HMessage.Direction.TOCLIENT);
        appendWallItem(expected);
        assertSameBytes(expected, out);
    }

    // ---- TradingNotOpen ----

    static HPacket tradingNotOpenPacket() {
        return new HPacket("TradingNotOpen", HMessage.Direction.TOCLIENT);
    }

    @Test
    void tradingNotOpenToPacket() {
        assertSameBytes(tradingNotOpenPacket(), new me.roboroads.gearth.gpackets.incoming.TradingNotOpen().toPacket());
    }

    @Test
    void tradingNotOpenFromPacket() {
        assertEquals(new me.roboroads.gearth.gpackets.incoming.TradingNotOpen(),
                me.roboroads.gearth.gpackets.incoming.TradingNotOpen.fromPacket(tradingNotOpenPacket()));
    }

    // ---- TradingOpen ----

    static HPacket tradingOpenPacket() {
        HPacket p = new HPacket("TradingOpen", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendInt(1).appendInt(67890).appendInt(0);
        return p;
    }

    @Test
    void tradingOpenToPacket() {
        assertSameBytes(tradingOpenPacket(), new TradingOpen(12345, 1, 67890, 0).toPacket());
    }

    @Test
    void tradingOpenFromPacket() {
        assertEquals(new TradingOpen(12345, 1, 67890, 0), TradingOpen.fromPacket(tradingOpenPacket()));
    }

    // ---- TradingOtherNotAllowed ----

    static HPacket tradingOtherNotAllowedPacket() {
        return new HPacket("TradingOtherNotAllowed", HMessage.Direction.TOCLIENT);
    }

    @Test
    void tradingOtherNotAllowedToPacket() {
        assertSameBytes(tradingOtherNotAllowedPacket(), new TradingOtherNotAllowed().toPacket());
    }

    @Test
    void tradingOtherNotAllowedFromPacket() {
        assertEquals(new TradingOtherNotAllowed(), TradingOtherNotAllowed.fromPacket(tradingOtherNotAllowedPacket()));
    }

    // ---- TradingYouAreNotAllowed ----

    static HPacket tradingYouAreNotAllowedPacket() {
        return new HPacket("TradingYouAreNotAllowed", HMessage.Direction.TOCLIENT);
    }

    @Test
    void tradingYouAreNotAllowedToPacket() {
        assertSameBytes(tradingYouAreNotAllowedPacket(), new TradingYouAreNotAllowed().toPacket());
    }

    @Test
    void tradingYouAreNotAllowedFromPacket() {
        assertEquals(new TradingYouAreNotAllowed(), TradingYouAreNotAllowed.fromPacket(tradingYouAreNotAllowedPacket()));
    }

    // ---- AcceptTrading ----

    static HPacket acceptTradingPacket() {
        return new HPacket("AcceptTrading", HMessage.Direction.TOSERVER);
    }

    @Test
    void acceptTradingToPacket() {
        assertSameBytes(acceptTradingPacket(), new AcceptTrading().toPacket());
    }

    @Test
    void acceptTradingFromPacket() {
        assertEquals(new AcceptTrading(), AcceptTrading.fromPacket(acceptTradingPacket()));
    }

    // ---- AddItemToTrade ----

    static HPacket addItemToTradePacket() {
        HPacket p = new HPacket("AddItemToTrade", HMessage.Direction.TOSERVER);
        p.appendInt(101);
        return p;
    }

    @Test
    void addItemToTradeToPacket() {
        assertSameBytes(addItemToTradePacket(), new AddItemToTrade(101).toPacket());
    }

    @Test
    void addItemToTradeFromPacket() {
        assertEquals(new AddItemToTrade(101), AddItemToTrade.fromPacket(addItemToTradePacket()));
    }

    // ---- AddItemsToTrade ----

    static HPacket addItemsToTradePacket() {
        HPacket p = new HPacket("AddItemsToTrade", HMessage.Direction.TOSERVER);
        p.appendInt(3).appendInt(101).appendInt(102).appendInt(103);
        return p;
    }

    @Test
    void addItemsToTradeToPacket() {
        assertSameBytes(addItemsToTradePacket(), new AddItemsToTrade(Arrays.asList(101, 102, 103)).toPacket());
    }

    @Test
    void addItemsToTradeFromPacket() {
        assertEquals(new AddItemsToTrade(Arrays.asList(101, 102, 103)), AddItemsToTrade.fromPacket(addItemsToTradePacket()));
    }

    @Test
    void addItemsToTradeRefusesMoreThan1500Items() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < 1501; i++) {
            ids.add(i);
        }

        LimitException e = assertThrows(LimitException.class, new AddItemsToTrade(ids)::toPacket);

        assertEquals("AddItemsToTrade breaks 1 limit (use toPacketUnchecked() to send it anyway):\n"
                + "  itemIds: at most 1500 items, got 1501", e.getMessage());
    }

    // ---- AddNftToTrade ----

    static HPacket addNftToTradePacket() {
        HPacket p = new HPacket("AddNftToTrade", HMessage.Direction.TOSERVER);
        p.appendInt(2).appendInt(77).appendInt(78);
        return p;
    }

    @Test
    void addNftToTradeToPacket() {
        assertSameBytes(addNftToTradePacket(), new AddNftToTrade(Arrays.asList(77, 78)).toPacket());
    }

    @Test
    void addNftToTradeFromPacket() {
        assertEquals(new AddNftToTrade(Arrays.asList(77, 78)), AddNftToTrade.fromPacket(addNftToTradePacket()));
    }

    // ---- CloseTrading ----

    static HPacket closeTradingPacket() {
        return new HPacket("CloseTrading", HMessage.Direction.TOSERVER);
    }

    @Test
    void closeTradingToPacket() {
        assertSameBytes(closeTradingPacket(), new CloseTrading().toPacket());
    }

    @Test
    void closeTradingFromPacket() {
        assertEquals(new CloseTrading(), CloseTrading.fromPacket(closeTradingPacket()));
    }

    // ---- ConfirmAcceptTrading ----

    static HPacket confirmAcceptTradingPacket() {
        return new HPacket("ConfirmAcceptTrading", HMessage.Direction.TOSERVER);
    }

    @Test
    void confirmAcceptTradingToPacket() {
        assertSameBytes(confirmAcceptTradingPacket(), new ConfirmAcceptTrading().toPacket());
    }

    @Test
    void confirmAcceptTradingFromPacket() {
        assertEquals(new ConfirmAcceptTrading(), ConfirmAcceptTrading.fromPacket(confirmAcceptTradingPacket()));
    }

    // ---- ConfirmDeclineTrading ----

    static HPacket confirmDeclineTradingPacket() {
        return new HPacket("ConfirmDeclineTrading", HMessage.Direction.TOSERVER);
    }

    @Test
    void confirmDeclineTradingToPacket() {
        assertSameBytes(confirmDeclineTradingPacket(), new ConfirmDeclineTrading().toPacket());
    }

    @Test
    void confirmDeclineTradingFromPacket() {
        assertEquals(new ConfirmDeclineTrading(), ConfirmDeclineTrading.fromPacket(confirmDeclineTradingPacket()));
    }

    // ---- GetNftTradeInventory ----

    static HPacket getNftTradeInventoryPacket() {
        return new HPacket("GetNftTradeInventory", HMessage.Direction.TOSERVER);
    }

    @Test
    void getNftTradeInventoryToPacket() {
        assertSameBytes(getNftTradeInventoryPacket(), new GetNftTradeInventory().toPacket());
    }

    @Test
    void getNftTradeInventoryFromPacket() {
        assertEquals(new GetNftTradeInventory(), GetNftTradeInventory.fromPacket(getNftTradeInventoryPacket()));
    }

    // ---- OpenTrading ----

    static HPacket openTradingPacket() {
        HPacket p = new HPacket("OpenTrading", HMessage.Direction.TOSERVER);
        p.appendInt(3);
        return p;
    }

    @Test
    void openTradingToPacket() {
        assertSameBytes(openTradingPacket(), new OpenTrading(3).toPacket());
    }

    @Test
    void openTradingFromPacket() {
        assertEquals(new OpenTrading(3), OpenTrading.fromPacket(openTradingPacket()));
    }

    // ---- RemoveItemFromTrade ----

    static HPacket removeItemFromTradePacket() {
        HPacket p = new HPacket("RemoveItemFromTrade", HMessage.Direction.TOSERVER);
        p.appendInt(101);
        return p;
    }

    @Test
    void removeItemFromTradeToPacket() {
        assertSameBytes(removeItemFromTradePacket(), new RemoveItemFromTrade(101).toPacket());
    }

    @Test
    void removeItemFromTradeFromPacket() {
        assertEquals(new RemoveItemFromTrade(101), RemoveItemFromTrade.fromPacket(removeItemFromTradePacket()));
    }

    // ---- RemoveNftFromTrade ----

    static HPacket removeNftFromTradePacket() {
        HPacket p = new HPacket("RemoveNftFromTrade", HMessage.Direction.TOSERVER);
        p.appendInt(77);
        return p;
    }

    @Test
    void removeNftFromTradeToPacket() {
        assertSameBytes(removeNftFromTradePacket(), new RemoveNftFromTrade(77).toPacket());
    }

    @Test
    void removeNftFromTradeFromPacket() {
        assertEquals(new RemoveNftFromTrade(77), RemoveNftFromTrade.fromPacket(removeNftFromTradePacket()));
    }

    // ---- SilverFee ----

    static HPacket silverFeePacket() {
        HPacket p = new HPacket("SilverFee", HMessage.Direction.TOSERVER);
        p.appendBoolean(true);
        return p;
    }

    @Test
    void silverFeeToPacket() {
        assertSameBytes(silverFeePacket(), new SilverFee(true).toPacket());
    }

    @Test
    void silverFeeFromPacket() {
        assertEquals(new SilverFee(true), SilverFee.fromPacket(silverFeePacket()));
    }

    // ---- UnacceptTrading ----

    static HPacket unacceptTradingPacket() {
        return new HPacket("UnacceptTrading", HMessage.Direction.TOSERVER);
    }

    @Test
    void unacceptTradingToPacket() {
        assertSameBytes(unacceptTradingPacket(), new UnacceptTrading().toPacket());
    }

    @Test
    void unacceptTradingFromPacket() {
        assertEquals(new UnacceptTrading(), UnacceptTrading.fromPacket(unacceptTradingPacket()));
    }
}
