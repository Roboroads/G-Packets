package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * Why the server disconnected you. The client's {@code DisconnectReason} event declares about
 * fifty codes with obfuscated names, and {@code resolveDisconnectedReasonLocalizationKey} only
 * gives some of them their own text; the rest show "You have been disconnected. Please try
 * again." ({@code disconnected.generic}). The names here come from those texts, and from the three
 * constants the client didn't obfuscate. {@link #of} keeps every other id.
 *
 * <p>The client also uses -1 (a client crash) and -3 (the socket closed) for disconnects it
 * detects itself; those never come from the server.
 */
public final class DisconnectReasonCode extends OpenIntEnum {
    // disconnected.maintenance: "Maintenance break!" The client also uses it itself when a
    // MaintenanceStatus arrives.
    public static final DisconnectReasonCode MAINTENANCE = new DisconnectReasonCode(-2);
    // disconnected.logged_out: "Logged out!"
    public static final DisconnectReasonCode LOGGED_OUT = new DisconnectReasonCode(0);
    // disconnected.just_banned: "You were banned!"
    public static final DisconnectReasonCode JUST_BANNED = new DisconnectReasonCode(1);
    // disconnected.concurrent_login: "You've logged in elsewhere!" The client's reasonString also
    // calls it concurrentlogin.
    public static final DisconnectReasonCode CONCURRENT_LOGIN = new DisconnectReasonCode(2);
    // disconnected.still_banned: "You've been banned!"
    public static final DisconnectReasonCode STILL_BANNED = new DisconnectReasonCode(10);
    // Shows the same text as CONCURRENT_LOGIN.
    public static final DisconnectReasonCode CONCURRENT_LOGIN_11 = new DisconnectReasonCode(11);
    // disconnected.hotel_closed: "Hotel is closed!"
    public static final DisconnectReasonCode HOTEL_CLOSED = new DisconnectReasonCode(12);
    // Shows the same text as CONCURRENT_LOGIN.
    public static final DisconnectReasonCode CONCURRENT_LOGIN_13 = new DisconnectReasonCode(13);
    // Shows the same text as CONCURRENT_LOGIN.
    public static final DisconnectReasonCode CONCURRENT_LOGIN_18 = new DisconnectReasonCode(18);
    // Shows the same text as HOTEL_CLOSED.
    public static final DisconnectReasonCode HOTEL_CLOSED_19 = new DisconnectReasonCode(19);
    // disconnected.incorrect_password: "Incorrect password!" The login screen shows its invalid
    // login error for it.
    public static final DisconnectReasonCode INCORRECT_PASSWORD = new DisconnectReasonCode(20);
    // disconnected.idle: "Idle disconnection!"
    public static final DisconnectReasonCode IDLE = new DisconnectReasonCode(112);
    // The client's own constant names for 117 to 119.
    public static final DisconnectReasonCode SOCKET_WRITE_EXCEPTION_1 = new DisconnectReasonCode(117);
    public static final DisconnectReasonCode SOCKET_WRITE_EXCEPTION_2 = new DisconnectReasonCode(118);
    public static final DisconnectReasonCode SOCKET_WRITE_EXCEPTION_3 = new DisconnectReasonCode(119);
    // disconnected.incompatible_client_version: "Old version. Please update!"
    public static final DisconnectReasonCode INCOMPATIBLE_CLIENT_VERSION = new DisconnectReasonCode(122);

    private DisconnectReasonCode(int value) {
        super(value);
    }

    /** The named reason with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DisconnectReasonCode of(int value) {
        return of(DisconnectReasonCode.class, value, DisconnectReasonCode::new);
    }

    /** The named reasons, sorted by id. */
    public static List<DisconnectReasonCode> values() {
        return values(DisconnectReasonCode.class);
    }
}
