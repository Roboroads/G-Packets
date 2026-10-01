package me.roboroads.gearth.gpackets;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Every open enum in the library has the shape {@code docs/contributing.md} describes. */
class OpenIntEnumTypesTest {

    // forPackage only picks the classpath roots; the filter keeps other packages (test fixtures,
    // some broken on purpose) out.
    private static final Reflections REFLECTIONS = new Reflections(new ConfigurationBuilder()
            .forPackage("me.roboroads.gearth.gpackets")
            .filterInputsBy(new FilterBuilder().includePackage("me.roboroads.gearth.gpackets"))
            .setScanners(Scanners.SubTypes));

    @Test
    void everyOpenEnumHasTheDocumentedShape() throws ReflectiveOperationException {
        Set<Class<? extends OpenIntEnum>> types = REFLECTIONS.getSubTypesOf(OpenIntEnum.class);
        assertFalse(types.isEmpty(), "No OpenIntEnum subclasses found; check the scan package");

        for (Class<? extends OpenIntEnum> type : types) {
            String name = type.getName();
            assertTrue(Modifier.isFinal(type.getModifiers()), name + " is not final");
            for (Constructor<?> constructor : type.getDeclaredConstructors()) {
                assertTrue(Modifier.isPrivate(constructor.getModifiers()), name + " has a constructor that isn't private");
            }

            Method of = type.getMethod("of", int.class);
            assertTrue(Modifier.isStatic(of.getModifiers()), name + ".of(int) is not static");
            assertEquals(type, of.getReturnType(), name + ".of(int) doesn't return " + type.getSimpleName());
            JsonCreator creator = of.getAnnotation(JsonCreator.class);
            assertNotNull(creator, name + ".of(int) has no @JsonCreator");
            assertEquals(JsonCreator.Mode.DELEGATING, creator.mode(), name + ".of(int) is not a delegating @JsonCreator");

            Method values = type.getMethod("values");
            assertTrue(Modifier.isStatic(values.getModifiers()), name + ".values() is not static");
            // Throws when two constants share an id.
            List<?> constants = (List<?>) values.invoke(null);
            assertFalse(constants.isEmpty(), name + " names no ids");
        }
    }
}
