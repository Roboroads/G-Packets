package me.roboroads.gearth.gpackets.incoming.sub.furni;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gearth.protocol.HPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.roboroads.gearth.gpackets.support.JsonSerializable;
import me.roboroads.gearth.gpackets.support.SubPacket;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.WireType;

import java.util.function.UnaryOperator;

/**
 * A furni's state data: what a furni shows or does beyond its type, such as a state string, a
 * map of values or a high score table. The low byte of {@link #typeAndFlags} picks the format and
 * so the subclass; {@link #UNIQUE_SERIAL_FLAG} adds the serial number of a limited edition.
 */
// The client: __Q2t/__G18.parseStuffData and room/object/data/__h1x.getStuffDataWrapperForType.
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "typeAndFlags", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LegacyStuffData.class, names = {"0", "256"}),
        @JsonSubTypes.Type(value = MapStuffData.class, names = {"1", "257"}),
        @JsonSubTypes.Type(value = StringArrayStuffData.class, names = {"2", "258"}),
        @JsonSubTypes.Type(value = VoteResultStuffData.class, names = {"3", "259"}),
        @JsonSubTypes.Type(value = EmptyStuffData.class, names = {"4", "260"}),
        @JsonSubTypes.Type(value = IntArrayStuffData.class, names = {"5", "261"}),
        @JsonSubTypes.Type(value = HighScoreStuffData.class, names = {"6", "262"}),
        @JsonSubTypes.Type(value = CrackableStuffData.class, names = {"7", "263"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class StuffData implements SubPacket, JsonSerializable {
    /**
     * The flag in {@link #typeAndFlags} for a limited edition furni: {@link #uniqueSerialNumber} and
     * {@link #uniqueSeriesSize} follow the format's values. The client calls it UNIQUE_SERIAL_NUMBER
     * (room/object/data/__G1k).
     */
    public static final int UNIQUE_SERIAL_FLAG = 256;

    public static final Schema<StuffData> SCHEMA;

    static {
        UnaryOperator<Schema<LegacyStuffData>> legacy = s -> s
                .string("legacyString");
        UnaryOperator<Schema<MapStuffData>> map = s -> s
                .list("entries", MapStuffDataEntry.SCHEMA);
        UnaryOperator<Schema<StringArrayStuffData>> stringArray = s -> s
                .list("values", WireType.STRING);
        UnaryOperator<Schema<VoteResultStuffData>> voteResult = s -> s
                .string("legacyString")
                .integer("result");
        UnaryOperator<Schema<EmptyStuffData>> empty = s -> s;
        UnaryOperator<Schema<IntArrayStuffData>> intArray = s -> s
                .list("values", WireType.INT);
        UnaryOperator<Schema<HighScoreStuffData>> highScore = s -> s
                .string("legacyString")
                .integer("scoreType")
                .integer("clearType")
                .list("entries", HighScoreData.SCHEMA);
        UnaryOperator<Schema<CrackableStuffData>> crackable = s -> s
                .string("legacyString")
                .integer("hits")
                .integer("target");
        // The client only looks at the low byte (the format) and at UNIQUE_SERIAL_FLAG, so these are
        // the values it reads. A value with another flag bit has no case and fails to parse.
        SCHEMA = Schema.of(StuffData.class)
                .integer("typeAndFlags")
                .branch("typeAndFlags", cases -> cases
                        .on(0, LegacyStuffData.class, legacy)
                        .on(1, MapStuffData.class, map)
                        .on(2, StringArrayStuffData.class, stringArray)
                        .on(3, VoteResultStuffData.class, voteResult)
                        .on(4, EmptyStuffData.class, empty)
                        .on(5, IntArrayStuffData.class, intArray)
                        .on(6, HighScoreStuffData.class, highScore)
                        .on(7, CrackableStuffData.class, crackable)
                        .on(UNIQUE_SERIAL_FLAG, LegacyStuffData.class, withSerial(legacy))
                        .on(UNIQUE_SERIAL_FLAG | 1, MapStuffData.class, withSerial(map))
                        .on(UNIQUE_SERIAL_FLAG | 2, StringArrayStuffData.class, withSerial(stringArray))
                        .on(UNIQUE_SERIAL_FLAG | 3, VoteResultStuffData.class, withSerial(voteResult))
                        .on(UNIQUE_SERIAL_FLAG | 4, EmptyStuffData.class, withSerial(empty))
                        .on(UNIQUE_SERIAL_FLAG | 5, IntArrayStuffData.class, withSerial(intArray))
                        // The client's high score data never reads the serial, even with the flag.
                        .on(UNIQUE_SERIAL_FLAG | 6, HighScoreStuffData.class, highScore)
                        .on(UNIQUE_SERIAL_FLAG | 7, CrackableStuffData.class, withSerial(crackable)));
    }

    // The format in the low byte (0 legacy, 1 map, 2 string array, 3 vote result, 4 empty, 5 int
    // array, 6 high score, 7 crackable), plus UNIQUE_SERIAL_FLAG for a limited edition. Set it to
    // write a serial: without the flag, the serial isn't on the wire.
    private Integer typeAndFlags;
    // The edition number of a limited edition furni. Only on the wire with UNIQUE_SERIAL_FLAG.
    private Integer uniqueSerialNumber;
    // How many of this limited edition furni exist. Only on the wire with UNIQUE_SERIAL_FLAG.
    private Integer uniqueSeriesSize;

    public static StuffData fromPacket(HPacket packet) {
        return SCHEMA.parse(packet);
    }

    @Override
    public void appendPacket(HPacket packet) {
        SCHEMA.append(this, packet);
    }

    private static <S extends StuffData> UnaryOperator<Schema<S>> withSerial(UnaryOperator<Schema<S>> body) {
        return s -> body.apply(s)
                .integer("uniqueSerialNumber")
                .integer("uniqueSeriesSize");
    }
}
