package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaBranchTest {

    static class Sample {
    }

    static class Person extends Sample {
    }

    static class Animal extends Sample {
    }

    private static final Schema<Sample> SCHEMA = Schema.of(Sample.class)
            .enumInt("kind", UserType.class)
            .branch("kind", cases -> cases
                    .on(UserType.PLAYER, Person.class, s -> s.string("name"))
                    .on(UserType.PET, Animal.class, s -> s
                            .integer("level")
                            .bool("hasSaddle")
                            .when("hasSaddle", true, w -> w.string("saddleColor"))));

    private static HPacket packet() {
        return new HPacket("Test", HMessage.Direction.TOCLIENT);
    }

    private static String bytes(HPacket packet) {
        return Arrays.toString(packet.toBytes());
    }

    @Test
    void readPicksTheCaseAndFlattensItsValues() {
        HPacket p = packet();
        p.appendInt(2).appendInt(9).appendBoolean(true).appendString("red");

        Map<String, Object> values = SCHEMA.read(p);

        assertEquals(Arrays.asList("kind", "level", "hasSaddle", "saddleColor"), new ArrayList<>(values.keySet()));
        assertEquals(2, values.get("kind"));
        assertEquals("red", values.get("saddleColor"));
    }

    @Test
    void readSkipsAWhenWhoseValueDoesNotMatch() {
        HPacket p = packet();
        p.appendInt(2).appendInt(9).appendBoolean(false);

        assertEquals(Arrays.asList("kind", "level", "hasSaddle"), new ArrayList<>(SCHEMA.read(p).keySet()));
    }

    @Test
    void readFailsForAValueWithoutACase() {
        HPacket p = packet();
        p.appendInt(7);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SCHEMA.read(p));

        assertEquals("Sample.kind: no case for value 7", e.getMessage());
    }

    @Test
    void writeAcceptsTheEnumConstantAsDiscriminator() {
        Map<String, Object> values = new HashMap<>();
        values.put("kind", UserType.PLAYER);
        values.put("name", "Ann");
        HPacket out = packet();

        SCHEMA.write(values, out);

        HPacket expected = packet();
        expected.appendInt(1).appendString("Ann");
        assertEquals(bytes(expected), bytes(out));
    }

    @Test
    void writeFailsForANullDiscriminator() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SCHEMA.write(new HashMap<>(), packet()));

        assertEquals("Sample.kind: is null", e.getMessage());
    }

    @Test
    void writeWithANullWhenFlagWritesFalseAndSkipsTheCase() {
        Map<String, Object> values = new HashMap<>();
        values.put("kind", 2);
        values.put("level", 3);
        HPacket out = packet();

        SCHEMA.write(values, out);

        HPacket expected = packet();
        expected.appendInt(2).appendInt(3).appendBoolean(false);
        assertEquals(bytes(expected), bytes(out));
    }

    @Test
    void writeAfterSwitchingCasesIgnoresTheOldCasesKeys() {
        HPacket pet = packet();
        pet.appendInt(2).appendInt(9).appendBoolean(true).appendString("red");
        Map<String, Object> values = SCHEMA.read(pet);
        values.put("kind", 1);
        values.put("name", "Ann");
        HPacket out = packet();

        SCHEMA.write(values, out);

        HPacket expected = packet();
        expected.appendInt(1).appendString("Ann");
        assertEquals(bytes(expected), bytes(out));
    }

    @Test
    void branchErrorsInsideListsNameTheElement() {
        Schema<Sample> list = Schema.of(Sample.class).list("all", SCHEMA);
        HPacket p = packet();
        p.appendInt(2).appendInt(1).appendString("a").appendInt(9);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> list.read(p));

        assertEquals("Sample.all[1].kind: no case for value 9", e.getMessage());
    }

    @Test
    void branchesExposeTheirCases() {
        BranchParameter branch = (BranchParameter) SCHEMA.parameters().get(1);

        assertNull(branch.name());
        assertEquals("kind", branch.on());
        assertTrue(branch.exhaustive());
        assertEquals(Arrays.asList(1, 2), new ArrayList<>(branch.cases().keySet()));
        assertEquals(Person.class, branch.cases().get(1).subclass());
        assertEquals(1, branch.cases().get(1).value());
        assertEquals("name", branch.cases().get(1).schema().parameters().get(0).name());

        BranchParameter when = (BranchParameter) branch.cases().get(2).schema().parameters().get(2);
        assertEquals("hasSaddle", when.on());
        assertFalse(when.exhaustive());
        assertNull(when.cases().get(true).subclass());
    }

    @Test
    void branchingOnAnUnknownValueIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).integer("a").branch("nope", cases -> cases));

        assertEquals("Sample has no earlier value named nope to branch on", e.getMessage());
    }

    @Test
    void duplicateCasesAreRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).enumInt("kind", UserType.class).branch("kind", cases -> cases
                        .on(UserType.PLAYER, Person.class, s -> s)
                        .on(1, Person.class, s -> s)));

        assertEquals("Sample.kind: duplicate case for value 1", e.getMessage());
    }

    @Test
    void aCaseNameThatClashesWithAnOuterNameIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).enumInt("kind", UserType.class).integer("level")
                        .branch("kind", cases -> cases.on(UserType.PET, Animal.class, s -> s.integer("level"))));

        assertEquals("Sample already has a parameter named level", e.getMessage());
    }

    @Test
    void anOuterNameThatClashesWithAnEarlierCaseIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> SCHEMA.string("saddleColor"));

        assertEquals("Sample already has a parameter named saddleColor", e.getMessage());
    }

    @Test
    void aWhenBodyNameThatClashesWithAnOuterNameIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).bool("flag").integer("extra").when("flag", true, w -> w.integer("extra")));
    }

    @Test
    void casesOfOneBranchMayShareNames() {
        Schema<Sample> schema = Schema.of(Sample.class).enumInt("kind", UserType.class)
                .branch("kind", cases -> cases
                        .on(UserType.PLAYER, Person.class, s -> s.integer("seconds"))
                        .on(UserType.PET, Animal.class, s -> s.integer("seconds")));

        assertEquals(2, schema.parameters().size());
    }

    @Test
    void aCaseSubclassMustExtendTheSchemaClass() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Person.class).enumInt("kind", UserType.class).branch("kind", cases -> cases
                        .on(UserType.PET, Animal.class, s -> s)));

        assertEquals("Person.kind: " + Animal.class.getName() + " does not extend " + Person.class.getName(), e.getMessage());
    }

    // The low byte picks the shape; flag 256 adds a serial inside the case, other bits are ignored.
    private static final Schema<Sample> MASKED = Schema.of(Sample.class)
            .integer("typeAndFlags")
            .branch("typeAndFlags", 0xFF, cases -> cases
                    .on(1, Person.class, s -> s
                            .string("name")
                            .when("typeAndFlags", 256, 256, w -> w.integer("serial")))
                    .on(2, Animal.class, s -> s.integer("level")));

    @Test
    void aMaskedBranchPicksTheCaseByTheMaskedBits() {
        HPacket p = packet();
        p.appendInt(0x201).appendString("Ann");

        Map<String, Object> values = MASKED.read(p);

        assertEquals(Arrays.asList("typeAndFlags", "name"), new ArrayList<>(values.keySet()));
        assertEquals(0x201, values.get("typeAndFlags"));
    }

    @Test
    void aMaskedWhenInACaseDependsOnAValueBeforeTheBranch() {
        HPacket p = packet();
        p.appendInt(0x301).appendString("Ann").appendInt(12);

        Map<String, Object> values = MASKED.read(p);

        assertEquals(12, values.get("serial"));
        HPacket out = packet();
        MASKED.write(values, out);
        assertEquals(bytes(p), bytes(out));
    }

    @Test
    void aMaskedBranchStillFailsForAShapeWithoutACase() {
        HPacket p = packet();
        p.appendInt(0x103);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MASKED.read(p));

        assertEquals("Sample.typeAndFlags: no case for value 259 (3 after the mask)", e.getMessage());
    }

    @Test
    void aMaskedBranchExposesItsMask() {
        BranchParameter branch = (BranchParameter) MASKED.parameters().get(1);
        BranchParameter when = (BranchParameter) branch.cases().get(1).schema().parameters().get(1);

        assertEquals(Integer.valueOf(0xFF), branch.mask());
        assertEquals(Integer.valueOf(256), when.mask());
        assertEquals("typeAndFlags", when.on());
        assertNull(((BranchParameter) SCHEMA.parameters().get(1)).mask());
    }

    @Test
    void aCaseOutsideTheMaskIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).integer("typeAndFlags").branch("typeAndFlags", 0xFF, cases -> cases
                        .on(256, Person.class, s -> s)));

        assertEquals("typeAndFlags: case 256 has bits outside the mask 255", e.getMessage());
    }

    // A pet or bot sends its owner, a player doesn't.
    private static final Schema<Sample> NEGATED = Schema.of(Sample.class)
            .enumInt("kind", UserType.class)
            .whenNot("kind", UserType.PLAYER, w -> w.integer("ownerId"))
            .string("name");

    @Test
    void aNegatedWhenReadsForEveryOtherValue() {
        HPacket pet = packet();
        pet.appendInt(2).appendInt(7).appendString("Rex");
        HPacket player = packet();
        player.appendInt(1).appendString("Alice");

        Map<String, Object> petValues = NEGATED.read(pet);

        assertEquals(Arrays.asList("kind", "ownerId", "name"), new ArrayList<>(petValues.keySet()));
        assertEquals(7, petValues.get("ownerId"));
        assertEquals(Arrays.asList("kind", "name"), new ArrayList<>(NEGATED.read(player).keySet()));
    }

    @Test
    void aNegatedWhenWritesForEveryOtherValue() {
        Map<String, Object> bot = new HashMap<>();
        bot.put("kind", UserType.BOT);
        bot.put("ownerId", 7);
        bot.put("name", "Frank");
        Map<String, Object> player = new HashMap<>(bot);
        player.put("kind", UserType.PLAYER);
        HPacket botOut = packet();
        HPacket playerOut = packet();

        NEGATED.write(bot, botOut);
        NEGATED.write(player, playerOut);

        assertEquals(bytes(packet().appendInt(4).appendInt(7).appendString("Frank")), bytes(botOut));
        assertEquals(bytes(packet().appendInt(1).appendString("Frank")), bytes(playerOut));
    }

    @Test
    void aNegatedWhenExposesItsValue() {
        BranchParameter when = (BranchParameter) NEGATED.parameters().get(1);

        assertTrue(when.negated());
        assertFalse(when.exhaustive());
        assertEquals("kind", when.on());
        assertEquals(Arrays.asList(1), new ArrayList<>(when.cases().keySet()));
        assertFalse(((BranchParameter) SCHEMA.parameters().get(1)).negated());
    }

    @Test
    void aSignBitMaskMatchesEveryNegativeValue() {
        Schema<Sample> schema = Schema.of(Sample.class)
                .integer("typeId")
                .when("typeId", Integer.MIN_VALUE, Integer.MIN_VALUE, w -> w.string("className"));
        HPacket negative = packet();
        negative.appendInt(-3).appendString("door");
        HPacket positive = packet();
        positive.appendInt(3);

        assertEquals("door", schema.read(negative).get("className"));
        assertEquals(Collections.singletonList("typeId"), new ArrayList<>(schema.read(positive).keySet()));
    }

    // 1 and 2 share the user that follows; 0 and any other value add nothing.
    private static final Schema<Sample> ONE_OF = Schema.of(Sample.class)
            .integer("moveType")
            .whenOneOf("moveType", Arrays.asList(1, 2), w -> w
                    .integer("userIndex")
                    .string("z"));

    @Test
    void whenOneOfReadsTheSharedParametersForEachValue() {
        for (int moveType : new int[]{1, 2}) {
            HPacket p = packet();
            p.appendInt(moveType).appendInt(7).appendString("1.0");

            Map<String, Object> values = ONE_OF.read(p);

            assertEquals(Arrays.asList("moveType", "userIndex", "z"), new ArrayList<>(values.keySet()));
            HPacket out = packet();
            ONE_OF.write(values, out);
            assertEquals(bytes(p), bytes(out));
        }
    }

    @Test
    void whenOneOfAddsNothingForAnotherValue() {
        HPacket p = packet();
        p.appendInt(0);

        assertEquals(Collections.singletonList("moveType"), new ArrayList<>(ONE_OF.read(p).keySet()));
    }

    @Test
    void whenOneOfSharesOneSchemaBetweenItsCases() {
        BranchParameter when = (BranchParameter) ONE_OF.parameters().get(1);

        assertFalse(when.exhaustive());
        assertEquals(Arrays.asList(1, 2), new ArrayList<>(when.cases().keySet()));
        assertSame(when.cases().get(1).schema(), when.cases().get(2).schema());
        assertNull(when.cases().get(1).subclass());
    }

    @Test
    void whenOneOfTakesEnumConstants() {
        BranchParameter when = (BranchParameter) Schema.of(Sample.class)
                .enumInt("kind", UserType.class)
                .whenOneOf("kind", Arrays.asList(UserType.PET, UserType.BOT), w -> w.integer("level"))
                .parameters().get(1);

        assertEquals(Arrays.asList(2, 4), new ArrayList<>(when.cases().keySet()));
    }

    @Test
    void whenOneOfRejectsNoValuesAndDuplicates() {
        IllegalArgumentException none = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).integer("a").whenOneOf("a", Collections.emptyList(), w -> w));
        IllegalArgumentException twice = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).integer("a").whenOneOf("a", Arrays.asList(1, 1), w -> w));

        assertEquals("Sample.a: whenOneOf needs at least one value", none.getMessage());
        assertEquals("Sample.a: duplicate value 1", twice.getMessage());
    }

    @Test
    void onlyAPlainWholeNumberCanBeMasked() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Sample.class).enumInt("kind", UserType.class).branch("kind", 0xFF, cases -> cases
                        .on(UserType.PLAYER, Person.class, s -> s)));

        assertEquals("kind: only a plain byte, short, int or long can be masked", e.getMessage());
    }
}
