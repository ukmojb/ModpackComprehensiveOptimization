package dev.mco.mixin.hei;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mco.internal.jei.HeiHashCacheHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.gui.textures.Textures;
import mezz.jei.startup.JeiStarter;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = JeiStarter.class, remap = false)
public abstract class MixinJeiStarter {

    @WrapMethod(method = "load(Ljava/util/List;Lmezz/jei/gui/textures/Textures;Z)V", remap = false)
    private void mco$withHashCache(List<IModPlugin> plugins, Textures textures, boolean isReload, Operation<Void> original) {
        HeiHashCacheHelper.beginLoad();
        try {
            original.call(plugins, textures, isReload);
        } finally {
            HeiHashCacheHelper.endLoad();
        }
    }
}
