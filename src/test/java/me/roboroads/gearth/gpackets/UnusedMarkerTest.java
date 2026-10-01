package me.roboroads.gearth.gpackets;

import me.roboroads.gearth.gpackets.incoming.RoomSettingsError;
import me.roboroads.gearth.gpackets.incoming.sub.catalog.Offer;
import me.roboroads.gearth.gpackets.incoming.sub.user.Player;
import me.roboroads.gearth.gpackets.incoming.sub.user.User;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Every {@link Unused} mark is paired with {@link Deprecated}, so extensions get a warning. */
class UnusedMarkerTest {

    // forPackage only picks the classpath roots; the filter keeps other packages (test fixtures) out.
    private static final Reflections REFLECTIONS = new Reflections(new ConfigurationBuilder()
            .forPackage("me.roboroads.gearth.gpackets")
            .filterInputsBy(new FilterBuilder().includePackage("me.roboroads.gearth.gpackets"))
            .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated, Scanners.FieldsAnnotated));

    @Test
    void everyUnusedFieldIsDeprecatedAndSoAreItsAccessors() {
        Set<Field> fields = REFLECTIONS.getFieldsAnnotatedWith(Unused.class);
        assertFalse(fields.isEmpty(), "No @Unused fields found; check the scan package");

        for (Field field : fields) {
            String where = field.getDeclaringClass().getName() + "." + field.getName();
            assertTrue(field.isAnnotationPresent(Deprecated.class), where + " has @Unused but not @Deprecated");
            assertFalse(field.getAnnotation(Unused.class).value().trim().isEmpty(), where + " has @Unused without a reason");
            if (field.getDeclaringClass().isEnum()) {
                continue;
            }
            assertTrue(getter(field).isAnnotationPresent(Deprecated.class), where + ": the getter is not @Deprecated");
            Method builderMethod = builderMethod(field);
            if (builderMethod != null) {
                assertTrue(builderMethod.isAnnotationPresent(Deprecated.class), where + ": the builder method is not @Deprecated");
            }
        }
    }

    @Test
    void everyUnusedClassIsADeprecatedPacket() {
        for (Class<?> type : REFLECTIONS.getTypesAnnotatedWith(Unused.class, true)) {
            assertTrue(type.isAnnotationPresent(Deprecated.class), type.getName() + " has @Unused but not @Deprecated");
            assertFalse(type.getAnnotation(Unused.class).value().trim().isEmpty(), type.getName() + " has @Unused without a reason");
            assertTrue(Packet.class.isAssignableFrom(type), type.getName() + ": @Unused on a class is only for packets");
        }
    }

    @Test
    void theClientIgnoresTheseParameters() {
        assertEquals("The client stores it but never reads it", parameter(Offer.SCHEMA, "unknownBoolean12").unused());
        assertEquals("The client copies it into its user data but nothing reads it", parameter(playerSchema(), "groupStatus").unused());
    }

    @Test
    @SuppressWarnings("deprecation")
    void theClientIgnoresThesePackets() {
        assertEquals("The client's handler takes the packet and does nothing with it", RoomSettingsError.TYPE.unused());
    }

    private static Method getter(Field field) {
        try {
            return field.getDeclaringClass().getMethod(field.getName());
        } catch (NoSuchMethodException e) {
            throw new AssertionError(field.getDeclaringClass().getName() + "." + field.getName() + " has no getter", e);
        }
    }

    /** The builder method Lombok generates for the field, or null when the class has no builder. */
    private static Method builderMethod(Field field) {
        Method builder;
        try {
            builder = field.getDeclaringClass().getMethod("builder");
        } catch (NoSuchMethodException e) {
            return null;
        }
        try {
            return builder.getReturnType().getMethod(field.getName(), field.getType());
        } catch (NoSuchMethodException e) {
            throw new AssertionError(field.getDeclaringClass().getName() + "." + field.getName() + " has no builder method", e);
        }
    }

    private static Parameter parameter(Schema<?> schema, String name) {
        for (Parameter parameter : schema.parameters()) {
            if (name.equals(parameter.name())) {
                return parameter;
            }
        }
        throw new AssertionError(schema.type().getSimpleName() + " has no parameter " + name);
    }

    private static Schema<?> playerSchema() {
        for (Parameter parameter : User.SCHEMA.parameters()) {
            if (parameter instanceof BranchParameter) {
                for (BranchParameter.Case c : ((BranchParameter) parameter).cases().values()) {
                    if (c.subclass() == Player.class) {
                        return c.schema();
                    }
                }
            }
        }
        throw new AssertionError("User.SCHEMA has no Player case");
    }
}
