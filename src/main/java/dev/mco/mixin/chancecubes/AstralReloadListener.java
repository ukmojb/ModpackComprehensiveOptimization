package dev.mco.mixin.chancecubes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mco.internal.cache.AstralSoundReload;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AssetLibrary.class, remap = false)
public abstract class AstralReloadListener {
    @Inject(method = {"onResourceManagerReload", "func_110549_a"}, at = @At("RETURN"))
    private void mco$trackRegisteredListener(CallbackInfo ci) {
        AstralSoundReload.registered((IResourceManagerReloadListener) (Object) this);
    }

    @WrapOperation(method = {"onResourceManagerReload", "func_110549_a"}, at = @At(value = "INVOKE",
            target = "Lhellfirepvp/astralsorcery/client/util/resource/BindableResource;invalidateAndReload()V"))
    private void mco$deferInvalidation(BindableResource texture, Operation<Void> original) {
        if (!AstralSoundReload.defer(texture)) original.call(texture);
    }
}
