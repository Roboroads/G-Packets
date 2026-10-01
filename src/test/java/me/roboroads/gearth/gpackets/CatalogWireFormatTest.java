package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.incoming.CatalogPage;
import me.roboroads.gearth.gpackets.incoming.CatalogPageWithEarliestExpiry;
import me.roboroads.gearth.gpackets.incoming.CatalogPublished;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.BadgeProduct;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.CatalogNode;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.FrontPageItem;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.FurniProduct;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.Localization;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.Offer;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.PageLinkFrontPageItem;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.ProductCodeFrontPageItem;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.ProductOfferFrontPageItem;
import me.roboroads.gearth.gpackets.model.enums.ActivityPointType;
import me.roboroads.gearth.gpackets.model.enums.CatalogType;
import me.roboroads.gearth.gpackets.model.enums.ClubLevel;
import me.roboroads.gearth.gpackets.model.enums.FrontPageItemType;
import me.roboroads.gearth.gpackets.model.enums.ProductType;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogIndex;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPage;
import me.roboroads.gearth.gpackets.outgoing.GetCatalogPageWithEarliestExpiry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("deprecation") // the samples set Offer.unknownBoolean12 and CatalogPageWithEarliestExpiry.image
class CatalogWireFormatTest {

    // ---- CatalogIndex ----

    static CatalogIndex catalogIndex() {
        CatalogNode child = CatalogNode.builder()
                .visible(true).icon(5).pageId(10).pageName("frontpage").pageTitle("Front page")
                .offerIds(Arrays.asList(11, 12)).children(Collections.emptyList())
                .build();
        CatalogNode root = CatalogNode.builder()
                .visible(true).icon(0).pageId(-1).pageName("root").pageTitle("Root")
                .offerIds(Collections.emptyList()).children(Collections.singletonList(child))
                .build();
        return CatalogIndex.builder().root(root).newAdditionsAvailable(false).catalogType(CatalogType.NORMAL).build();
    }

    static HPacket catalogIndexPacket() {
        HPacket p = new HPacket("CatalogIndex", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true).appendInt(0).appendInt(-1).appendString("root").appendString("Root")
                .appendInt(0)
                .appendInt(1);
        p.appendBoolean(true).appendInt(5).appendInt(10).appendString("frontpage").appendString("Front page")
                .appendInt(2).appendInt(11).appendInt(12)
                .appendInt(0);
        p.appendBoolean(false).appendString("NORMAL");
        return p;
    }

    @Test
    void catalogIndexToPacket() {
        assertSameBytes(catalogIndexPacket(), catalogIndex().toPacket());
    }

    @Test
    void catalogIndexFromPacket() {
        assertEquals(catalogIndex(), CatalogIndex.fromPacket(catalogIndexPacket()));
    }

    @Test
    void catalogIndexValuesNestTheTree() {
        Map<?, ?> root = (Map<?, ?>) CatalogIndex.TYPE.read(catalogIndexPacket()).get("root");
        Map<?, ?> child = (Map<?, ?>) ((List<?>) root.get("children")).get(0);

        assertEquals("frontpage", child.get("pageName"));
        assertEquals(Arrays.asList(11, 12), child.get("offerIds"));
    }

    @Test
    void catalogPageValuesLeaveOutTheMissingFrontPageItems() {
        assertFalse(CatalogPage.TYPE.read(catalogPagePacket(false)).containsKey("frontPageItems"));
    }

    // ---- CatalogPage ----

    static List<FrontPageItem> frontPageItems() {
        return Arrays.asList(
                new PageLinkFrontPageItem(0, "Rares", "rares.png", FrontPageItemType.PAGE_LINK, 0, "rares_page"),
                new ProductOfferFrontPageItem(1, "Dragon", "dragon.png", FrontPageItemType.PRODUCT_OFFER, 3600, 99),
                new ProductCodeFrontPageItem(2, "Code", "code.png", FrontPageItemType.PRODUCT_CODE, 0, "CODE1"));
    }

    static CatalogPage catalogPage(List<FrontPageItem> frontPageItems) {
        Offer offer = Offer.builder()
                .offerId(99).localizationId("rare_dragon").isRent(false)
                .priceInCredits(25).priceInActivityPoints(5).activityPointType(ActivityPointType.DUCKETS).priceInSilver(0)
                .giftable(true)
                .products(Arrays.asList(
                        FurniProduct.builder().productType(ProductType.STUFF)
                                .furniClassId(100).extraParam("").productCount(1)
                                .uniqueLimitedItem(true).uniqueLimitedItemSeriesSize(500).uniqueLimitedItemsLeft(12)
                                .build(),
                        FurniProduct.builder().productType(ProductType.ITEM)
                                .furniClassId(200).extraParam("poster").productCount(2)
                                .uniqueLimitedItem(false)
                                .build(),
                        BadgeProduct.builder().productType(ProductType.BADGE).extraParam("ACH_1").build()))
                .clubLevel(ClubLevel.CLUB).bundlePurchaseAllowed(true).unknownBoolean12(false).previewImage("dragon.png")
                .build();
        return CatalogPage.builder()
                .pageId(10).catalogType(CatalogType.NORMAL).layoutCode("default_3x3")
                .localization(Localization.builder()
                        .images(Collections.singletonList("header.png"))
                        .texts(Collections.singletonList("Hello"))
                        .build())
                .offers(Collections.singletonList(offer))
                .offerId(-1).acceptSeasonCurrencyAsCredits(false)
                .frontPageItems(frontPageItems)
                .build();
    }

    static HPacket catalogPagePacket(boolean withFrontPageItems) {
        HPacket p = new HPacket("CatalogPage", HMessage.Direction.TOCLIENT);
        p.appendInt(10).appendString("NORMAL").appendString("default_3x3");
        // localization
        p.appendInt(1).appendString("header.png").appendInt(1).appendString("Hello");
        // offers
        p.appendInt(1);
        p.appendInt(99).appendString("rare_dragon").appendBoolean(false)
                .appendInt(25).appendInt(5).appendInt(0).appendInt(0)
                .appendBoolean(true);
        p.appendInt(3);
        p.appendString("s").appendInt(100).appendString("").appendInt(1)
                .appendBoolean(true).appendInt(500).appendInt(12);
        p.appendString("i").appendInt(200).appendString("poster").appendInt(2)
                .appendBoolean(false);
        p.appendString("b").appendString("ACH_1");
        p.appendInt(1).appendBoolean(true).appendBoolean(false).appendString("dragon.png");
        // offerId, acceptSeasonCurrencyAsCredits
        p.appendInt(-1).appendBoolean(false);
        if (withFrontPageItems) {
            p.appendInt(3);
            p.appendInt(0).appendString("Rares").appendString("rares.png").appendInt(0)
                    .appendString("rares_page").appendInt(0);
            p.appendInt(1).appendString("Dragon").appendString("dragon.png").appendInt(1)
                    .appendInt(99).appendInt(3600);
            p.appendInt(2).appendString("Code").appendString("code.png").appendInt(2)
                    .appendString("CODE1").appendInt(0);
        }
        return p;
    }

    @Test
    void anOfferKeepsAnActivityPointTypeTheLibraryDoesNotName() {
        HPacket in = new HPacket("Test", HMessage.Direction.TOCLIENT);
        in.appendInt(7).appendString("seasonal").appendBoolean(false)
                .appendInt(0).appendInt(10).appendInt(2000).appendInt(0)
                .appendBoolean(true)
                .appendInt(0)
                .appendInt(0).appendBoolean(false).appendBoolean(false).appendString("");

        Offer offer = Offer.fromPacket(in);
        HPacket out = new HPacket("Test", HMessage.Direction.TOCLIENT);
        offer.appendPacket(out);

        assertFalse(offer.activityPointType().known());
        assertEquals(2000, offer.activityPointType().value());
        assertSameBytes(in, out);
    }

    @Test
    void catalogPageToPacket() {
        assertSameBytes(catalogPagePacket(true), catalogPage(frontPageItems()).toPacket());
    }

    @Test
    void catalogPageFromPacket() {
        assertEquals(catalogPage(frontPageItems()), CatalogPage.fromPacket(catalogPagePacket(true)));
    }

    @Test
    void catalogPageWithoutFrontPageItemsToPacket() {
        assertSameBytes(catalogPagePacket(false), catalogPage(null).toPacket());
    }

    @Test
    void catalogPageWithoutFrontPageItemsFromPacket() {
        assertEquals(catalogPage(null), CatalogPage.fromPacket(catalogPagePacket(false)));
    }

    // ---- CatalogPageWithEarliestExpiry ----

    @Test
    void catalogPageWithEarliestExpiryRoundTrips() {
        CatalogPageWithEarliestExpiry sample = CatalogPageWithEarliestExpiry.builder()
                .pageName("seasonal").secondsToExpiry(3600).image("seasonal.png").build();
        HPacket expected = new HPacket("CatalogPageWithEarliestExpiry", HMessage.Direction.TOCLIENT);
        expected.appendString("seasonal").appendInt(3600).appendString("seasonal.png");

        assertSameBytes(expected, sample.toPacket());
        assertEquals(sample, CatalogPageWithEarliestExpiry.fromPacket(expected));
    }

    // ---- CatalogPublished ----

    static HPacket catalogPublishedPacket(boolean withHash) {
        HPacket p = new HPacket("CatalogPublished", HMessage.Direction.TOCLIENT);
        p.appendBoolean(withHash);
        if (withHash) {
            p.appendString("abc123");
        }
        return p;
    }

    @Test
    void catalogPublishedWithHashRoundTrips() {
        CatalogPublished sample = CatalogPublished.builder().instantlyRefreshCatalog(true).newFurniDataHash("abc123").build();

        assertSameBytes(catalogPublishedPacket(true), sample.toPacket());
        assertEquals(sample, CatalogPublished.fromPacket(catalogPublishedPacket(true)));
    }

    @Test
    void catalogPublishedValuesLeaveOutTheMissingTail() {
        assertEquals(Collections.singletonList("instantlyRefreshCatalog"),
                new ArrayList<>(CatalogPublished.TYPE.read(catalogPublishedPacket(false)).keySet()));
    }

    @Test
    void catalogPublishedWithoutHashRoundTrips() {
        CatalogPublished sample = CatalogPublished.builder().instantlyRefreshCatalog(false).build();

        assertSameBytes(catalogPublishedPacket(false), sample.toPacket());
        assertEquals(sample, CatalogPublished.fromPacket(catalogPublishedPacket(false)));
    }

    // ---- outgoing ----

    @Test
    void getCatalogIndexRoundTrips() {
        GetCatalogIndex sample = GetCatalogIndex.builder().catalogType(CatalogType.BUILDERS_CLUB).build();
        HPacket expected = new HPacket("GetCatalogIndex", HMessage.Direction.TOSERVER);
        expected.appendString("BUILDERS_CLUB");

        assertSameBytes(expected, sample.toPacket());
        assertEquals(sample, GetCatalogIndex.fromPacket(expected));
    }

    @Test
    void getCatalogPageRoundTrips() {
        GetCatalogPage sample = GetCatalogPage.builder().pageId(5).offerId(7).catalogType(CatalogType.NORMAL).build();
        HPacket expected = new HPacket("GetCatalogPage", HMessage.Direction.TOSERVER);
        expected.appendInt(5).appendInt(7).appendString("NORMAL");

        assertSameBytes(expected, sample.toPacket());
        assertEquals(sample, GetCatalogPage.fromPacket(expected));
    }

    @Test
    void getCatalogPageWithEarliestExpiryRoundTrips() {
        GetCatalogPageWithEarliestExpiry sample = new GetCatalogPageWithEarliestExpiry();
        HPacket expected = new HPacket("GetCatalogPageWithEarliestExpiry", HMessage.Direction.TOSERVER);

        assertSameBytes(expected, sample.toPacket());
        assertEquals(sample, GetCatalogPageWithEarliestExpiry.fromPacket(expected));
    }
}
