package dev.mco.mixin.astralsorcery;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mco.internal.cache.AstralTextureBatch;
import hellfirepvp.astralsorcery.client.util.TexturePreloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TexturePreloader.class, remap = false)
public abstract class MixinTexturePreloader {
    @WrapMethod(method = {"preloadMandatoryTextures", "preloadTextures"})
    private static void mco$decodeBatch(Operation<Void> original) {
        AstralTextureBatch.run(original::call);
    }

    @Inject(method = "preloadTextures", at = @At(value = "INVOKE",
            target = "Lhellfirepvp/astralsorcery/client/util/SpriteLibrary;init()V"))
    private static void mco$finishTextures(CallbackInfo ci) {
        AstralTextureBatch.beforeSprites();
    }
}
