package dev.mco.internal.cache;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public final class AvaritiaArchive {
    public static boolean excludes(File source, String name) {
        if (!source.isFile()) return false;
        String prefix = name + "/";
        try (ZipFile archive = new ZipFile(source)) {
            if (archive.getEntry(name) != null) return false;
            var entries = archive.entries();
            while (entries.hasMoreElements()) {
                String entry = entries.nextElement().getName();
                if (entry.startsWith(prefix) || entry.startsWith("/") || entry.contains("//")
                        || entry.startsWith("./") || entry.startsWith("../")
                        || entry.contains("/./") || entry.contains("/../")
                        || entry.endsWith("/.") || entry.endsWith("/..")
                        || entry.equals(".") || entry.equals("..")) return false;
            }
            return true;
        } catch (IOException | IllegalArgumentException error) {
            System.getLogger("mco").log(System.Logger.Level.WARNING, "无法读取配方归档目录 " + source, error);
            return false;
        }
    }

    private AvaritiaArchive() {}
}
