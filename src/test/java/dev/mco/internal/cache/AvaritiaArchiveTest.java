package dev.mco.internal.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

final class AvaritiaArchiveTest {
    @TempDir Path root;

    @Test
    void discoversImplicitDirectoriesAndChangesToArchives() throws IOException {
        Path file = root.resolve("recipes.jar");
        String folder = "assets/addon/avaritia_recipes";
        write(file, "assets/addon/textures/a.png");
        assertTrue(AvaritiaArchive.excludes(file.toFile(), folder + "/extreme"));
        write(file, folder + "/extreme/nested/sample.json", folder + "/_factories.json");
        assertFalse(AvaritiaArchive.excludes(file.toFile(), folder + "/extreme"));
        assertFalse(AvaritiaArchive.excludes(file.toFile(), folder + "/_factories.json"));
        assertTrue(AvaritiaArchive.excludes(file.toFile(), folder + "/compressor"));
        assertFalse(AvaritiaArchive.excludes(root.toFile(), folder));
        write(file, "/assets/addon/avaritia_recipes/extreme/sample.json");
        assertFalse(AvaritiaArchive.excludes(file.toFile(), folder + "/extreme"));
    }

    private static void write(Path file, String... paths) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(file))) {
            for (String path : paths) {
                zip.putNextEntry(new ZipEntry(path));
                zip.write(1);
                zip.closeEntry();
            }
        }
    }
}
