package dev.mco.internal.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;

public final class LibNineModelPaths {
    private record Key(String namespace, String path) {}
    private static final AtomicReference<LibNineModelPaths> ACTIVE = new AtomicReference<>();
    private final ConcurrentHashMap<Key, ResourceLocation> paths = new ConcurrentHashMap<>();

    public static <T> T duringBake(Supplier<T> bake) {
        var session = new LibNineModelPaths();
        ACTIVE.set(session);
        try {
            return bake.get();
        } finally {
            ACTIVE.compareAndSet(session, null);
        }
    }

    public static ResourceLocation resolve(ResourceLocation location, Supplier<ResourceLocation> original) {
        var session = ACTIVE.get();
        if (session == null) return original.get();
        var key = new Key(location.getNamespace(), location.getPath());
        var result = session.paths.get(key);
        if (result != null) return result;
        return session.paths.computeIfAbsent(key, ignored -> original.get());
    }

    private LibNineModelPaths() {}
}
