package me.roboroads.gearth.gpackets;

import gearth.protocol.HPacket;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;
import me.roboroads.gearth.gpackets.support.PacketTypes;
import me.roboroads.gearth.gpackets.support.schema.BranchParameter;
import me.roboroads.gearth.gpackets.support.schema.ListParameter;
import me.roboroads.gearth.gpackets.support.schema.OptionalParameter;
import me.roboroads.gearth.gpackets.support.schema.Parameter;
import me.roboroads.gearth.gpackets.support.schema.Schema;
import me.roboroads.gearth.gpackets.support.schema.StructParameter;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PacketImplementationTest {

    @Test
    public void verifyPacketImplementations() {
        Reflections reflections = new Reflections("me.roboroads.gearth.gpackets");
        Set<Class<? extends Packet>> packetClasses = reflections.getSubTypesOf(Packet.class);

        assertFalse(packetClasses.isEmpty(), "No Packet implementations found! Check scan package.");

        for (Class<? extends Packet> clazz : packetClasses) {
            // Skip interfaces and abstract classes
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            // Check fromPacket(HPacket)
            try {
                Method fromPacket = clazz.getDeclaredMethod("fromPacket", HPacket.class);
                assertTrue(Modifier.isStatic(fromPacket.getModifiers()),
                        "Method fromPacket in " + clazz.getName() + " must be static");
                assertTrue(clazz.isAssignableFrom(fromPacket.getReturnType()),
                        "Method fromPacket in " + clazz.getName() + " must return " + clazz.getSimpleName() + " (or subtype), but returns " + fromPacket.getReturnType().getSimpleName());
            } catch (NoSuchMethodException e) {
                fail("Class " + clazz.getName() + " must implement static method: public static " + clazz.getSimpleName() + " fromPacket(HPacket packet)");
            }

            // Check fromJson(String)
            try {
                Method fromJson = clazz.getDeclaredMethod("fromJson", String.class);
                assertTrue(Modifier.isStatic(fromJson.getModifiers()),
                        "Method fromJson in " + clazz.getName() + " must be static");
                assertTrue(clazz.isAssignableFrom(fromJson.getReturnType()),
                        "Method fromJson in " + clazz.getName() + " must return " + clazz.getSimpleName() + " (or subtype), but returns " + fromJson.getReturnType().getSimpleName());
            } catch (NoSuchMethodException e) {
                fail("Class " + clazz.getName() + " must implement static method: public static " + clazz.getSimpleName() + " fromJson(String json)");
            }

            // Check TYPE field
            try {
                java.lang.reflect.Field typeField = clazz.getDeclaredField("TYPE");
                assertTrue(Modifier.isPublic(typeField.getModifiers()), "TYPE field in " + clazz.getName() + " must be public");
                assertTrue(Modifier.isStatic(typeField.getModifiers()), "TYPE field in " + clazz.getName() + " must be static");
                assertTrue(Modifier.isFinal(typeField.getModifiers()), "TYPE field in " + clazz.getName() + " must be final");
                assertEquals(PacketType.class, typeField.getType(), "TYPE field in " + clazz.getName() + " must be of type PacketType");

                PacketType<?> type = (PacketType<?>) typeField.get(null);
                assertNotNull(type, "TYPE field in " + clazz.getName() + " must not be null");
                assertNotNull(type.header(), "TYPE.header() in " + clazz.getName() + " must not be null");
                assertNotNull(type.direction(), "TYPE.direction() in " + clazz.getName() + " must not be null");
                assertNotNull(type.schema(), "TYPE.schema() in " + clazz.getName() + " must not be null");
                assertEquals(clazz, type.schema().type(), "TYPE in " + clazz.getName() + " must use Schema.of(" + clazz.getSimpleName() + ".class)");
                assertTrue(PacketTypes.all().contains(type), clazz.getName() + ".TYPE is missing from PacketTypes.all()");
            } catch (NoSuchFieldException e) {
                fail("Class " + clazz.getName() + " must have a public static final PacketType TYPE field");
            } catch (IllegalAccessException e) {
                fail("TYPE field in " + clazz.getName() + " must be accessible");
            }
        }
    }

    @Test
    public void everySchemaParameterHasAGetterAndABuilderMethod() {
        Set<Schema<?>> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (PacketType<?> type : PacketTypes.all()) {
            checkSchema(type.schema(), seen);
        }
    }

    private static void checkSchema(Schema<?> schema, Set<Schema<?>> seen) {
        if (!seen.add(schema)) {
            return;
        }
        for (Class<?> leaf : leaves(schema)) {
            for (Parameter parameter : parametersOf(schema, leaf)) {
                assertGetter(leaf, parameter.name());
                assertBuilderMethod(leaf, parameter.name());
            }
        }
        for (Schema<?> nested : nestedSchemas(schema)) {
            checkSchema(nested, seen);
        }
    }

    /** The classes objects of this schema are built as: each branch case's subclass, or the schema's own class. */
    private static Set<Class<?>> leaves(Schema<?> schema) {
        Set<Class<?>> leaves = new LinkedHashSet<>();
        for (Parameter parameter : schema.parameters()) {
            if (parameter instanceof BranchParameter) {
                for (BranchParameter.Case c : ((BranchParameter) parameter).cases().values()) {
                    if (c.subclass() != null) {
                        leaves.add(c.subclass());
                    }
                }
            }
        }
        if (leaves.isEmpty()) {
            leaves.add(schema.type());
        }
        return leaves;
    }

    /** The named parameters an object of class {@code leaf} can carry. */
    private static List<Parameter> parametersOf(Schema<?> schema, Class<?> leaf) {
        List<Parameter> named = new ArrayList<>();
        for (Parameter parameter : schema.parameters()) {
            if (parameter instanceof BranchParameter) {
                for (BranchParameter.Case c : ((BranchParameter) parameter).cases().values()) {
                    if (c.subclass() == null || c.subclass() == leaf) {
                        named.addAll(parametersOf(c.schema(), leaf));
                    }
                }
            } else if (parameter instanceof OptionalParameter) {
                named.addAll(parametersOf(((OptionalParameter) parameter).schema(), leaf));
            } else {
                named.add(parameter);
            }
        }
        return named;
    }

    private static List<Schema<?>> nestedSchemas(Schema<?> schema) {
        List<Schema<?>> nested = new ArrayList<>();
        for (Parameter parameter : schema.parameters()) {
            if (parameter instanceof ListParameter && ((ListParameter) parameter).elementSchema() != null) {
                nested.add(((ListParameter) parameter).elementSchema());
            } else if (parameter instanceof StructParameter) {
                nested.add(((StructParameter) parameter).schema());
            } else if (parameter instanceof BranchParameter) {
                for (BranchParameter.Case c : ((BranchParameter) parameter).cases().values()) {
                    nested.addAll(nestedSchemas(c.schema()));
                }
            } else if (parameter instanceof OptionalParameter) {
                nested.addAll(nestedSchemas(((OptionalParameter) parameter).schema()));
            }
        }
        return nested;
    }

    private static void assertGetter(Class<?> type, String name) {
        try {
            type.getMethod(name);
        } catch (NoSuchMethodException e) {
            fail(type.getName() + " has no getter " + name + "() for its schema parameter");
        }
    }

    private static void assertBuilderMethod(Class<?> type, String name) {
        Class<?> builder;
        try {
            builder = type.getMethod("builder").getReturnType();
        } catch (NoSuchMethodException e) {
            fail(type.getName() + " has schema parameter " + name + " but no static builder()");
            return;
        }
        for (Method method : builder.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == 1) {
                return;
            }
        }
        fail(builder.getName() + " has no builder method " + name + "(value) for its schema parameter");
    }
}
