package dev.mco.mixin.farmersfuturedelight;

import dev.mco.internal.cache.WaterloggedModelIndex;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xy177.farmersfuturedelight.api.WaterloggedBlockApi;

import java.util.Set;

@Mixin(value = WaterloggedBlockApi.class, remap = false)
public abstract class WaterloggedModels {
    @Shadow @Final private static Set<ResourceLocation> RENDERED_MODELS;
    @Shadow @Final private static Set<Block> RENDERED_BLOCKS;

    @Inject(method = {"registerRenderedBlock", "unregisterRenderedBlock", "registerRenderedModel", "unregisterRenderedModel"}, at = @At("RETURN"))
    private static void mco$invalidate(CallbackInfo ci) {
        WaterloggedModelIndex.registrationsChanged();
    }

    @Inject(method = "isRenderedModel(Lnet/minecraft/util/ResourceLocation;)Z", at = @At("HEAD"), cancellable = true)
    private static void mco$lookup(ResourceLocation model, CallbackInfoReturnable<Boolean> result) {
        WaterloggedModelIndex index = WaterloggedModelIndex.current();
        if (index != null && model != null && (model.getClass() == ModelResourceLocation.class || model.getClass() == ResourceLocation.class)) {
            result.setReturnValue(index.contains(model, RENDERED_MODELS, RENDERED_BLOCKS));
        }
    }
}
