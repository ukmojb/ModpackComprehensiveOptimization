package dev.mco.mixin.projecte;

import dev.mco.internal.cache.KnowledgeStackIndex;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "moze_intel.projecte.playerData.Transmutation", remap = false)
public abstract class MixinTransmutationKnowledge {
    @Unique
    private static final ThreadLocal<KnowledgeStackIndex> mco$knowledgeIndex = new ThreadLocal<>();

    @Inject(method = "cacheFullKnowledge", at = @At("HEAD"), require = 1)
    private static void mco$beginKnowledge(CallbackInfo ci) {
        mco$knowledgeIndex.set(new KnowledgeStackIndex());
    }

    @Inject(method = "cacheFullKnowledge", at = @At("RETURN"), require = 1)
    private static void mco$endKnowledge(CallbackInfo ci) {
        mco$knowledgeIndex.remove();
    }

    @Redirect(method = "cacheFullKnowledge", at = @At(value = "INVOKE",
            target = "Lmoze_intel/projecte/utils/ItemHelper;containsItemStack(Ljava/util/List;Lnet/minecraft/item/ItemStack;)Z"), require = 1)
    private static boolean mco$containsKnowledge(List<ItemStack> knowledge, ItemStack candidate) {
        return mco$knowledgeIndex.get().contains(candidate);
    }

    @Redirect(method = "cacheFullKnowledge", at = @At(value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), require = 1)
    private static boolean mco$addKnowledge(List<ItemStack> knowledge, Object value) {
        ItemStack stack = (ItemStack) value;
        boolean added = knowledge.add(stack);
        if (added) {
            mco$knowledgeIndex.get().add(stack);
        }
        return added;
    }
}
