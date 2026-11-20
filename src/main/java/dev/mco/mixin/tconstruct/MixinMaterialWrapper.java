package dev.mco.mixin.tconstruct;

import dev.mco.internal.jei.TConMaterialRecipeIndex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.List;

@Pseudo
@Mixin(targets = "slimeknights.tconstruct.plugin.jei.material.MaterialWrapper", remap = false)
public abstract class MixinMaterialWrapper {
    @Shadow @Final
    private Material material;

    @Redirect(method = "getInputs", at = @At(value = "INVOKE",
            target = "Lslimeknights/tconstruct/library/TinkerRegistry;getAllMeltingRecipies()Ljava/util/List;"), require = 1)
    private List<MeltingRecipe> mco$findMaterialRecipes() {
        return TConMaterialRecipeIndex.getRecipes(material.getFluid());
    }
}
