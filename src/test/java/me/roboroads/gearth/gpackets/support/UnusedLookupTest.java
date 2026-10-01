package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.outgoing.Chat;
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.ValueParameter;
import org.junit.jupiter.api.Test;
import testfixtures.unused.UnusedFixtures.Base;
import testfixtures.unused.UnusedFixtures.Child;
import testfixtures.unused.UnusedFixtures.IgnoredPacket;
import testfixtures.unused.UnusedFixtures.Mode;
import testfixtures.unused.UnusedFixtures.Shadow;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("deprecation")
class UnusedLookupTest {

    private static Parameter parameter(Schema<?> schema, String name) {
        for (Parameter parameter : schema.parameters()) {
            if (name.equals(parameter.name())) {
                return parameter;
            }
        }
        throw new AssertionError(schema.type().getSimpleName() + " has no parameter " + name);
    }

    @Test
    void aParameterTakesTheReasonFromItsField() {
        Schema<Base> schema = Schema.of(Base.class).integer("legacy").integer("kind");

        assertEquals("The client stores it but never reads it", parameter(schema, "legacy").unused());
        assertNull(parameter(schema, "kind").unused());
    }

    @Test
    void aFieldOnASuperclassCounts() {
        Schema<Child> schema = Schema.of(Child.class).integer("legacy").string("extra").string("kept");

        assertEquals("The client stores it but never reads it", parameter(schema, "legacy").unused());
        assertEquals("Only sent by old servers", parameter(schema, "extra").unused());
        assertNull(parameter(schema, "kept").unused());
    }

    @Test
    void theNearestFieldWins() {
        Schema<Shadow> schema = Schema.of(Shadow.class).integer("legacy");

        assertNull(parameter(schema, "legacy").unused());
    }

    @Test
    void aBranchCaseUsesItsSubclassFields() {
        Schema<Base> schema = Schema.of(Base.class).integer("kind")
                .branch("kind", cases -> cases.on(1, Child.class, s -> s.string("extra")));
        BranchParameter branch = (BranchParameter) schema.parameters().get(1);
        Schema<?> child = branch.cases().values().iterator().next().schema();

        assertEquals("Only sent by old servers", parameter(child, "extra").unused());
        assertNull(branch.unused());
    }

    @Test
    void aNameWithoutAFieldIsNotUnused() {
        assertNull(parameter(Schema.of(Base.class).integer("missing"), "missing").unused());
    }

    @Test
    void unusedOptionsListOnlyTheIgnoredConstants() {
        Schema<Base> schema = Schema.of(Base.class).enumInt("mode", Mode.class).integer("kind");

        assertEquals(Collections.singletonMap("OFF", "The client declares it but never acts on it"),
                ((ValueParameter) parameter(schema, "mode")).unusedOptions());
        assertEquals(Collections.emptyMap(), ((ValueParameter) parameter(schema, "kind")).unusedOptions());
    }

    @Test
    void aPacketTypeTakesTheReasonFromItsClass() {
        PacketType<IgnoredPacket> ignored = PacketType.of("Ignored", HMessage.Direction.TOCLIENT, Schema.of(IgnoredPacket.class));

        assertEquals("The client's handler ignores it", ignored.unused());
        assertNull(Chat.TYPE.unused());
    }
}
