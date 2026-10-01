package me.roboroads.gearth.gpackets.model.enums;

import me.roboroads.gearth.gpackets.support.schema.IntEnum;
import me.roboroads.gearth.gpackets.support.schema.StringEnum;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumWireValueTest {

    @Test
    @SuppressWarnings("rawtypes")
    void everyEnumImplementsIntEnumOrStringEnum() {
        Set<Class<? extends Enum>> enums = new Reflections("me.roboroads.gearth.gpackets.model.enums").getSubTypesOf(Enum.class);

        assertFalse(enums.isEmpty(), "No enums found! Check scan package.");
        for (Class<? extends Enum> type : enums) {
            assertTrue(IntEnum.class.isAssignableFrom(type) || StringEnum.class.isAssignableFrom(type),
                    type.getName() + " must implement IntEnum or StringEnum");
        }
    }

    @Test
    void wireValuesComeFromTheExistingGetters() {
        assertEquals(2, ((IntEnum) Direction.EAST).value());
        assertEquals("F", ((StringEnum) Gender.FEMALE).code());
        assertEquals("b", ((StringEnum) ProductType.BADGE).code());
    }
}
