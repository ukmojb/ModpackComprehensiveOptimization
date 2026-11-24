package dev.mco.mixin.avaritia;

import dev.mco.internal.cache.AvaritiaArchive;
import morph.avaritia.util.TriConsumer;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "morph.avaritia.recipe.AvaritiaRecipeManager", remap = false)
public abstract class MixinRecipeManager {
    @Inject(method = "loadFactories", at = @At("HEAD"), cancellable = true)
    private static void mco$checkFactoryFile(ModContainer mod, CallbackInfo ci) {
        if (AvaritiaArchive.excludes(mod.getSource(), "assets/" + mod.getModId() + "/avaritia_recipes/_factories.json")) ci.cancel();
    }

    @Inject(method = "loadRecipes", at = @At("HEAD"), cancellable = true)
    private static void mco$checkRecipeDirectory(ModContainer mod, String type, TriConsumer<?, ?, ?> consumer, CallbackInfo ci) {
        String id = mod.getModId();
        if (!"minecraft".equals(id) && AvaritiaArchive.excludes(mod.getResource(), "assets/" + id + "/avaritia_recipes/" + type)) {
            ci.cancel();
        }
    }
}
