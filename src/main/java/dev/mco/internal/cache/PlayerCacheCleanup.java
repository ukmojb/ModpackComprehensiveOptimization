package dev.mco.internal.cache;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.function.Predicate;

public final class PlayerCacheCleanup {

    private final Map<EntityPlayer, ?> players;

    private PlayerCacheCleanup(Map<EntityPlayer, ?> players) {
        this.players = players;
    }

    public static void register(Map<EntityPlayer, ?> players) {
        MinecraftForge.EVENT_BUS.register(new PlayerCacheCleanup(players));
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        removePlayers(player -> player.world == event.getWorld());
    }

    @SubscribeEvent
    public void onPlayerLogout(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        removePlayers(player -> player == event.player);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        removePlayers(player -> player == event.getOriginal());
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer joined = (EntityPlayer) event.getEntity();
        removePlayers(player -> player != joined && player.world == event.getWorld()
                && (player.isDead || player.getEntityId() == joined.getEntityId()));
    }

    private void removePlayers(Predicate<EntityPlayer> predicate) {
        synchronized (players) {
            players.keySet().removeIf(predicate);
        }
    }
}
