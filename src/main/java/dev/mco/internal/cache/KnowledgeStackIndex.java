package dev.mco.internal.cache;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class KnowledgeStackIndex {
    private final Map<Item, Set<Integer>> variants = new HashMap<>();
    private final Set<Item> allVariants = new HashSet<>();

    public void add(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getHasSubtypes()) {
            variants.computeIfAbsent(stack.getItem(), item -> new HashSet<>()).add(stack.getItemDamage());
        } else {
            allVariants.add(stack.getItem());
        }
    }

    public boolean contains(ItemStack stack) {
        if (allVariants.contains(stack.getItem())) {
            return true;
        }
        Set<Integer> damages = variants.get(stack.getItem());
        return damages != null && damages.contains(stack.getItemDamage());
    }
}
