package me.roboroads.gearth.gpackets.support.schema;

import me.roboroads.gearth.gpackets.incoming.CatalogIndex;
import me.roboroads.gearth.gpackets.model.enums.Direction;
import me.roboroads.gearth.gpackets.support.schema.limit.Limit;
import me.roboroads.gearth.gpackets.support.schema.limit.MaxLength;
import me.roboroads.gearth.gpackets.support.schema.limit.Violation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.each;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxLength;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxSize;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.not;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.notEmpty;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.range;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.requiresVip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaLimitsTest {

    static class Plain {
    }

    static class Item {
    }

    private static final Schema<Item> ITEM = Schema.of(Item.class).string("label", maxLength(3));

    private static Map<String, Object> values(Object... keysAndValues) {
        Map<String, Object> values = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            values.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return values;
    }

    private static List<String> lines(List<Violation> violations) {
        String[] lines = new String[violations.size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = violations.get(i).toString();
        }
        return Arrays.asList(lines);
    }

    @Test
    void aParameterKeepsItsLimits() {
        Schema<Plain> schema = Schema.of(Plain.class).string("name", notEmpty(), maxLength(5)).integer("id");
        List<Limit> limits = schema.parameters().get(0).limits();

        assertEquals(2, limits.size());
        assertEquals(5, ((MaxLength) limits.get(1)).max());
        assertTrue(schema.parameters().get(1).limits().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> limits.add(requiresVip()));
    }

    @Test
    void aLimitOnAParameterItDoesNotFitFailsWhenTheSchemaIsBuilt() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Schema.of(Plain.class).integer("count", maxLength(5)));
        assertEquals("Plain.count: at most 5 characters doesn't apply to this parameter", e.getMessage());

        assertThrows(IllegalArgumentException.class, () -> Schema.of(Plain.class).string("name", maxSize(2)));
        assertThrows(IllegalArgumentException.class, () -> Schema.of(Plain.class).string("name", range(1, 2)));
        assertThrows(IllegalArgumentException.class, () -> Schema.of(Plain.class).enumInt("dir", Direction.class, range(0, 3)));
        assertThrows(IllegalArgumentException.class, () -> Schema.of(Plain.class).list("ids", WireType.INT, each(maxLength(3))));
        assertThrows(IllegalArgumentException.class, () -> Schema.of(Plain.class).list("items", ITEM, each(maxLength(3))));
        assertThrows(IllegalArgumentException.class, () -> Schema.of(Plain.class).struct("item", ITEM, maxSize(1)));
    }

    @Test
    void violationsListEveryBrokenLimitWithItsPath() {
        Schema<Plain> schema = Schema.of(Plain.class)
                .string("name", notEmpty(), maxLength(5))
                .list("tags", WireType.STRING, maxSize(2), each(maxLength(3)))
                .integer("sleep", range(30, 3600).orZero(), requiresVip())
                .list("items", ITEM);

        assertEquals(Arrays.asList(
                        "name: at most 5 characters, got 7",
                        "tags: at most 2 items, got 3",
                        "tags[1]: at most 3 characters, got 4",
                        "sleep: must be 30 to 3600, or 0, got 10",
                        "items[1].label: at most 3 characters, got 4"),
                lines(schema.violations(values(
                        "name", "toolong",
                        "tags", Arrays.asList("a", "bcde", "f"),
                        "sleep", 10,
                        "items", Arrays.asList(values("label", "abc"), values("label", "abcd"))))));
        assertTrue(schema.violations(values("name", "ok", "tags", Collections.singletonList("ab"), "sleep", 0)).isEmpty());
    }

    @Test
    void aMissingValueIsCheckedAsItsDefault() {
        Schema<Plain> schema = Schema.of(Plain.class).string("name", notEmpty()).integer("sleep", range(30, 3600));

        assertEquals(Arrays.asList("name: must not be empty", "sleep: must be 30 to 3600, got 0"),
                lines(schema.violations(new HashMap<>())));
    }

    @Test
    void notComparesEnumsByWireValue() {
        Schema<Plain> schema = Schema.of(Plain.class).enumInt("dir", Direction.class, not(Direction.NORTH));

        assertEquals(1, schema.violations(values("dir", Direction.NORTH)).size());
        assertEquals(1, schema.violations(values("dir", 0)).size());
        assertTrue(schema.violations(values("dir", Direction.EAST)).isEmpty());
    }

    @Test
    void aWrongValueTypeFailsLikeWriting() {
        Schema<Plain> schema = Schema.of(Plain.class).string("name", maxLength(5));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> schema.violations(values("name", 5)));
        assertTrue(e.getMessage().startsWith("name: "), e.getMessage());
    }

    @Test
    void limitsInAnOptionalBodyAreCheckedOnlyWhenItIsWritten() {
        Schema<Plain> schema = Schema.of(Plain.class).bool("flag").optional(o -> o.string("hash", maxLength(2)));

        assertTrue(schema.violations(new HashMap<>()).isEmpty());
        assertEquals(Collections.singletonList("hash: at most 2 characters, got 3"), lines(schema.violations(values("hash", "abc"))));
    }

    @Test
    void rulesSeeTheValuesAsTheyWouldBeWritten() {
        Schema<Plain> schema = Schema.of(Plain.class)
                .integer("a")
                .integer("b")
                .rule("b is more than a", values -> (Integer) values.get("b") > (Integer) values.get("a"));

        assertEquals(1, schema.rules().size());
        assertEquals("b is more than a", schema.rules().get(0).description());
        assertEquals(Collections.singletonList("Plain: b is more than a"), lines(schema.violations(new HashMap<>())));
        assertTrue(schema.violations(values("a", 1, "b", 2)).isEmpty());
    }

    @Test
    void aRuleSeesALeftOutListAsEmptyAndALeftOutStructAsNoValues() {
        Schema<Plain> schema = Schema.of(Plain.class)
                .list("tags", WireType.STRING)
                .struct("item", ITEM)
                .rule("at most 3 tags", values -> ((List<?>) values.get("tags")).size() <= 3)
                .rule("no item values", values -> ((Map<?, ?>) values.get("item")).isEmpty());

        assertTrue(schema.violations(new HashMap<>()).isEmpty());
    }

    @Test
    void aRuleThatThrowsFailsWithItsDescription() {
        Schema<Plain> schema = Schema.of(Plain.class)
                .integer("a")
                .rule("a fits", values -> {
                    throw new IllegalStateException("boom");
                });

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> schema.violations(new HashMap<>()));

        assertEquals("Plain: the rule \"a fits\" failed: java.lang.IllegalStateException: boom", e.getMessage());
    }

    @Test
    void hasChecksLooksThroughNestedSchemas() {
        assertFalse(Schema.of(Plain.class).integer("id").hasChecks());
        assertFalse(Schema.of(Plain.class).integer("id", requiresVip()).hasChecks());
        assertTrue(Schema.of(Plain.class).string("name", maxLength(5)).hasChecks());
        assertTrue(Schema.of(Plain.class).list("items", ITEM).hasChecks());
        assertTrue(Schema.of(Plain.class).integer("a").rule("always", values -> true).hasChecks());
    }

    @Test
    void hasChecksStopsAtASchemaThatContainsItself() {
        assertFalse(CatalogIndex.TYPE.schema().hasChecks());
    }
}
