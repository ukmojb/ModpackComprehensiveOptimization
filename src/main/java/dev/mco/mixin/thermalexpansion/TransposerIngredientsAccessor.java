package dev.mco.mixin.thermalexpansion;

import cofh.thermalexpansion.plugins.jei.machine.transposer.TransposerRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = TransposerRecipeWrapper.class, remap = false)
public interface TransposerIngredientsAccessor {
    @Accessor("inputs") List<List<ItemStack>> mco$getInputs();
    @Accessor("outputs") List<List<ItemStack>> mco$getOutputs();
    @Accessor("inputFluids") List<List<FluidStack>> mco$getInputFluids();
}
