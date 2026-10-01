package me.roboroads.gearth.gpackets.support.schema;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Converts between values and typed objects through Lombok's API: the static {@code builder()} and
 * its methods to build, the fluent getters to read. Method lookups are cached per class.
 */
final class Binder {
    private static final Map<String, Method> METHODS = new ConcurrentHashMap<>();

    private Binder() {
    }

    static <T> T bind(Schema<T> schema, Map<String, Object> values) {
        Class<?> target = targetClass(schema, values, schema.type());
        Method builderMethod = builderMethod(target);
        if (builderMethod == null) {
            if (!values.isEmpty()) {
                throw new IllegalStateException(target.getName() + " has no builder() to set " + values.keySet());
            }
            return schema.type().cast(construct(target));
        }
        Object builder = invoke(builderMethod, null);
        apply(schema, values, builder);
        return schema.type().cast(invoke(require(builder.getClass(), "build", 0), builder));
    }

    static Map<String, Object> unbind(Schema<?> schema, Object object) {
        return unbindAt(schema, object, schema.type().getSimpleName());
    }

    private static Class<?> targetClass(Schema<?> schema, Map<String, Object> values, Class<?> current) {
        for (Parameter parameter : schema.parameters()) {
            if (parameter instanceof BranchParameter) {
                BranchParameter branch = (BranchParameter) parameter;
                BranchParameter.Case match = branch.caseFor(values.get(branch.on()), schema.type().getSimpleName());
                if (match != null) {
                    current = targetClass(match.schema(), values, match.subclass() != null ? match.subclass() : current);
                }
            } else if (parameter instanceof OptionalParameter) {
                current = targetClass(((OptionalParameter) parameter).schema(), values, current);
            }
        }
        return current;
    }

    private static void apply(Schema<?> schema, Map<String, Object> values, Object builder) {
        for (Parameter parameter : schema.parameters()) {
            if (parameter instanceof BranchParameter) {
                BranchParameter branch = (BranchParameter) parameter;
                BranchParameter.Case match = branch.caseFor(values.get(branch.on()), schema.type().getSimpleName());
                if (match != null) {
                    apply(match.schema(), values, builder);
                }
            } else if (parameter instanceof OptionalParameter) {
                apply(((OptionalParameter) parameter).schema(), values, builder);
            } else if (values.get(parameter.name()) != null) {
                Method setter = require(builder.getClass(), parameter.name(), 1);
                Object field = toField(parameter, values.get(parameter.name()));
                try {
                    invoke(setter, builder, field);
                } catch (IllegalArgumentException e) {
                    // The schema's wire type does not fit the field, e.g. shortValue for an Integer field.
                    throw new IllegalArgumentException(schema.type().getSimpleName() + "." + parameter.name()
                            + ": cannot set a " + (field == null ? "null" : field.getClass().getName()) + " on " + setter, e);
                }
            }
        }
    }

    private static Object toField(Parameter parameter, Object value) {
        if (parameter instanceof ValueParameter) {
            ValueParameter valueParameter = (ValueParameter) parameter;
            return valueParameter.enumType() != null ? valueParameter.toEnum(value) : value;
        }
        if (parameter instanceof ListParameter) {
            ListParameter list = (ListParameter) parameter;
            List<Object> elements = new ArrayList<>();
            for (Object element : (List<?>) value) {
                elements.add(list.elementType() != null ? element : bind(list.elementSchema(), Parameter.asValues(element, "")));
            }
            return elements;
        }
        if (parameter instanceof StructParameter) {
            return bind(((StructParameter) parameter).schema(), Parameter.asValues(value, ""));
        }
        throw new IllegalStateException("Unknown parameter kind " + parameter.getClass().getName());
    }

    private static Map<String, Object> unbindAt(Schema<?> schema, Object object, String path) {
        Map<String, Object> values = new LinkedHashMap<>();
        unbindInto(schema, object, values, path);
        return values;
    }

    private static void unbindInto(Schema<?> schema, Object object, Map<String, Object> values, String path) {
        for (Parameter parameter : schema.parameters()) {
            if (parameter instanceof BranchParameter) {
                BranchParameter.Case match = pickCase((BranchParameter) parameter, object, values, path);
                if (match != null) {
                    unbindInto(match.schema(), object, values, path);
                }
            } else if (parameter instanceof OptionalParameter) {
                unbindInto(((OptionalParameter) parameter).schema(), object, values, path);
            } else {
                Object field = invoke(require(object.getClass(), parameter.name(), 0), object);
                values.put(parameter.name(), fromField(parameter, field, path + "." + parameter.name()));
            }
        }
    }

    /**
     * For a branch with subclasses, a set discriminator picks the case and the object must be an
     * instance of its subclass; a null discriminator is filled in from the first case whose subclass
     * the object is. For {@code when(...)}, the discriminator's value picks the case.
     */
    private static BranchParameter.Case pickCase(BranchParameter branch, Object object, Map<String, Object> values, String path) {
        boolean bySubclass = false;
        for (BranchParameter.Case c : branch.cases().values()) {
            bySubclass |= c.subclass() != null;
        }
        if (!bySubclass) {
            return branch.caseFor(values.get(branch.on()), path);
        }

        Object current = values.get(branch.on());
        if (current != null) {
            BranchParameter.Case match = branch.caseFor(current, path);
            if (!match.subclass().isInstance(object)) {
                throw new IllegalArgumentException(path + "." + branch.on() + ": is " + current
                        + " but the object is a " + object.getClass().getSimpleName());
            }
            return match;
        }
        for (BranchParameter.Case c : branch.cases().values()) {
            if (c.subclass().isInstance(object)) {
                values.put(branch.on(), c.value());
                return c;
            }
        }
        throw new IllegalArgumentException(path + "." + branch.on() + ": no case for " + object.getClass().getName());
    }

    private static Object fromField(Parameter parameter, Object field, String path) {
        if (field == null) {
            return null;
        }
        if (parameter instanceof ValueParameter) {
            return field instanceof IntEnum || field instanceof StringEnum ? ValueParameter.wireValue(field) : field;
        }
        if (parameter instanceof ListParameter) {
            ListParameter list = (ListParameter) parameter;
            List<Object> elements = new ArrayList<>();
            int i = 0;
            for (Object element : (List<?>) field) {
                String at = path + "[" + i++ + "]";
                elements.add(list.elementType() != null || element == null ? element : unbindAt(list.elementSchema(), element, at));
            }
            return elements;
        }
        if (parameter instanceof StructParameter) {
            return unbindAt(((StructParameter) parameter).schema(), field, path);
        }
        throw new IllegalStateException("Unknown parameter kind " + parameter.getClass().getName());
    }

    /**
     * The class's static {@code builder()}, or null. A subclass also inherits its parent's
     * {@code builder()}; {@link Class#getMethod} picks the one with the most specific return type.
     */
    private static Method builderMethod(Class<?> type) {
        String key = type.getName() + "#builder";
        Method cached = METHODS.get(key);
        if (cached != null) {
            return cached;
        }
        try {
            Method method = type.getMethod("builder");
            method.setAccessible(true);
            METHODS.put(key, method);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /** The public, non-bridge method with this name and parameter count, or null. */
    private static Method find(Class<?> type, String name, int parameterCount) {
        String key = type.getName() + '#' + name + '/' + parameterCount;
        Method cached = METHODS.get(key);
        if (cached != null) {
            return cached;
        }
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount && !method.isBridge()) {
                method.setAccessible(true);
                METHODS.put(key, method);
                return method;
            }
        }
        return null;
    }

    private static Method require(Class<?> type, String name, int parameterCount) {
        Method method = find(type, name, parameterCount);
        if (method == null) {
            throw new IllegalStateException(type.getName() + " has no method " + name + " with " + parameterCount + " parameter(s)");
        }
        return method;
    }

    private static Object invoke(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call " + method, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException(cause);
        }
    }

    private static Object construct(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(type.getName() + " has neither builder() nor a no-arg constructor", e);
        }
    }
}
