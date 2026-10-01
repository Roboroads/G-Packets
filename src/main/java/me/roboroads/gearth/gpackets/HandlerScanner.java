package me.roboroads.gearth.gpackets;

import gearth.extensions.IExtension;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Finds handler classes for {@link GPackets#init}: classes with an {@link Intercept} method in the
 * extension's package or below, in the same jar or classes folder as the extension.
 */
final class HandlerScanner {

    private HandlerScanner() {
    }

    /**
     * The handler classes next to {@code extensionClass}, sorted by name: concrete classes with an
     * {@link Intercept} method in its package or below, in its jar or classes folder, that are not
     * extensions themselves. Empty when the class has no {@code file:} code source.
     */
    static List<Class<?>> handlerClasses(Class<?> extensionClass) {
        CodeSource source = extensionClass.getProtectionDomain().getCodeSource();
        if (source == null || source.getLocation() == null || !"file".equals(source.getLocation().getProtocol())) {
            return Collections.emptyList();
        }
        String name = extensionClass.getName();
        String packageName = name.lastIndexOf('.') < 0 ? "" : name.substring(0, name.lastIndexOf('.'));
        List<Class<?>> handlers = new ArrayList<>();
        for (String className : classNames(source.getLocation(), packageName)) {
            Class<?> handler = loadHandler(className, extensionClass.getClassLoader());
            if (handler != null) {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    /**
     * The class if it is a handler, otherwise null. A class that fails to load or link is skipped:
     * it could not run as a handler, and an unrelated broken class must not break init. So is a
     * nested class from Scala or Groovy whose name Java 8 can't parse: there isAnonymousClass throws
     * {@code InternalError("Malformed class name")}.
     */
    static Class<?> loadHandler(String className, ClassLoader loader) {
        try {
            Class<?> type = Class.forName(className, false, loader);
            int modifiers = type.getModifiers();
            if (type.isInterface() || type.isEnum() || type.isAnnotation() || type.isAnonymousClass() || type.isLocalClass()
                    || Modifier.isAbstract(modifiers) || IExtension.class.isAssignableFrom(type)) {
                return null;
            }
            return GPackets.collectAnnotatedMethods(type).isEmpty() ? null : type;
        } catch (ClassNotFoundException | LinkageError | InternalError e) {
            return null;
        }
    }

    /**
     * The fully qualified names of the classes in {@code packageName} and its subpackages at
     * {@code location}, a classes folder or a jar, sorted. For the default package only the classes
     * at the root are listed.
     */
    static List<String> classNames(URL location, String packageName) {
        String packagePath = packageName.replace('.', '/');
        List<String> names = new ArrayList<>();
        try {
            Path path = toPath(location);
            if (Files.isDirectory(path)) {
                Path root = packagePath.isEmpty() ? path : path.resolve(packagePath);
                if (Files.isDirectory(root)) {
                    try (Stream<Path> files = packagePath.isEmpty() ? Files.list(root) : Files.walk(root)) {
                        files.filter(file -> file.toString().endsWith(".class"))
                                .forEach(file -> addClassName(names, path.relativize(file).toString().replace(File.separatorChar, '/')));
                    }
                }
            } else {
                String prefix = packagePath.isEmpty() ? "" : packagePath + "/";
                try (JarFile jar = new JarFile(path.toFile())) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        String entry = entries.nextElement().getName();
                        boolean inPackage = packagePath.isEmpty() ? entry.indexOf('/') < 0 : entry.startsWith(prefix);
                        if (inPackage && entry.endsWith(".class")) {
                            addClassName(names, entry);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot scan " + location + " for @Intercept handlers", e);
        }
        Collections.sort(names);
        return names;
    }

    /** The file at a {@code file:} URL, also when the URL leaves spaces unescaped, as {@code File.toURL()} does. */
    private static Path toPath(URL location) {
        try {
            return Paths.get(location.toURI());
        } catch (URISyntaxException e) {
            return new File(location.getPath()).toPath();
        }
    }

    /** Adds {@code com/example/Foo.class} as {@code com.example.Foo}, skipping module-info and package-info. */
    private static void addClassName(List<String> names, String path) {
        String name = path.substring(0, path.length() - ".class".length()).replace('/', '.');
        if (!name.endsWith("module-info") && !name.endsWith("package-info")) {
            names.add(name);
        }
    }
}
