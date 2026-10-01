package me.roboroads.gearth.gpackets.support.schema;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.model.enums.UserType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void aCaseSubclassMustExtendTheSchemaClass() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Person.class).enumInt("kind", UserType.class).branch("kind", cases -> cases
                        .on(UserType.PET, Animal.class, s -> s)));

        assertEquals("Person.kind: " + Animal.class.getName() + " does not extend " + Person.class.getName(), e.getMessage());
    }
}
