package me.roboroads.gearth.gpackets;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.incoming.AuthenticationOK;
import me.roboroads.gearth.gpackets.incoming.AvailabilityStatus;
import me.roboroads.gearth.gpackets.incoming.CompleteDiffieHandshake;
import me.roboroads.gearth.gpackets.incoming.DisconnectReason;
import me.roboroads.gearth.gpackets.incoming.ErrorReport;
import me.roboroads.gearth.gpackets.incoming.GenericError;
import me.roboroads.gearth.gpackets.incoming.IdentityAccounts;
import me.roboroads.gearth.gpackets.incoming.InfoHotelClosed;
import me.roboroads.gearth.gpackets.incoming.InfoHotelClosing;
import me.roboroads.gearth.gpackets.incoming.InitDiffieHandshake;
import me.roboroads.gearth.gpackets.incoming.IsFirstLoginOfDay;
import me.roboroads.gearth.gpackets.incoming.LoginFailedHotelClosed;
import me.roboroads.gearth.gpackets.incoming.MaintenanceStatus;
import me.roboroads.gearth.gpackets.incoming.NoobnessLevel;
import me.roboroads.gearth.gpackets.incoming.Ping;
import me.roboroads.gearth.gpackets.incoming.UniqueMachineID;
import me.roboroads.gearth.gpackets.incoming.UserObject;
import me.roboroads.gearth.gpackets.incoming.UserRights;
import me.roboroads.gearth.gpackets.incoming.sub.user.IdentityAccount;
import me.roboroads.gearth.gpackets.model.enums.ClubLevel;
import me.roboroads.gearth.gpackets.model.enums.DisconnectReasonCode;
import me.roboroads.gearth.gpackets.model.enums.Gender;
import me.roboroads.gearth.gpackets.model.enums.GenericErrorCode;
import me.roboroads.gearth.gpackets.model.enums.Noobness;
import me.roboroads.gearth.gpackets.model.enums.OperatingSystem;
import me.roboroads.gearth.gpackets.outgoing.ClientHello;
import me.roboroads.gearth.gpackets.outgoing.Disconnect;
import me.roboroads.gearth.gpackets.outgoing.InfoRetrieve;
import me.roboroads.gearth.gpackets.outgoing.Pong;
import me.roboroads.gearth.gpackets.outgoing.SSOTicket;
import me.roboroads.gearth.gpackets.outgoing.UniqueID;
import me.roboroads.gearth.gpackets.outgoing.VersionCheck;
import me.roboroads.gearth.gpackets.support.schema.limit.LimitException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.WireAssert.assertSameBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("deprecation") // tests ErrorReport's unused messageId and timestamp
class HandshakeWireFormatTest {

    // ---- AuthenticationOK ----

    static HPacket authenticationOkPacket() {
        HPacket p = new HPacket("AuthenticationOK", HMessage.Direction.TOCLIENT);
        p.appendInt(12345).appendInt(2).appendShort((short) 0).appendShort((short) 1).appendInt(678);
        return p;
    }

    static AuthenticationOK authenticationOk() {
        return new AuthenticationOK(12345, Arrays.asList((short) 0, (short) 1), 678);
    }

    @Test
    void authenticationOkToPacket() {
        assertSameBytes(authenticationOkPacket(), authenticationOk().toPacket());
    }

    @Test
    void authenticationOkFromPacket() {
        assertEquals(authenticationOk(), AuthenticationOK.fromPacket(authenticationOkPacket()));
    }

    // ---- AvailabilityStatus ----

    static HPacket availabilityStatusPacket() {
        HPacket p = new HPacket("AvailabilityStatus", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true).appendBoolean(false).appendBoolean(true);
        return p;
    }

    @Test
    void availabilityStatusToPacket() {
        assertSameBytes(availabilityStatusPacket(), new AvailabilityStatus(true, false, true).toPacket());
    }

    @Test
    void availabilityStatusFromPacket() {
        assertEquals(new AvailabilityStatus(true, false, true), AvailabilityStatus.fromPacket(availabilityStatusPacket()));
    }

    @Test
    void availabilityStatusMayLeaveOutTheLastFlag() {
        HPacket p = new HPacket("AvailabilityStatus", HMessage.Direction.TOCLIENT);
        p.appendBoolean(false).appendBoolean(true);
        assertSameBytes(p, new AvailabilityStatus(false, true, null).toPacket());
        assertEquals(new AvailabilityStatus(false, true, null), AvailabilityStatus.fromPacket(p));
    }

    // ---- CompleteDiffieHandshake (incoming) ----

    static HPacket completeDiffieHandshakePacket() {
        HPacket p = new HPacket("CompleteDiffieHandshake", HMessage.Direction.TOCLIENT);
        p.appendString("0123456789abcdef").appendBoolean(true);
        return p;
    }

    @Test
    void completeDiffieHandshakeToPacket() {
        assertSameBytes(completeDiffieHandshakePacket(), new CompleteDiffieHandshake("0123456789abcdef", true).toPacket());
    }

    @Test
    void completeDiffieHandshakeFromPacket() {
        assertEquals(new CompleteDiffieHandshake("0123456789abcdef", true),
                CompleteDiffieHandshake.fromPacket(completeDiffieHandshakePacket()));
    }

    @Test
    void completeDiffieHandshakeMayLeaveOutServerClientEncryption() {
        HPacket p = new HPacket("CompleteDiffieHandshake", HMessage.Direction.TOCLIENT);
        p.appendString("0123456789abcdef");
        assertSameBytes(p, new CompleteDiffieHandshake("0123456789abcdef", null).toPacket());
        assertEquals(new CompleteDiffieHandshake("0123456789abcdef", null), CompleteDiffieHandshake.fromPacket(p));
    }

    // ---- DisconnectReason ----

    static HPacket disconnectReasonPacket() {
        HPacket p = new HPacket("DisconnectReason", HMessage.Direction.TOCLIENT);
        p.appendInt(2);
        return p;
    }

    @Test
    void disconnectReasonToPacket() {
        assertSameBytes(disconnectReasonPacket(), new DisconnectReason(DisconnectReasonCode.CONCURRENT_LOGIN).toPacket());
    }

    @Test
    void disconnectReasonFromPacket() {
        DisconnectReason parsed = DisconnectReason.fromPacket(disconnectReasonPacket());
        assertEquals(new DisconnectReason(DisconnectReasonCode.CONCURRENT_LOGIN), parsed);
        assertSame(DisconnectReasonCode.CONCURRENT_LOGIN, parsed.reason());
    }

    @Test
    void disconnectReasonMayLeaveOutTheReason() {
        HPacket p = new HPacket("DisconnectReason", HMessage.Direction.TOCLIENT);
        assertSameBytes(p, new DisconnectReason(null).toPacket());
        assertNull(DisconnectReason.fromPacket(p).reason());
    }

    @Test
    void disconnectReasonKeepsAnUnnamedReason() {
        HPacket p = new HPacket("DisconnectReason", HMessage.Direction.TOCLIENT);
        p.appendInt(105);
        DisconnectReason parsed = DisconnectReason.fromPacket(p);
        assertEquals(105, parsed.reason().value());
        assertSameBytes(p, parsed.toPacket());
    }

    // ---- ErrorReport ----

    static HPacket errorReportPacket() {
        HPacket p = new HPacket("ErrorReport", HMessage.Direction.TOCLIENT);
        p.appendInt(2154).appendInt(1001).appendString("2026-10-02 12:00:00");
        return p;
    }

    @Test
    void errorReportToPacket() {
        assertSameBytes(errorReportPacket(), new ErrorReport(2154, 1001, "2026-10-02 12:00:00").toPacket());
    }

    @Test
    void errorReportFromPacket() {
        assertEquals(new ErrorReport(2154, 1001, "2026-10-02 12:00:00"), ErrorReport.fromPacket(errorReportPacket()));
    }

    // ---- GenericError ----

    static HPacket genericErrorPacket() {
        HPacket p = new HPacket("GenericError", HMessage.Direction.TOCLIENT);
        p.appendInt(-100002);
        return p;
    }

    @Test
    void genericErrorToPacket() {
        assertSameBytes(genericErrorPacket(), new GenericError(GenericErrorCode.WRONG_ROOM_PASSWORD).toPacket());
    }

    @Test
    void genericErrorFromPacket() {
        GenericError parsed = GenericError.fromPacket(genericErrorPacket());
        assertEquals(new GenericError(GenericErrorCode.WRONG_ROOM_PASSWORD), parsed);
        assertSame(GenericErrorCode.WRONG_ROOM_PASSWORD, parsed.errorCode());
    }

    @Test
    void genericErrorKeepsAnUnnamedCode() {
        HPacket p = new HPacket("GenericError", HMessage.Direction.TOCLIENT);
        p.appendInt(4012);
        GenericError parsed = GenericError.fromPacket(p);
        assertEquals(4012, parsed.errorCode().value());
        assertSameBytes(p, parsed.toPacket());
    }

    // ---- IdentityAccounts ----

    static HPacket identityAccountsPacket() {
        HPacket p = new HPacket("IdentityAccounts", HMessage.Direction.TOCLIENT);
        p.appendInt(2)
                .appendInt(12345).appendString("Roboroads")
                .appendInt(12346).appendString("Roboroads2");
        return p;
    }

    static IdentityAccounts identityAccounts() {
        return new IdentityAccounts(Arrays.asList(
                new IdentityAccount(12345, "Roboroads"),
                new IdentityAccount(12346, "Roboroads2")));
    }

    @Test
    void identityAccountsToPacket() {
        assertSameBytes(identityAccountsPacket(), identityAccounts().toPacket());
    }

    @Test
    void identityAccountsFromPacket() {
        assertEquals(identityAccounts(), IdentityAccounts.fromPacket(identityAccountsPacket()));
    }

    @Test
    void identityAccountsMayBeEmpty() {
        HPacket p = new HPacket("IdentityAccounts", HMessage.Direction.TOCLIENT);
        p.appendInt(0);
        assertSameBytes(p, new IdentityAccounts(Collections.emptyList()).toPacket());
        assertEquals(new IdentityAccounts(Collections.emptyList()), IdentityAccounts.fromPacket(p));
    }

    // ---- InfoHotelClosed ----

    static HPacket infoHotelClosedPacket() {
        HPacket p = new HPacket("InfoHotelClosed", HMessage.Direction.TOCLIENT);
        p.appendInt(8).appendInt(30).appendBoolean(true);
        return p;
    }

    @Test
    void infoHotelClosedToPacket() {
        assertSameBytes(infoHotelClosedPacket(), new InfoHotelClosed(8, 30, true).toPacket());
    }

    @Test
    void infoHotelClosedFromPacket() {
        assertEquals(new InfoHotelClosed(8, 30, true), InfoHotelClosed.fromPacket(infoHotelClosedPacket()));
    }

    // ---- InfoHotelClosing ----

    static HPacket infoHotelClosingPacket() {
        HPacket p = new HPacket("InfoHotelClosing", HMessage.Direction.TOCLIENT);
        p.appendInt(15);
        return p;
    }

    @Test
    void infoHotelClosingToPacket() {
        assertSameBytes(infoHotelClosingPacket(), new InfoHotelClosing(15).toPacket());
    }

    @Test
    void infoHotelClosingFromPacket() {
        assertEquals(new InfoHotelClosing(15), InfoHotelClosing.fromPacket(infoHotelClosingPacket()));
    }

    // ---- InitDiffieHandshake (incoming) ----

    static HPacket initDiffieHandshakePacket() {
        HPacket p = new HPacket("InitDiffieHandshake", HMessage.Direction.TOCLIENT);
        p.appendString("aaaa1111").appendString("bbbb2222");
        return p;
    }

    @Test
    void initDiffieHandshakeToPacket() {
        assertSameBytes(initDiffieHandshakePacket(), new InitDiffieHandshake("aaaa1111", "bbbb2222").toPacket());
    }

    @Test
    void initDiffieHandshakeFromPacket() {
        assertEquals(new InitDiffieHandshake("aaaa1111", "bbbb2222"), InitDiffieHandshake.fromPacket(initDiffieHandshakePacket()));
    }

    // ---- IsFirstLoginOfDay ----

    static HPacket isFirstLoginOfDayPacket() {
        HPacket p = new HPacket("IsFirstLoginOfDay", HMessage.Direction.TOCLIENT);
        p.appendBoolean(true);
        return p;
    }

    @Test
    void isFirstLoginOfDayToPacket() {
        assertSameBytes(isFirstLoginOfDayPacket(), new IsFirstLoginOfDay(true).toPacket());
    }

    @Test
    void isFirstLoginOfDayFromPacket() {
        assertEquals(new IsFirstLoginOfDay(true), IsFirstLoginOfDay.fromPacket(isFirstLoginOfDayPacket()));
    }

    // ---- LoginFailedHotelClosed ----

    static HPacket loginFailedHotelClosedPacket() {
        HPacket p = new HPacket("LoginFailedHotelClosed", HMessage.Direction.TOCLIENT);
        p.appendInt(7).appendInt(45);
        return p;
    }

    @Test
    void loginFailedHotelClosedToPacket() {
        assertSameBytes(loginFailedHotelClosedPacket(), new LoginFailedHotelClosed(7, 45).toPacket());
    }

    @Test
    void loginFailedHotelClosedFromPacket() {
        assertEquals(new LoginFailedHotelClosed(7, 45), LoginFailedHotelClosed.fromPacket(loginFailedHotelClosedPacket()));
    }

    // ---- MaintenanceStatus ----

    static HPacket maintenanceStatusPacket() {
        HPacket p = new HPacket("MaintenanceStatus", HMessage.Direction.TOCLIENT);
        p.appendBoolean(false).appendInt(10).appendInt(30);
        return p;
    }

    @Test
    void maintenanceStatusToPacket() {
        assertSameBytes(maintenanceStatusPacket(), new MaintenanceStatus(false, 10, 30).toPacket());
    }

    @Test
    void maintenanceStatusFromPacket() {
        assertEquals(new MaintenanceStatus(false, 10, 30), MaintenanceStatus.fromPacket(maintenanceStatusPacket()));
    }

    @Test
    void maintenanceStatusMayLeaveOutTheDuration() {
        HPacket p = new HPacket("MaintenanceStatus", HMessage.Direction.TOCLIENT);
        p.appendBoolean(false).appendInt(10);
        assertSameBytes(p, new MaintenanceStatus(false, 10, null).toPacket());
        assertEquals(new MaintenanceStatus(false, 10, null), MaintenanceStatus.fromPacket(p));
    }

    // ---- NoobnessLevel ----

    static HPacket noobnessLevelPacket() {
        HPacket p = new HPacket("NoobnessLevel", HMessage.Direction.TOCLIENT);
        p.appendInt(2);
        return p;
    }

    @Test
    void noobnessLevelToPacket() {
        assertSameBytes(noobnessLevelPacket(), new NoobnessLevel(Noobness.REAL_NOOB).toPacket());
    }

    @Test
    void noobnessLevelFromPacket() {
        assertEquals(new NoobnessLevel(Noobness.REAL_NOOB), NoobnessLevel.fromPacket(noobnessLevelPacket()));
    }

    // ---- Ping ----

    static HPacket pingPacket() {
        return new HPacket("Ping", HMessage.Direction.TOCLIENT);
    }

    @Test
    void pingToPacket() {
        assertSameBytes(pingPacket(), new Ping().toPacket());
    }

    @Test
    void pingFromPacket() {
        assertEquals(new Ping(), Ping.fromPacket(pingPacket()));
    }

    // ---- UniqueMachineID ----

    static HPacket uniqueMachineIdPacket() {
        HPacket p = new HPacket("UniqueMachineID", HMessage.Direction.TOCLIENT);
        p.appendString("placeholder-machine-id");
        return p;
    }

    @Test
    void uniqueMachineIdToPacket() {
        assertSameBytes(uniqueMachineIdPacket(), new UniqueMachineID("placeholder-machine-id").toPacket());
    }

    @Test
    void uniqueMachineIdFromPacket() {
        assertEquals(new UniqueMachineID("placeholder-machine-id"), UniqueMachineID.fromPacket(uniqueMachineIdPacket()));
    }

    // ---- UserObject ----

    static HPacket userObjectHead(HPacket p) {
        p.appendInt(12345)
                .appendString("Roboroads")
                .appendString("hr-115-42.hd-195-19.ch-3030-82.lg-275-1408")
                .appendString("M")
                .appendString("Hello there")
                .appendString("")
                .appendBoolean(false)
                .appendInt(42)
                .appendInt(3)
                .appendInt(2)
                .appendBoolean(false)
                .appendString("2026-10-01")
                .appendBoolean(true)
                .appendBoolean(false);
        return p;
    }

    static HPacket userObjectPacket() {
        HPacket p = userObjectHead(new HPacket("UserObject", HMessage.Direction.TOCLIENT));
        p.appendBoolean(false).appendString("ff0000").appendInt(1).appendInt(3);
        return p;
    }

    static UserObject.UserObjectBuilder userObjectHead() {
        return UserObject.builder()
                .userId(12345)
                .name("Roboroads")
                .figure("hr-115-42.hd-195-19.ch-3030-82.lg-275-1408")
                .sex(Gender.MALE)
                .customData("Hello there")
                .realName("")
                .directMail(false)
                .respectTotal(42)
                .respectLeft(3)
                .petRespectLeft(2)
                .streamPublishingAllowed(false)
                .lastAccessDate("2026-10-01")
                .nameChangeAllowed(true)
                .accountSafetyLocked(false);
    }

    static UserObject userObject() {
        return userObjectHead()
                .accountTradeLocked(false)
                .nameColor("ff0000")
                .respectReplenishesLeft(1)
                .maxRespectPerDay(3)
                .build();
    }

    @Test
    void userObjectToPacket() {
        assertSameBytes(userObjectPacket(), userObject().toPacket());
    }

    @Test
    void userObjectFromPacket() {
        assertEquals(userObject(), UserObject.fromPacket(userObjectPacket()));
    }

    @Test
    void userObjectMayStopAfterEachOptionalBlock() {
        HPacket withoutTail = userObjectHead(new HPacket("UserObject", HMessage.Direction.TOCLIENT));
        assertSameBytes(withoutTail, userObjectHead().build().toPacket());
        assertEquals(userObjectHead().build(), UserObject.fromPacket(withoutTail));

        HPacket withoutRespectTail = userObjectHead(new HPacket("UserObject", HMessage.Direction.TOCLIENT));
        withoutRespectTail.appendBoolean(true).appendString("");
        UserObject tradeLocked = userObjectHead().accountTradeLocked(true).nameColor("").build();
        assertSameBytes(withoutRespectTail, tradeLocked.toPacket());
        assertEquals(tradeLocked, UserObject.fromPacket(withoutRespectTail));
    }

    // ---- UserRights ----

    static HPacket userRightsPacket() {
        HPacket p = new HPacket("UserRights", HMessage.Direction.TOCLIENT);
        p.appendInt(2).appendInt(5).appendBoolean(true);
        return p;
    }

    @Test
    void userRightsToPacket() {
        assertSameBytes(userRightsPacket(), new UserRights(ClubLevel.VIP, 5, true).toPacket());
    }

    @Test
    void userRightsFromPacket() {
        assertEquals(new UserRights(ClubLevel.VIP, 5, true), UserRights.fromPacket(userRightsPacket()));
    }

    // ---- ClientHello ----

    static HPacket clientHelloPacket() {
        HPacket p = new HPacket("ClientHello", HMessage.Direction.TOSERVER);
        p.appendString("WIN63-202609161723-93809945").appendString("FLASH29").appendInt(6).appendInt(4);
        return p;
    }

    @Test
    void clientHelloToPacket() {
        assertSameBytes(clientHelloPacket(),
                new ClientHello("WIN63-202609161723-93809945", "FLASH29", OperatingSystem.WINDOWS, 4).toPacket());
    }

    @Test
    void clientHelloFromPacket() {
        assertEquals(new ClientHello("WIN63-202609161723-93809945", "FLASH29", OperatingSystem.WINDOWS, 4),
                ClientHello.fromPacket(clientHelloPacket()));
    }

    @Test
    void clientHelloSendsFourLikeTheClientByDefault() {
        ClientHello hello = ClientHello.builder()
                .releaseVersion("WIN63-202609161723-93809945")
                .clientType("FLASH29")
                .operatingSystem(OperatingSystem.WINDOWS)
                .build();
        assertSameBytes(clientHelloPacket(), hello.toPacket());
    }

    // ---- CompleteDiffieHandshake (outgoing) ----

    static HPacket outgoingCompleteDiffieHandshakePacket() {
        HPacket p = new HPacket("CompleteDiffieHandshake", HMessage.Direction.TOSERVER);
        p.appendString("fedcba9876543210");
        return p;
    }

    @Test
    void outgoingCompleteDiffieHandshakeToPacket() {
        assertSameBytes(outgoingCompleteDiffieHandshakePacket(),
                new me.roboroads.gearth.gpackets.outgoing.CompleteDiffieHandshake("fedcba9876543210").toPacket());
    }

    @Test
    void outgoingCompleteDiffieHandshakeFromPacket() {
        assertEquals(new me.roboroads.gearth.gpackets.outgoing.CompleteDiffieHandshake("fedcba9876543210"),
                me.roboroads.gearth.gpackets.outgoing.CompleteDiffieHandshake.fromPacket(outgoingCompleteDiffieHandshakePacket()));
    }

    // ---- Disconnect ----

    static HPacket disconnectPacket() {
        return new HPacket("Disconnect", HMessage.Direction.TOSERVER);
    }

    @Test
    void disconnectToPacket() {
        assertSameBytes(disconnectPacket(), new Disconnect().toPacket());
    }

    @Test
    void disconnectFromPacket() {
        assertEquals(new Disconnect(), Disconnect.fromPacket(disconnectPacket()));
    }

    // ---- InfoRetrieve ----

    static HPacket infoRetrievePacket() {
        return new HPacket("InfoRetrieve", HMessage.Direction.TOSERVER);
    }

    @Test
    void infoRetrieveToPacket() {
        assertSameBytes(infoRetrievePacket(), new InfoRetrieve().toPacket());
    }

    @Test
    void infoRetrieveFromPacket() {
        assertEquals(new InfoRetrieve(), InfoRetrieve.fromPacket(infoRetrievePacket()));
    }

    // ---- InitDiffieHandshake (outgoing) ----

    static HPacket outgoingInitDiffieHandshakePacket() {
        return new HPacket("InitDiffieHandshake", HMessage.Direction.TOSERVER);
    }

    @Test
    void outgoingInitDiffieHandshakeToPacket() {
        assertSameBytes(outgoingInitDiffieHandshakePacket(), new me.roboroads.gearth.gpackets.outgoing.InitDiffieHandshake().toPacket());
    }

    @Test
    void outgoingInitDiffieHandshakeFromPacket() {
        assertEquals(new me.roboroads.gearth.gpackets.outgoing.InitDiffieHandshake(),
                me.roboroads.gearth.gpackets.outgoing.InitDiffieHandshake.fromPacket(outgoingInitDiffieHandshakePacket()));
    }

    // ---- Pong ----

    static HPacket pongPacket() {
        return new HPacket("Pong", HMessage.Direction.TOSERVER);
    }

    @Test
    void pongToPacket() {
        assertSameBytes(pongPacket(), new Pong().toPacket());
    }

    @Test
    void pongFromPacket() {
        assertEquals(new Pong(), Pong.fromPacket(pongPacket()));
    }

    // ---- SSOTicket ----

    static HPacket ssoTicketPacket() {
        HPacket p = new HPacket("SSOTicket", HMessage.Direction.TOSERVER);
        p.appendString("placeholder-sso-ticket").appendInt(5321);
        return p;
    }

    @Test
    void ssoTicketToPacket() {
        assertSameBytes(ssoTicketPacket(), new SSOTicket("placeholder-sso-ticket", 5321).toPacket());
    }

    @Test
    void ssoTicketFromPacket() {
        assertEquals(new SSOTicket("placeholder-sso-ticket", 5321), SSOTicket.fromPacket(ssoTicketPacket()));
    }

    @Test
    void ssoTicketRefusesAnEmptyTicket() {
        assertThrows(LimitException.class, () -> new SSOTicket("", 5321).toPacket());
    }

    // ---- UniqueID ----

    static HPacket uniqueIdPacket() {
        HPacket p = new HPacket("UniqueID", HMessage.Direction.TOSERVER);
        p.appendString("placeholder-machine-id").appendString("placeholder-fingerprint").appendString("WIN/32,0,0,465");
        return p;
    }

    @Test
    void uniqueIdToPacket() {
        assertSameBytes(uniqueIdPacket(),
                new UniqueID("placeholder-machine-id", "placeholder-fingerprint", "WIN/32,0,0,465").toPacket());
    }

    @Test
    void uniqueIdFromPacket() {
        assertEquals(new UniqueID("placeholder-machine-id", "placeholder-fingerprint", "WIN/32,0,0,465"),
                UniqueID.fromPacket(uniqueIdPacket()));
    }

    // ---- VersionCheck ----

    static HPacket versionCheckPacket() {
        HPacket p = new HPacket("VersionCheck", HMessage.Direction.TOSERVER);
        p.appendInt(401).appendString("app:/").appendString("https://example.com/gamedata/external_variables/1");
        return p;
    }

    @Test
    void versionCheckToPacket() {
        assertSameBytes(versionCheckPacket(),
                new VersionCheck(401, "app:/", "https://example.com/gamedata/external_variables/1").toPacket());
    }

    @Test
    void versionCheckFromPacket() {
        assertEquals(new VersionCheck(401, "app:/", "https://example.com/gamedata/external_variables/1"),
                VersionCheck.fromPacket(versionCheckPacket()));
    }

    @Test
    void versionCheckSends401LikeTheClientByDefault() {
        VersionCheck check = VersionCheck.builder()
                .flashClientUrl("app:/")
                .externalVariablesUrl("https://example.com/gamedata/external_variables/1")
                .build();
        assertSameBytes(versionCheckPacket(), check.toPacket());
    }
}
