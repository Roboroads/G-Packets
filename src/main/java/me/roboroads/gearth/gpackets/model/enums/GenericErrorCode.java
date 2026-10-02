package me.roboroads.gearth.gpackets.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * The code of a {@code GenericError}. The server sends many codes; the client only handles the
 * ones named here, in the connection handler ({@code communication/demo/__cW.as}), the
 * navigator ({@code navigator/__cW.as} onError) and the room session's {@code GenericErrorHandler}.
 * {@link #of} keeps every other id.
 */
public final class GenericErrorCode extends OpenIntEnum {
    // navigator: shows the room password input again with navigator.password.retryinfo, "Wrong
    // password. Please retry, or cancel entering the room."
    public static final GenericErrorCode WRONG_ROOM_PASSWORD = new GenericErrorCode(-100002);
    // navigator: notification.nft_token_required, "You don't have the NFT token required to access
    // this room".
    public static final GenericErrorCode NFT_TOKEN_REQUIRED = new GenericErrorCode(-100005);
    // The client's own name (session/enum/GenericErrorEnum.as). Nothing in this client handles it.
    public static final GenericErrorCode STRIP_LOCKED_FOR_TRADING = new GenericErrorCode(-13001);
    // connection.login.error.-400.desc: "Connecting to the server failed".
    public static final GenericErrorCode CONNECTING_FAILED = new GenericErrorCode(-400);
    // connection.login.error.-3.desc: "Authentication failed" (another text says "Wrong email or
    // password").
    public static final GenericErrorCode AUTHENTICATION_FAILED = new GenericErrorCode(-3);
    // The client's own name (session/enum/GenericErrorEnum.as). The room session raises
    // RSEME_KICKED for it.
    public static final GenericErrorCode KICKED_BY_OWNER = new GenericErrorCode(4008);
    // navigator: navigator.alert.need.to.be.vip.
    public static final GenericErrorCode NEED_TO_BE_VIP = new GenericErrorCode(4009);
    // navigator: navigator.alert.invalid_room_name, "Room name is unacceptable!"
    public static final GenericErrorCode INVALID_ROOM_NAME = new GenericErrorCode(4010);
    // navigator: navigator.alert.cannot_perm_ban, "Cannot ban group member!"
    public static final GenericErrorCode CANNOT_BAN_GROUP_MEMBER = new GenericErrorCode(4011);
    // navigator: navigator.alert.room_in_maintenance.
    public static final GenericErrorCode ROOM_IN_MAINTENANCE = new GenericErrorCode(4013);

    private GenericErrorCode(int value) {
        super(value);
    }

    /** The named code with this id, or a value that keeps it. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GenericErrorCode of(int value) {
        return of(GenericErrorCode.class, value, GenericErrorCode::new);
    }

    /** The named codes, sorted by id. */
    public static List<GenericErrorCode> values() {
        return values(GenericErrorCode.class);
    }
}
