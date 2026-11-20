package dev.mco.mixin.extratrees;

import dev.mco.internal.jei.LumbermillRecipeIndex;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Pseudo
@Mixin(targets = "binnie.extratrees.machines.lumbermill.recipes.LumbermillRecipeManager", remap = false)
public abstract class MixinLumbermillRecipeManager {

    @Redirect(
            method = "getRecipeWithPlanksOutput(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Lnet/minecraft/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/registries/IForgeRegistry;getValuesCollection()Ljava/util/Collection;"
            ),
            remap = false,
            require = 1
    )
    private static Collection<IRecipe> mco$usePlankRecipeCandidates(IForgeRegistry<IRecipe> registry) {
        return LumbermillRecipeIndex.getPlankCandidates(registry);
    }
}
