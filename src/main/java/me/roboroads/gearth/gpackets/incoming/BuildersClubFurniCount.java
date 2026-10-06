package me.roboroads.gearth.gpackets.incoming;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import me.roboroads.gearth.gpackets.support.CheckedAgainst;
import me.roboroads.gearth.gpackets.support.Json;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.schema.Schema;

/**
 * How many furni you have borrowed from the Builders Club catalog; the answer to
 * {@code BuildersClubQueryFurniCount}. The catalog shows it next to the limit from
 * {@code BuildersClubSubscriptionStatus} (builder.header.status.limit, "Borrowed items: %COUNT%/%LIMIT%").
 */
@CheckedAgainst("WIN63-202609091217-117204808")
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BuildersClubFurniCount implements Packet, JsonSerializable {
    public static final PacketType<BuildersClubFurniCount> TYPE = PacketType.of("BuildersClubFurniCount", HMessage.Direction.TOCLIENT, Schema.of(BuildersClubFurniCount.class)
            .integer("furniCount"));

    // The catalog starts at -1 and won't let you place more while the count is below 0 or at
    // BuildersClubSubscriptionStatus.furniLimit (the catalog's getBuilderFurniPlaceableStatusForOffer).
    private Integer furniCount;

    public static BuildersClubFurniCount fromPacket(HPacket packet) {
        return TYPE.schema().parse(packet);
    }

    public static BuildersClubFurniCount fromJson(String json) {
        return Json.parse(BuildersClubFurniCount.class, json);
    }

    @Override
    public HPacket toPacket() {
        return TYPE.toPacket(this);
    }
}
