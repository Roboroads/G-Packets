package me.roboroads.gearth.gpackets;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * The fully qualified names of the classes in {@code packageName} and its subpackages at
     * {@code location}, a classes folder or a jar, sorted. For the default package only the classes
     * at the root are listed.
     */
    static List<String> classNames(URL location, String packageName) {
        String packagePath = packageName.replace('.', '/');
        List<String> names = new ArrayList<>();
        try {
            Path path = Paths.get(location.toURI());
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
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Cannot scan " + location + " for @Intercept handlers", e);
        }
        Collections.sort(names);
        return names;
    }

    /** Adds {@code com/example/Foo.class} as {@code com.example.Foo}, skipping module-info and package-info. */
    private static void addClassName(List<String> names, String path) {
        String name = path.substring(0, path.length() - ".class".length()).replace('/', '.');
        if (!name.endsWith("module-info") && !name.endsWith("package-info")) {
            names.add(name);
        }
    }
}
