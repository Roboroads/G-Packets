package me.roboroads.gearth.gpackets.support.schema;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;

/**
 * An int code that reads like an enum but keeps ids it doesn't name, for values the client gets
 * as data (new ones appear without a client update). The named ids are the subclass's
 * {@code public static final} fields of its own type. {@link #of} returns that field for a named
 * id, so {@code ==} works for those, and a new instance for any other id, so a read and a write
 * never lose it. A subclass looks like this:
 *
 * <pre>{@code
 * public final class ChatBarStyle extends OpenIntEnum {
 *     public static final ChatBarStyle DEFAULT = new ChatBarStyle(0);
 *
 *     private ChatBarStyle(int value) {
 *         super(value);
 *     }
 *
 *     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
 *     public static ChatBarStyle of(int value) {
 *         return of(ChatBarStyle.class, value, ChatBarStyle::new);
 *     }
 *
 *     public static List<ChatBarStyle> values() {
 *         return values(ChatBarStyle.class);
 *     }
 * }
 * }</pre>
 */
public abstract class OpenIntEnum implements IntEnum {
    private static final Map<Class<?>, Registry> REGISTRIES = new ConcurrentHashMap<>();

    private final int value;

    protected OpenIntEnum(int value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public final int value() {
        return value;
    }

    /** The name of the constant with this id, or null when the class doesn't name it. */
    public final String name() {
        return registry(getClass()).names.get(value);
    }

    /** Whether the class names this id. */
    public final boolean known() {
        return name() != null;
    }

    /**
     * The named constant with this id, or a new {@code unknown.apply(value)} that keeps it. Unnamed
     * instances aren't cached, so compare them with {@code equals}.
     */
    protected static <E extends OpenIntEnum> E of(Class<E> type, int value, IntFunction<E> unknown) {
        OpenIntEnum named = registry(type).byValue.get(value);
        return named != null ? type.cast(named) : unknown.apply(value);
    }

    /** The named constants, sorted by value. */
    protected static <E extends OpenIntEnum> List<E> values(Class<E> type) {
        List<E> values = new ArrayList<>();
        for (OpenIntEnum constant : registry(type).byValue.values()) {
            values.add(type.cast(constant));
        }
        return Collections.unmodifiableList(values);
    }

    /** The named constants by name, sorted by value. */
    static Map<String, OpenIntEnum> constants(Class<?> type) {
        Registry registry = registry(type);
        Map<String, OpenIntEnum> constants = new LinkedHashMap<>();
        for (OpenIntEnum constant : registry.byValue.values()) {
            constants.put(registry.names.get(constant.value), constant);
        }
        return constants;
    }

    /** The named constant with this id, or what the type's own {@code of(int)} makes of it. */
    static OpenIntEnum fromWire(Class<?> type, int value) {
        Registry registry = registry(type);
        OpenIntEnum named = registry.byValue.get(value);
        if (named != null) {
            return named;
        }
        if (registry.of == null) {
            throw new IllegalStateException(type.getName() + " needs a public static of(int) to keep id " + value);
        }
        try {
            return (OpenIntEnum) registry.of.invoke(null, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot call " + registry.of, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw cause instanceof RuntimeException ? (RuntimeException) cause : new IllegalStateException(cause);
        }
    }

    @Override
    public final boolean equals(Object other) {
        return other != null && other.getClass() == getClass() && ((OpenIntEnum) other).value == value;
    }

    @Override
    public final int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        String name = name();
        return name != null ? name : getClass().getSimpleName() + "(" + value + ")";
    }

    // get-then-put rather than computeIfAbsent: building reads static fields, which can initialize
    // the class, and a nested registry lookup inside computeIfAbsent is not allowed.
    private static Registry registry(Class<?> type) {
        Registry registry = REGISTRIES.get(type);
        if (registry == null) {
            registry = build(type);
            REGISTRIES.put(type, registry);
        }
        return registry;
    }

    private static Registry build(Class<?> type) {
        Registry registry = new Registry();
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (field.getType() != type || !Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                continue;
            }
            OpenIntEnum constant;
            try {
                field.setAccessible(true);
                constant = (OpenIntEnum) field.get(null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot read " + type.getName() + "." + field.getName(), e);
            }
            if (constant == null) {
                throw new IllegalStateException(type.getName() + "." + field.getName() + " is null; was it read while the class was still initializing?");
            }
            String clash = registry.names.get(constant.value);
            if (clash != null) {
                throw new IllegalStateException(type.getName() + "." + clash + " and " + field.getName()
                        + " both have the value " + constant.value);
            }
            registry.byValue.put(constant.value, constant);
            registry.names.put(constant.value, field.getName());
        }
        try {
            registry.of = type.getMethod("of", int.class);
        } catch (NoSuchMethodException e) {
            registry.of = null;
        }
        return registry;
    }

    private static final class Registry {
        final Map<Integer, OpenIntEnum> byValue = new TreeMap<>();
        final Map<Integer, String> names = new HashMap<>();
        Method of;
    }
}
