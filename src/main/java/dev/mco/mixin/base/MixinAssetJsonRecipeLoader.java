package dev.mco.mixin.base;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.teamacronymcoders.base.recipesystem.loader.AssetJsonRecipeLoader;
import dev.mco.internal.cache.BaseRecipeArchives;
import java.util.stream.Stream;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AssetJsonRecipeLoader.class, remap = false)
public abstract class MixinAssetJsonRecipeLoader {
    @ModifyExpressionValue(method = "loadRecipes", at = @At(value = "INVOKE", target = "Ljava/util/List;parallelStream()Ljava/util/stream/Stream;"))
    private Stream<ModContainer> mco$filterArchives(Stream<ModContainer> mods) {
        return mods.filter(new BaseRecipeArchives()::needsScan);
    }
}
