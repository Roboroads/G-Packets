package me.roboroads.gearth.gpackets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testfixtures.discovery.ConcreteChild;
import testfixtures.discovery.DiscoveryExtension;
import testfixtures.discovery.ExtensionArgHandler;
import testfixtures.discovery.NoArgHandler;
import testfixtures.discovery.PassedHandler;
import testfixtures.discovery.sub.NestedPackageHandler;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HandlerScannerTest {

    private static final List<String> ENTRIES = Arrays.asList(
            "com/example/A.class",
            "com/example/A$Inner.class",
            "com/example/sub/B.class",
            "com/example/package-info.class",
            "com/example/notes.txt",
            "com/other/C.class",
            "module-info.class",
            "Root.class");

    private static final List<String> EXAMPLE_CLASSES = Arrays.asList("com.example.A", "com.example.A$Inner", "com.example.sub.B");

    private static Path folder(Path dir) throws IOException {
        for (String entry : ENTRIES) {
            Path file = dir.resolve(entry);
            Files.createDirectories(file.getParent());
            Files.write(file, new byte[0]);
        }
        return dir;
    }

    private static Path jar(Path file) throws IOException {
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(file))) {
            for (String entry : ENTRIES) {
                out.putNextEntry(new JarEntry(entry));
                out.closeEntry();
            }
        }
        return file;
    }

    @Test
    void listsThePackageAndItsSubpackagesInAFolder(@TempDir Path dir) throws IOException {
        assertEquals(EXAMPLE_CLASSES, HandlerScanner.classNames(folder(dir).toUri().toURL(), "com.example"));
    }

    @Test
    void listsThePackageAndItsSubpackagesInAJar(@TempDir Path dir) throws IOException {
        assertEquals(EXAMPLE_CLASSES, HandlerScanner.classNames(jar(dir.resolve("ext.jar")).toUri().toURL(), "com.example"));
    }

    @Test
    void theDefaultPackageListsOnlyRootClasses(@TempDir Path dir) throws IOException {
        Path classes = Files.createDirectory(dir.resolve("classes"));

        assertEquals(Collections.singletonList("Root"), HandlerScanner.classNames(folder(classes).toUri().toURL(), ""));
        assertEquals(Collections.singletonList("Root"), HandlerScanner.classNames(jar(dir.resolve("ext.jar")).toUri().toURL(), ""));
    }

    @Test
    void aPackageThatIsNotThereListsNothing(@TempDir Path dir) throws IOException {
        assertEquals(Collections.emptyList(), HandlerScanner.classNames(folder(dir).toUri().toURL(), "com.missing"));
    }

    @Test
    void findsTheHandlerClassesNextToTheExtension() {
        assertEquals(Arrays.<Class<?>>asList(
                        ConcreteChild.class,
                        ExtensionArgHandler.class,
                        NoArgHandler.class,
                        PassedHandler.class,
                        NestedPackageHandler.class),
                HandlerScanner.handlerClasses(DiscoveryExtension.class));
    }

    @Test
    void findsNothingWithoutACodeSource() {
        assertEquals(Collections.emptyList(), HandlerScanner.handlerClasses(String.class));
    }

    @Test
    void aClassThatFailsToLoadIsSkipped(@TempDir Path dir) throws IOException {
        Path broken = dir.resolve("com/example/Broken.class");
        Files.createDirectories(broken.getParent());
        Files.write(broken, new byte[]{1, 2, 3});

        try (URLClassLoader loader = new URLClassLoader(new URL[]{dir.toUri().toURL()}, null)) {
            assertNull(HandlerScanner.loadHandler("com.example.Broken", loader));
        }
    }

    @Test
    void aClassWithANameJava8CannotParseIsSkipped(@TempDir Path dir) throws Exception {
        // On Java 8, isAnonymousClass throws InternalError("Malformed class name") for Mal_X
        copyRenamed(dir, "testfixtures/malformed/Mal.class");
        copyRenamed(dir, "testfixtures/malformed/Mal$X.class");

        try (URLClassLoader loader = new URLClassLoader(new URL[]{dir.toUri().toURL()}, null)) {
            assertNull(HandlerScanner.loadHandler("testfixtures.malformed.Mal_X", loader));
        }
    }

    /** Copies a compiled test class into {@code dir}, renaming Mal$X to Mal_X; same length, so the class file stays valid. */
    private static void copyRenamed(Path dir, String resource) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(HandlerScannerTest.class.getResource("/" + resource).toURI()));
        String renamed = new String(bytes, StandardCharsets.ISO_8859_1).replace("Mal$X", "Mal_X");
        Path file = dir.resolve(resource.replace("Mal$X", "Mal_X"));
        Files.createDirectories(file.getParent());
        Files.write(file, renamed.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Test
    @SuppressWarnings("deprecation")
    void aFolderUrlWithUnescapedSpacesIsListed(@TempDir Path dir) throws IOException {
        // File.toURL() doesn't escape spaces, and a class loader built with it reports that URL as the code source
        Path classes = folder(Files.createDirectory(dir.resolve("my classes")));

        assertEquals(EXAMPLE_CLASSES, HandlerScanner.classNames(classes.toFile().toURL(), "com.example"));
    }
}
