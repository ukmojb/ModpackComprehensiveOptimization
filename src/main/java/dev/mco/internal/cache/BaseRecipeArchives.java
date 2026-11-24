package dev.mco.internal.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipFile;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.ModContainer;

public final class BaseRecipeArchives {
    private static final String DIRECTORY = "/base/recipe_system";
    private final ConcurrentHashMap<Path, Optional<Set<String>>> archives = new ConcurrentHashMap<>();

    public boolean needsScan(ModContainer mod) {
        if (mod.getClass() != FMLModContainer.class) return true;
        File source = mod.getResource();
        if (source == null || !source.isFile()) return true;
        String id = mod.getModId();
        if (id.indexOf('/') >= 0 || id.indexOf('\\') >= 0 || id.equals(".") || id.equals("..") || id.isEmpty()) return true;
        try {
            var namespaces = archives.computeIfAbsent(source.toPath().toRealPath(), BaseRecipeArchives::read);
            return namespaces.isEmpty() || namespaces.get().contains(id);
        } catch (IOException error) {
            System.getLogger("mco").log(System.Logger.Level.WARNING, "无法解析配方来源 " + source, error);
            return true;
        }
    }

    private static Optional<Set<String>> read(Path source) {
        Set<String> namespaces = new HashSet<>();
        try (var zip = new ZipFile(source.toFile())) {
            var entries = zip.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith("/") || name.indexOf('\\') >= 0 || name.contains("//")
                        || name.startsWith("./") || name.startsWith("../") || name.contains("/./")
                        || name.contains("/../") || name.endsWith("/.") || name.endsWith("/..")
                        || name.equals(".") || name.equals("..")) return Optional.empty();
                if (!name.startsWith("assets/")) continue;
                int end = name.indexOf('/', 7);
                if (end < 0 || !name.startsWith(DIRECTORY, end)) continue;
                int suffix = end + DIRECTORY.length();
                if (suffix == name.length() || name.charAt(suffix) == '/') namespaces.add(name.substring(7, end));
            }
            return Optional.of(Set.copyOf(namespaces));
        } catch (IOException | IllegalArgumentException error) {
            System.getLogger("mco").log(System.Logger.Level.WARNING, "无法读取配方归档 " + source, error);
            return Optional.empty();
        }
    }
}
