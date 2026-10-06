package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.model.enums.ExpressionType;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/** A user in the room waves, laughs, blows a kiss or shows another expression. */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Expression implements Packet, JsonSerializable {
    public static final PacketType<Expression> TYPE = PacketType.of("Expression", HMessage.Direction.TOCLIENT, Schema.of(Expression.class)
            .integer("userIndex")
            .enumInt("expressionType", ExpressionType.class));

    // The user's room index (User.userIndex), not their account id. The client calls it userId.
    private Integer userIndex;
    private ExpressionType expressionType;

    public static Expression fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static Expression fromJson(String json) {
        return Json.parse(Expression.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
