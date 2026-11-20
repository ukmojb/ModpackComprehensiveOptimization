package dev.mco.internal.cache;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JAOPCAResourceCache {

    private static final Map<ResourceLocation, Boolean> CACHE = new ConcurrentHashMap<>();

    private JAOPCAResourceCache() {
    }

    public static boolean hasResource(ResourceLocation location) {
        if (location == null) {
            return false;
        }
        Boolean cached = CACHE.get(location);
        if (cached != null) {
            return cached;
        }
        boolean exists = resourceExists(location);
        CACHE.putIfAbsent(location, exists);
        return exists;
    }

    private static boolean resourceExists(ResourceLocation location) {
        try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(location)) {
            return resource != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void clear() {
        CACHE.clear();
    }
}
