package dev.mco.internal.jei;

import net.minecraftforge.fluids.Fluid;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class TConMaterialRecipeIndex {
    private static volatile Thread loadThread;
    private static Map<Fluid, List<MeltingRecipe>> recipesByFluid;

    private TConMaterialRecipeIndex() {
    }

    public static void beginLoad() {
        recipesByFluid = null;
        loadThread = Thread.currentThread();
    }

    public static void endLoad() {
        loadThread = null;
        recipesByFluid = null;
    }

    public static List<MeltingRecipe> getRecipes(Fluid fluid) {
        return getRecipes(fluid, TinkerRegistry::getAllMeltingRecipies);
    }

    static List<MeltingRecipe> getRecipes(Fluid fluid, Supplier<List<MeltingRecipe>> source) {
        if (Thread.currentThread() != loadThread) {
            return source.get();
        }
        if (recipesByFluid == null) {
            recipesByFluid = indexRecipes(source.get());
        }
        return recipesByFluid.getOrDefault(fluid, Collections.emptyList());
    }

    static Map<Fluid, List<MeltingRecipe>> indexRecipes(List<MeltingRecipe> recipes) {
        Map<Fluid, List<MeltingRecipe>> index = new HashMap<>();
        for (MeltingRecipe recipe : recipes) {
            index.computeIfAbsent(recipe.output.getFluid(), ignored -> new ArrayList<>()).add(recipe);
        }
        return index;
    }
}
