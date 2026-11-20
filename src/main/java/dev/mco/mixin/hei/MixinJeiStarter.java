package dev.mco.mixin.hei;

import dev.mco.internal.jei.HeiHashCacheHelper;
import dev.mco.internal.jei.TransposerContainerCache;
import dev.mco.internal.jei.TConMaterialRecipeIndex;
import mezz.jei.api.IModPlugin;
import mezz.jei.gui.textures.Textures;
import mezz.jei.startup.JeiStarter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = JeiStarter.class, remap = false)
public abstract class MixinJeiStarter {

    @Inject(
            method = "load(Ljava/util/List;Lmezz/jei/gui/textures/Textures;Z)V",
            at = @At("HEAD"),
            remap = false
    )
    private void mco$onJeiLoadStart(List<IModPlugin> plugins, Textures textures, boolean isReload, CallbackInfo ci) {
        HeiHashCacheHelper.beginLoad();
        TransposerContainerCache.beginLoad();
        TConMaterialRecipeIndex.beginLoad();
    }

    @Inject(
            method = "load(Ljava/util/List;Lmezz/jei/gui/textures/Textures;Z)V",
            at = @At("RETURN"),
            remap = false
    )
    private void mco$onJeiLoadEnd(List<IModPlugin> plugins, Textures textures, boolean isReload, CallbackInfo ci) {
        HeiHashCacheHelper.endLoad();
        TransposerContainerCache.endLoad();
        TConMaterialRecipeIndex.endLoad();
    }
}
