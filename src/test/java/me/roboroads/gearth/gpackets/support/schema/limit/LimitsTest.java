package me.roboroads.gearth.gpackets.support.schema.limit;

import me.roboroads.gearth.gpackets.model.enums.Direction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.each;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxLength;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.maxSize;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.not;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.notEmpty;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.range;
import static me.roboroads.gearth.gpackets.support.schema.limit.Limits.requiresVip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimitsTest {

    @Test
    void maxLengthCountsCharacters() {
        MaxLength limit = maxLength(3);

        assertEquals(3, limit.max());
        assertEquals(Limit.Target.STRING, limit.target());
        assertEquals("at most 3 characters", limit.describe());
        assertNull(limit.problem("abc"));
        assertEquals("at most 3 characters, got 4", limit.problem("abcd"));
    }

    @Test
    void notEmptyRejectsTheEmptyString() {
        assertEquals("not empty", notEmpty().describe());
        assertNull(notEmpty().problem("a"));
        assertEquals("must not be empty", notEmpty().problem(""));
    }

    @Test
    void rangeIncludesBothEnds() {
        Range limit = range(30, 3600);

        assertEquals(Limit.Target.NUMBER, limit.target());
        assertEquals("30 to 3600", limit.describe());
        assertFalse(limit.allowsZero());
        assertNull(limit.problem(30));
        assertNull(limit.problem(3600L));
        assertNull(limit.problem((short) 31));
        assertEquals("must be 30 to 3600, got 10", limit.problem(10));
        assertEquals("must be 30 to 3600, got 0", limit.problem(0));
    }

    @Test
    void orZeroAlsoAllowsZero() {
        Range limit = range(30, 3600).orZero();

        assertTrue(limit.allowsZero());
        assertEquals(30, limit.min());
        assertEquals(3600, limit.max());
        assertEquals("30 to 3600, or 0", limit.describe());
        assertNull(limit.problem(0));
        assertEquals("must be 30 to 3600, or 0, got 10", limit.problem(10));
    }

    @Test
    void maxSizeCountsItems() {
        assertEquals(Limit.Target.LIST, maxSize(2).target());
        assertEquals("at most 2 items", maxSize(2).describe());
        assertNull(maxSize(2).problem(Arrays.asList("a", "b")));
        assertEquals("at most 2 items, got 3", maxSize(2).problem(Arrays.asList("a", "b", "c")));
    }

    @Test
    void eachAppliesTheLimitToEveryItem() {
        Each limit = each(maxLength(3));

        assertEquals(Limit.Target.EACH, limit.target());
        assertTrue(limit.limit() instanceof MaxLength);
        assertEquals("each at most 3 characters", limit.describe());
        assertNull(limit.problem(Arrays.asList("ab", "abc")));
        assertEquals("item 1: at most 3 characters, got 4", limit.problem(Arrays.asList("ab", "abcd")));
    }

    @Test
    void notComparesWireValues() {
        Not one = not(Direction.NORTH);
        Not two = not(Direction.NORTH, Direction.EAST);

        assertEquals(Limit.Target.VALUE, one.target());
        assertEquals(Arrays.<Object>asList(0, 2), two.values());
        assertEquals("not NORTH", one.describe());
        assertEquals("none of NORTH, EAST", two.describe());
        assertEquals("must not be NORTH", two.problem(0));
        assertEquals("must not be EAST", two.problem(2L));
        assertNull(two.problem(4));
        assertEquals("not \"x\"", not("x").describe());
        assertEquals("must not be \"x\"", not("x").problem("x"));
    }

    @Test
    void requiresVipOnlyDescribes() {
        assertEquals(Limit.Target.ANY, requiresVip().target());
        assertEquals("VIP only", requiresVip().describe());
        assertFalse(requiresVip().checked());
        assertNull(requiresVip().problem(123));
        assertTrue(maxLength(1).checked());
    }

    @Test
    void factoriesRejectLimitsThatMakeNoSense() {
        assertThrows(IllegalArgumentException.class, () -> maxLength(-1));
        assertThrows(IllegalArgumentException.class, () -> range(5, 1));
        assertThrows(IllegalArgumentException.class, () -> maxSize(-1));
        assertThrows(IllegalArgumentException.class, () -> each(maxSize(2)));
        assertThrows(IllegalArgumentException.class, () -> each(each(maxLength(1))));
        assertThrows(IllegalArgumentException.class, () -> each(requiresVip()));
        assertThrows(IllegalArgumentException.class, () -> not());
    }

    @Test
    void aRuleHoldsWhenItsCheckPasses() {
        Rule rule = new Rule("a is positive", values -> (Integer) values.get("a") > 0);

        assertEquals("a is positive", rule.description());
        assertTrue(rule.holds(Collections.<String, Object>singletonMap("a", 1)));
        assertFalse(rule.holds(Collections.<String, Object>singletonMap("a", 0)));
    }

    @Test
    void theExceptionListsEveryViolation() {
        Violation name = new Violation("name", "at most 5 characters, got 7", maxLength(5), null);
        Violation tags = new Violation("tags", "at most 2 items, got 3", maxSize(2), null);

        LimitException one = new LimitException("Chat", Collections.singletonList(name));
        LimitException two = new LimitException("SaveRoomSettings", Arrays.asList(name, tags));

        assertEquals("Chat breaks 1 limit (use toPacketUnchecked() to send it anyway):\n"
                + "  name: at most 5 characters, got 7", one.getMessage());
        assertEquals("SaveRoomSettings breaks 2 limits (use toPacketUnchecked() to send it anyway):\n"
                + "  name: at most 5 characters, got 7\n"
                + "  tags: at most 2 items, got 3", two.getMessage());
        assertEquals(Arrays.asList(name, tags), two.violations());
        assertThrows(UnsupportedOperationException.class, () -> two.violations().clear());
        assertEquals("name: at most 5 characters, got 7", name.toString());
    }
}
