package dev.mco.internal.cache;

import dev.mco.ModpackComprehensiveOptimization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JAOPCAResourceCache {

    private static volatile Map<ResourceLocation, Boolean> cache = new ConcurrentHashMap<>();

    private JAOPCAResourceCache() {
    }

    public static boolean hasResource(ResourceLocation location) {
        if (location == null) {
            return false;
        }
        return cache.computeIfAbsent(location, JAOPCAResourceCache::resourceExists);
    }

    private static boolean resourceExists(ResourceLocation location) {
        try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(location)) {
            return resource != null;
        } catch (FileNotFoundException missing) {
            return false;
        } catch (IOException failure) {
            ModpackComprehensiveOptimization.LOGGER.warn("读取 JAOPCA 资源 {} 失败", location, failure);
            return false;
        }
    }

    public static void clear() {
        cache = new ConcurrentHashMap<>();
    }
}
