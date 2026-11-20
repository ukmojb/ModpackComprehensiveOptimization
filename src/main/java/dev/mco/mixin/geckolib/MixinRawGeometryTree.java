package dev.mco.mixin.geckolib;

import dev.mco.ModpackComprehensiveOptimization;
import dev.mco.internal.cache.GeckoHierarchyIndex;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.geo.raw.pojo.RawGeoModel;
import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;

@Mixin(value = RawGeometryTree.class, remap = false)
public abstract class MixinRawGeometryTree {
    @Inject(method = "parseHierarchy", at = @At("HEAD"), cancellable = true, require = 1)
    private static void mco$indexHierarchy(RawGeoModel model, ResourceLocation location,
                                              CallbackInfoReturnable<RawGeometryTree> cir) {
        long start = System.nanoTime();
        RawGeometryTree result = GeckoHierarchyIndex.tryBuild(model);
        if (result != null) {
            int count = model.getMinecraftGeometry()[0].getBones().length;
            if (count >= 1000) {
                ModpackComprehensiveOptimization.LOGGER.info("[mco/GeckoLib] Indexed {} bones for {} in {}ms.",
                        count, location, (System.nanoTime() - start) / 1_000_000);
            }
            cir.setReturnValue(result);
        }
    }
}
