package dev.mco.mixin.hei;

import dev.mco.internal.jei.HeiHashCacheHelper;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackHelper;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = FluidStackHelper.class, remap = false)
public abstract class MixinFluidStackHelper {

    @Shadow
    @Final
    private Map<FluidStack, Integer> hashCache;

    @Inject(method = "<init>()V", at = @At("RETURN"), remap = false)
    private void mco$registerHashCache(CallbackInfo ci) {
        HeiHashCacheHelper.registerFluidHashCache(this.hashCache);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Redirect(
            method = "getHash(Lnet/minecraftforge/fluids/FluidStack;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            remap = false
    )
    private Object mco$skipPostLoadCachePut(Map map, Object key, Object value) {
        if (HeiHashCacheHelper.isCachingActive()) {
            return map.put(key, value);
        }
        return null;
    }
}
