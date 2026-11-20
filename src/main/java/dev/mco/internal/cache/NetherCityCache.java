package dev.mco.internal.cache;

import dev.mco.mixin.betternether.CityManagerAccessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.city.CityStructureManager;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class NetherCityCache {
    private static WeakReference<World> owner = new WeakReference<>(null);
    private static SoftReference<CityStructureManager> idle = new SoftReference<>(null);
    private static long lastUse;

    static {
        MinecraftForge.EVENT_BUS.register(NetherCityCache.class);
    }

    private NetherCityCache() {}

    public static synchronized void onNetherWorldLoad(World world) {
        if (world.isRemote || world.provider.getDimension() != -1 || owner.get() == world) return;
        owner = new WeakReference<>(world);
        CityManagerAccessor.setCityManager(null);
        idle.clear();
    }

    public static synchronized void ensureCityManagerForNether(World world) {
        if (world.isRemote || world != owner.get() || !ConfigLoader.hasCities()) return;
        lastUse = System.nanoTime();
        if (CityManagerAccessor.getCityManager() != null) return;
        CityStructureManager manager = idle.get();
        if (manager == null) {
            manager = new CityStructureManager(world.getSeed());
            manager.load(world);
            manager.setDistance(ConfigLoader.getCityDistance());
        }
        CityManagerAccessor.setCityManager(manager);
        idle.clear();
    }

    public static synchronized BlockPos computeNearestCityIfUnloaded(World world, int chunkX, int chunkZ) {
        if (CityManagerAccessor.getCityManager() != null) return null;
        int distance = Math.max(3, ConfigLoader.getCityDistance());
        int variance = distance / 3;
        int regionX = chunkX / distance;
        int regionZ = chunkZ / distance;
        Random random = new Random(regionX * 341873128712L + regionZ * 132897987541L + 2 * world.getSeed());
        return new BlockPos(regionX * distance + random.nextInt(variance) + variance, 0,
                regionZ * distance + random.nextInt(variance) + variance);
    }

    public static synchronized void onAfterWorldSave(World world) {
        if (world == owner.get() && world.playerEntities.isEmpty()
                && System.nanoTime() - lastUse > TimeUnit.SECONDS.toNanos(15)) {
            release();
        }
    }

    private static void release() {
        CityStructureManager manager = CityManagerAccessor.getCityManager();
        if (manager != null) {
            idle = new SoftReference<>(manager);
            CityManagerAccessor.setCityManager(null);
        }
    }

    @SubscribeEvent
    public static synchronized void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        World world = owner.get();
        if (event.fromDim != -1 || world == null || !world.playerEntities.isEmpty()) return;
        CityStructureManager manager = CityManagerAccessor.getCityManager();
        if (manager != null) {
            manager.save(world);
            release();
        }
    }

    @SubscribeEvent
    public static synchronized void onUnload(WorldEvent.Unload event) {
        if (event.getWorld() != owner.get()) return;
        CityStructureManager manager = CityManagerAccessor.getCityManager();
        if (manager != null) manager.save(event.getWorld());
        CityManagerAccessor.setCityManager(null);
        idle.clear();
        owner.clear();
    }
}
