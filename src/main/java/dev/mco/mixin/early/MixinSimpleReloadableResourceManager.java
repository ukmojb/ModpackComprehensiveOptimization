package dev.mco.mixin.early;

import dev.mco.internal.cache.JAOPCAResourceCache;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleReloadableResourceManager.class)
public abstract class MixinSimpleReloadableResourceManager {

    @Inject(method = "reloadResources", at = @At("HEAD"), require = 1)
    private void mco$clearJAOPCAResourceCache(CallbackInfo ci) {
        JAOPCAResourceCache.clear();
    }
}
