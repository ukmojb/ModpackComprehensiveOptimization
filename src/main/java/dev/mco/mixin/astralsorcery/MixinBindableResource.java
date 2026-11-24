package dev.mco.mixin.astralsorcery;

import dev.mco.internal.cache.AstralTextureBatch;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BindableResource.class, remap = false)
public abstract class MixinBindableResource {
    @Inject(method = "allocateGlId", at = @At("HEAD"), cancellable = true)
    private void mco$queueDecode(CallbackInfo ci) {
        if (AstralTextureBatch.enqueue((BindableResource) (Object) this)) ci.cancel();
    }
}
