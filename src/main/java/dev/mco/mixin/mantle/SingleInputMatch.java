package dev.mco.mixin.mantle;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.util.RecipeMatch;
import java.util.LinkedList;
import java.util.Optional;

@Mixin(value = RecipeMatch.ItemCombination.class, remap = false)
public abstract class SingleInputMatch extends RecipeMatch {
    @Shadow @Final protected NonNullList<ItemStack> itemStacks;
    protected SingleInputMatch(int needed, int matched) { super(needed, matched); }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void mco$match(NonNullList<ItemStack> inputs, CallbackInfoReturnable<Optional<Match>> result) {
        if (inputs.size() != 1) return;
        ItemStack template = null;
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack stack = itemStacks.get(i);
            if (stack.isEmpty()) continue;
            if (template != null) {
                result.setReturnValue(Optional.empty());
                return;
            }
            template = stack;
        }
        ItemStack input = inputs.get(0);
        if (template != null && !(ItemStack.areItemsEqual(template, input) && ItemStack.areItemStackTagsEqual(template, input))) {
            result.setReturnValue(Optional.empty());
            return;
        }
        LinkedList<ItemStack> items = new LinkedList<>();
        if (template != null) {
            ItemStack item = input.copy();
            item.setCount(1);
            items.add(item);
        }
        result.setReturnValue(Optional.of(new Match(items, amountMatched)));
    }
}
