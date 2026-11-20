package dev.mco.internal.cache;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.city.CityStructureManager;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public final class NetherCityCache {
    private static final Map<World, NetherCityCache> WORLDS = new WeakHashMap<>();

    private CityStructureManager active;
    private SoftReference<CityStructureManager> idle = new SoftReference<>(null);
    private long lastUse;

    static {
        MinecraftForge.EVENT_BUS.register(NetherCityCache.class);
    }

    private NetherCityCache() {}

    public static synchronized CityStructureManager forGeneration(World world) {
        if (world.isRemote || !ConfigLoader.hasCities()) return null;
        NetherCityCache cache = WORLDS.computeIfAbsent(world, ignored -> new NetherCityCache());
        cache.lastUse = System.nanoTime();
        if (cache.active == null) {
            cache.active = cache.idle.get();
            if (cache.active == null) {
                CityStructureManager manager = new CityStructureManager(world.getSeed());
                manager.load(world);
                manager.setDistance(ConfigLoader.getCityDistance());
                cache.active = manager;
            }
            cache.idle.clear();
        }
        return cache.active;
    }

    public static synchronized BlockPos nearestCity(World world, int chunkX, int chunkZ) {
        NetherCityCache cache = WORLDS.get(world);
        if (cache != null && cache.active != null) {
            return cache.active.getNearestStructure(world, chunkX, chunkZ);
        }
        int distance = ConfigLoader.getCityDistance();
        int variance = distance / 3;
        int regionX = chunkX / distance;
        int regionZ = chunkZ / distance;
        Random random = new Random(regionX * 341873128712L + regionZ * 132897987541L + 2 * world.getSeed());
        return new BlockPos(regionX * distance + random.nextInt(variance) + variance, 0,
                regionZ * distance + random.nextInt(variance) + variance);
    }

    public static synchronized void save(World world) {
        NetherCityCache cache = WORLDS.get(world);
        if (cache == null || cache.active == null) return;
        cache.active.save(world);
        if (world.playerEntities.isEmpty() && System.nanoTime() - cache.lastUse > TimeUnit.SECONDS.toNanos(15)) {
            cache.release();
        }
    }

    private void release() {
        idle = new SoftReference<>(active);
        active = null;
    }

    @SubscribeEvent
    public static synchronized void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        for (Map.Entry<World, NetherCityCache> entry : WORLDS.entrySet()) {
            World world = entry.getKey();
            NetherCityCache cache = entry.getValue();
            if (world.provider.getDimension() == event.fromDim && world.playerEntities.isEmpty() && cache.active != null) {
                cache.active.save(world);
                cache.release();
            }
        }
    }

    @SubscribeEvent
    public static synchronized void onUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        NetherCityCache cache = WORLDS.remove(world);
        if (cache != null && cache.active != null) cache.active.save(world);
    }
}
