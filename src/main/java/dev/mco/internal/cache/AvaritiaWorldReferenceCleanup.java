package dev.mco.internal.cache;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = "mco", value = Side.CLIENT)
public final class AvaritiaWorldReferenceCleanup {

    private static final Set<WorldReference> RENDERERS = Collections.newSetFromMap(new WeakHashMap<>());

    private AvaritiaWorldReferenceCleanup() {
    }

    public static void register(WorldReference renderer) {
        RENDERERS.add(renderer);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            for (WorldReference renderer : RENDERERS) {
                renderer.mco$clearWorldReferences(event.getWorld());
            }
        }
    }

    public interface WorldReference {
        void mco$clearWorldReferences(World world);
    }
}
