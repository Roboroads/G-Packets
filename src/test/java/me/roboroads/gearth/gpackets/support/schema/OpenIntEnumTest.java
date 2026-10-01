package me.roboroads.gearth.gpackets.support.schema;

import me.roboroads.gearth.gpackets.support.Json;
import org.junit.jupiter.api.Test;
import testfixtures.openenum.OpenEnumFixtures.Clash;
import testfixtures.openenum.OpenEnumFixtures.Shade;
import testfixtures.openenum.OpenEnumFixtures.Tone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("deprecation") // Shade.FADED is marked unused
class OpenIntEnumTest {

    @Test
    void ofReturnsTheNamedConstant() {
        assertSame(Shade.LIGHT, Shade.of(1));
        assertSame(Shade.DARK, Shade.of(2));
    }

    @Test
    void aNamedIdKnowsItsName() {
        assertTrue(Shade.LIGHT.known());
        assertEquals("LIGHT", Shade.LIGHT.name());
        assertEquals("LIGHT", Shade.LIGHT.toString());
        assertEquals(1, Shade.LIGHT.value());
    }

    @Test
    void anUnnamedIdKeepsItsValue() {
        Shade nine = Shade.of(9);

        assertEquals(9, nine.value());
        assertFalse(nine.known());
        assertNull(nine.name());
        assertEquals("Shade(9)", nine.toString());
    }

    @Test
    void unnamedIdsAreEqualByValue() {
        assertEquals(Shade.of(9), Shade.of(9));
        assertEquals(Shade.of(9).hashCode(), Shade.of(9).hashCode());
        assertNotEquals(Shade.of(9), Shade.of(10));
        assertEquals(1, new HashSet<>(Arrays.asList(Shade.of(9), Shade.of(9))).size());
    }

    @Test
    void theSameIdOfAnotherTypeIsNotEqual() {
        assertNotEquals(Shade.of(1), Tone.of(1));
        assertNotEquals(Shade.of(9), Tone.of(9));
    }

    @Test
    void negativeIdsWork() {
        Shade minusOne = Shade.of(-1);

        assertEquals(-1, minusOne.value());
        assertFalse(minusOne.known());
        assertEquals("Shade(-1)", minusOne.toString());
    }

    @Test
    void valuesAreTheNamedConstantsSortedByValue() {
        assertEquals(Arrays.asList(Shade.LIGHT, Shade.DARK, Shade.FADED), Shade.values());
    }

    @Test
    void valuesCannotBeModified() {
        List<Shade> values = Shade.values();

        assertThrows(UnsupportedOperationException.class, values::clear);
    }

    @Test
    void twoConstantsWithTheSameValueFail() {
        IllegalStateException e = assertThrows(IllegalStateException.class, Clash::values);

        assertTrue(e.getMessage().startsWith(Clash.class.getName() + "."), e.getMessage());
        assertTrue(e.getMessage().contains("FIRST") && e.getMessage().contains("SECOND"), e.getMessage());
        assertTrue(e.getMessage().endsWith(" both have the value 1"), e.getMessage());
    }

    @Test
    void constantsListsNamesSortedByValue() {
        assertEquals(Arrays.asList("LIGHT", "DARK", "FADED"), new ArrayList<>(OpenIntEnum.constants(Shade.class).keySet()));
        assertSame(Shade.LIGHT, OpenIntEnum.constants(Shade.class).get("LIGHT"));
    }

    @Test
    void fromWireGivesTheNamedConstantOrCallsTheTypesOwnOf() {
        assertSame(Shade.DARK, OpenIntEnum.fromWire(Shade.class, 2));
        assertEquals(Shade.of(9), OpenIntEnum.fromWire(Shade.class, 9));
    }

    /** An open enum without of(int): fine for named ids, an error for any other. */
    public static final class NoFactory extends OpenIntEnum {
        public static final NoFactory ONE = new NoFactory(1);

        private NoFactory(int value) {
            super(value);
        }
    }

    @Test
    void fromWireNeedsAnOfMethodForAnUnnamedId() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> OpenIntEnum.fromWire(NoFactory.class, 5));

        assertEquals(NoFactory.class.getName() + " needs a public static of(int) to keep id 5", e.getMessage());
        assertSame(NoFactory.ONE, OpenIntEnum.fromWire(NoFactory.class, 1));
    }

    @Test
    void jsonWritesTheIdAndReadsItBack() {
        assertEquals("1", Json.stringify(Shade.LIGHT));
        assertEquals("9", Json.stringify(Shade.of(9)));
        assertSame(Shade.LIGHT, Json.parse(Shade.class, "1"));
        assertEquals(Shade.of(9), Json.parse(Shade.class, "9"));
    }
}
