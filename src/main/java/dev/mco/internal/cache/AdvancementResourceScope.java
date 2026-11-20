package dev.mco.internal.cache;

import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AdvancementResourceScope {
    private static final ThreadLocal<AdvancementResourceScope> CURRENT = new ThreadLocal<>();
    private final Map<Path, FileSystem> archives = new HashMap<>();
    private final Set<Closeable> retained = new HashSet<>();

    private AdvancementResourceScope() {
    }

    public static boolean load(Map<ResourceLocation, Advancement.Builder> advancements) {
        AdvancementResourceScope previous = CURRENT.get();
        AdvancementResourceScope scope = new AdvancementResourceScope();
        CURRENT.set(scope);
        try {
            return ForgeHooks.loadAdvancements(advancements);
        } finally {
            for (FileSystem archive : scope.archives.values()) {
                IOUtils.closeQuietly(archive);
            }
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }

    public static FileSystem open(Path path, ClassLoader loader) throws IOException {
        AdvancementResourceScope scope = CURRENT.get();
        if (scope == null) {
            return FileSystems.newFileSystem(path, loader);
        }
        Path key = path.toAbsolutePath().normalize();
        FileSystem archive = scope.archives.get(key);
        if (archive == null) {
            archive = FileSystems.newFileSystem(path, loader);
            scope.archives.put(key, archive);
            scope.retained.add(archive);
        }
        return archive;
    }

    public static void close(Closeable resource) {
        AdvancementResourceScope scope = CURRENT.get();
        if (scope == null || !scope.retained.contains(resource)) {
            IOUtils.closeQuietly(resource);
        }
    }
}
