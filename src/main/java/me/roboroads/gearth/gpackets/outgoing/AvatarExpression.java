package me.roboroads.gearth.gpackets.outgoing;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ExpressionType;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

import static me.roboroads.gearth.gpackets.model.enums.ExpressionType.CRY;
import static me.roboroads.gearth.gpackets.model.enums.ExpressionType.NONE;
import static me.roboroads.gearth.gpackets.model.enums.ExpressionType.RIDE_JUMP;
import static me.roboroads.gearth.gpackets.model.enums.ExpressionType.SNOWBOARD_360;
import static me.roboroads.gearth.gpackets.model.enums.ExpressionType.SNOWBOARD_OLLIE;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.not;

/** Waves, laughs, blows a kiss or shows another expression; the server tells the room with {@code Expression}. */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AvatarExpression implements Packet, JsonSerializable {
    public static final PacketType<AvatarExpression> TYPE = PacketType.of("AvatarExpression", HMessage.Direction.TOSERVER, Schema.of(AvatarExpression.class)
            .enumInt("expressionType", ExpressionType.class, not(NONE, CRY, SNOWBOARD_OLLIE, SNOWBOARD_360, RIDE_JUMP)));

    // The client sends WAVE, IDLE and RESPECT for everyone, and BLOW_A_KISS, LAUGH, JUMP and
    // EXPRESSION_67 only for VIP users (OwnAvatarMenuView, MeMenuMainView, ChatInputWidgetHandler
    // ":kiss", ":d", ":jump", ":67"). It never sends the others.
    private ExpressionType expressionType;

    public static AvatarExpression fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static AvatarExpression fromJson(String json) {
        return Json.parse(AvatarExpression.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
