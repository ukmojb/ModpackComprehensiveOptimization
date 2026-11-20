package dev.mco.internal.jei;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class LumbermillRecipeIndex {

    private static final String PLANK_WOOD_ORE = "plankWood";

    private static volatile boolean active;
    private static Thread ownerThread;
    private static List<IRecipe> cachedCandidates;

    private LumbermillRecipeIndex() {
    }

    public static void beginLoad() {
        active = true;
        ownerThread = Thread.currentThread();
        cachedCandidates = null;
    }

    public static void endLoad() {
        active = false;
        ownerThread = null;
        cachedCandidates = null;
    }

    public static Collection<IRecipe> getPlankCandidates(IForgeRegistry<IRecipe> registry) {
        if (!active || Thread.currentThread() != ownerThread) {
            return registry.getValuesCollection();
        }
        if (cachedCandidates == null) {
            cachedCandidates = buildCandidates(registry.getValuesCollection());
        }
        return cachedCandidates;
    }

    private static List<IRecipe> buildCandidates(Collection<IRecipe> allRecipes) {
        int plankOreId = OreDictionary.getOreID(PLANK_WOOD_ORE);
        List<IRecipe> candidates = new ArrayList<>(256);
        for (IRecipe recipe : allRecipes) {
            if (isCandidate(recipe, plankOreId)) {
                candidates.add(recipe);
            }
        }
        return candidates;
    }

    private static boolean isCandidate(IRecipe recipe, int plankOreId) {
        if (recipe == null) {
            return false;
        }
        ItemStack output = recipe.getRecipeOutput();
        if (!recipe.isDynamic() && output != null && !output.isEmpty() && !hasOreIdSafe(output, plankOreId)) {
            return false;
        }
        return !isMultiIngredientStandardRecipe(recipe);
    }

    private static boolean hasOreIdSafe(ItemStack stack, int oreId) {
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids) {
            if (id == oreId) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMultiIngredientStandardRecipe(IRecipe recipe) {
        Class<?> cls = recipe.getClass();
        if (cls != ShapedRecipes.class
                && cls != ShapelessRecipes.class
                && cls != ShapedOreRecipe.class
                && cls != ShapelessOreRecipe.class) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int nonEmptyCount = 0;
        for (int i = 0, len = ingredients.size(); i < len; i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient != null && ingredient != Ingredient.EMPTY) {
                nonEmptyCount++;
                if (nonEmptyCount > 1) {
                    return true;
                }
            }
        }
        return false;
    }
}
