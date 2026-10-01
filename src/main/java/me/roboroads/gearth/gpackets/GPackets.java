package me.roboroads.gearth.gpackets;

import gearth.extensions.ExtensionBase;
import gearth.extensions.IExtension;
import gearth.protocol.HMessage;
import me.roboroads.gearth.gpackets.support.Packet;
import me.roboroads.gearth.gpackets.support.PacketType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Opt-in reflection entry point. Call {@link #init(IExtension)} from your extension to register
 * every method annotated with {@link Intercept} through G-Earth's own {@code intercept(...)}:
 * the extension's own, those of the handler objects you pass, and those of every handler class in
 * the extension's package or below, which init creates itself. If you never call init, nothing is
 * scanned.
 *
 * <p>Calling init twice registers the handlers twice.
 */
public final class GPackets {

    private GPackets() {
    }

    /** Registers the extension's {@link Intercept} handlers and those of the handler classes found next to it. */
    public static void init(IExtension extension) {
        init(extension, new Object[0]);
    }

    /**
     * Registers the {@link Intercept} handlers of the extension, of every extra handler object, and of
     * every handler class in the extension's package or below (in the extension's jar or classes
     * folder), in that order; found classes are sorted by name. A found class is created with a
     * constructor that takes the extension, or else one that takes nothing; a class you pass an
     * instance of is not created. Nothing is registered unless every found class can be created and
     * every annotated method is valid; otherwise an {@link IllegalStateException} names the class or
     * the {@code Class#method} and the problem.
     */
    public static void init(IExtension extension, Object... handlers) {
        List<Object> targets = new ArrayList<>();
        targets.add(extension);
        Set<Class<?>> passed = new HashSet<>();
        if (handlers != null) {
            for (Object handler : handlers) {
                if (handler != null) {
                    targets.add(handler);
                    passed.add(handler.getClass());
                }
            }
        }
        for (Class<?> type : HandlerScanner.handlerClasses(extension.getClass())) {
            if (!passed.contains(type)) {
                targets.add(create(type, extension));
            }
        }

        List<Registration> registrations = new ArrayList<>();
        for (Object target : targets) {
            for (Method method : collectAnnotatedMethods(target.getClass())) {
                registrations.add(validate(target, method));
            }
        }

        for (Registration registration : registrations) {
            registration.register(extension);
        }
    }

    /** Creates a found handler with a constructor that takes the extension, or else one that takes nothing. */
    private static Object create(Class<?> type, IExtension extension) {
        Constructor<?> chosen = null;
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            if (parameters.length == 1 && parameters[0].isInstance(extension)) {
                chosen = constructor;
                break;
            }
        }
        if (chosen == null) {
            try {
                chosen = type.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(type.getName() + ": found @Intercept methods but no constructor that takes"
                        + " nothing or the extension; create it yourself and pass it to GPackets.init");
            }
        }
        chosen.setAccessible(true);
        try {
            return chosen.getParameterCount() == 1 ? chosen.newInstance(extension) : chosen.newInstance();
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(type.getName() + ": its constructor threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(type.getName() + ": cannot create it", e);
        }
    }

    static List<Method> collectAnnotatedMethods(Class<?> type) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class && !c.getName().startsWith("gearth."); c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Intercept.class)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    private static Registration validate(Object target, Method method) {
        String where = method.getDeclaringClass().getName() + "#" + method.getName();

        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException(where + ": @Intercept method must not be static");
        }
        if (method.getReturnType() != void.class) {
            throw new IllegalStateException(where + ": @Intercept method must return void");
        }

        Class<?>[] params = method.getParameterTypes();
        if (params.length == 0) {
            throw new IllegalStateException(where + ": @Intercept method parameters must be (packet), (packet, HMessage) or (HMessage)");
        }
        boolean trailingMessage = params[params.length - 1] == HMessage.class;
        int packetParamCount = params.length - (trailingMessage ? 1 : 0);
        if (packetParamCount > 1) {
            throw new IllegalStateException(where + ": @Intercept method parameters must be (packet), (packet, HMessage) or (HMessage)");
        }
        Class<?> packetParam = packetParamCount == 1 ? params[0] : null;

        Class<? extends Packet>[] packetClasses = method.getAnnotation(Intercept.class).value();
        List<PacketType<?>> types = new ArrayList<>();
        if (packetClasses.length > 0) {
            if (packetParam != null && !acceptsAll(packetParam, packetClasses)) {
                throw new IllegalStateException(where + ": @Intercept packet parameter must accept every listed packet class (use Packet)");
            }
            for (Class<? extends Packet> packetClass : packetClasses) {
                types.add(typeOf(packetClass, where));
            }
        } else {
            if (packetParam == null || packetParam == Packet.class || !Packet.class.isAssignableFrom(packetParam)) {
                throw new IllegalStateException(where + ": @Intercept with no value must take a concrete packet type as its first parameter");
            }
            @SuppressWarnings("unchecked")
            Class<? extends Packet> inferred = (Class<? extends Packet>) packetParam;
            types.add(typeOf(inferred, where));
        }

        Shape shape = packetParam == null
                ? Shape.MESSAGE_ONLY
                : (trailingMessage ? Shape.PACKET_AND_MESSAGE : Shape.PACKET);

        method.setAccessible(true);
        MethodHandle handle;
        try {
            handle = MethodHandles.lookup().unreflect(method).bindTo(target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(where + ": cannot access @Intercept method", e);
        }
        return new Registration(handle, shape, types);
    }

    private static PacketType<?> typeOf(Class<? extends Packet> packetClass, String where) {
        Object value;
        try {
            value = packetClass.getField("TYPE").get(null);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(where + ": " + packetClass.getName() + " has no public static final PacketType TYPE");
        } catch (IllegalAccessException | NullPointerException e) {
            throw new IllegalStateException(where + ": " + packetClass.getName() + " TYPE must be a public static field", e);
        }
        if (!(value instanceof PacketType)) {
            throw new IllegalStateException(where + ": " + packetClass.getName() + " TYPE must be a PacketType");
        }
        return (PacketType<?>) value;
    }

    private static boolean acceptsAll(Class<?> param, Class<? extends Packet>[] packetClasses) {
        if (!Packet.class.isAssignableFrom(param)) {
            return false;
        }
        for (Class<? extends Packet> packetClass : packetClasses) {
            if (!param.isAssignableFrom(packetClass)) {
                return false;
            }
        }
        return true;
    }

    private enum Shape {
        PACKET, PACKET_AND_MESSAGE, MESSAGE_ONLY
    }

    private static final class Registration {
        private final MethodHandle handle;
        private final Shape shape;
        private final List<PacketType<?>> types;

        Registration(MethodHandle handle, Shape shape, List<PacketType<?>> types) {
            this.handle = handle;
            this.shape = shape;
            this.types = types;
        }

        void register(IExtension extension) {
            for (PacketType<?> type : types) {
                ExtensionBase.MessageListener listener = message -> invoke(type, message);
                extension.intercept(type.direction(), type.header(), listener);
            }
        }

        private void invoke(PacketType<?> type, HMessage message) {
            List<Object> args = new ArrayList<>(2);
            if (shape != Shape.MESSAGE_ONLY) {
                args.add(type.parse(message.getPacket()));
            }
            if (shape == Shape.PACKET_AND_MESSAGE || shape == Shape.MESSAGE_ONLY) {
                args.add(message);
            }
            try {
                handle.invokeWithArguments(args);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }
}
