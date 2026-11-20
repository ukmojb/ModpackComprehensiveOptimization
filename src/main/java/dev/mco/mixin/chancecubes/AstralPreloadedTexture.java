package dev.mco.mixin.chancecubes;

import dev.mco.internal.cache.AstralSoundReload;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BindableResource.class, remap = false)
public abstract class AstralPreloadedTexture {
    @Inject(method = "allocateGlId", at = @At("HEAD"))
    private void mco$keepPreloadedTexture(CallbackInfo ci) {
        AstralSoundReload.requested((BindableResource) (Object) this);
    }
}
