package me.roboroads.gearth.gpackets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
