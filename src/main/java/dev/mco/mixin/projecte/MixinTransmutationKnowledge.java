package dev.mco.mixin.projecte;

import dev.mco.internal.cache.KnowledgeStackIndex;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "moze_intel.projecte.playerData.Transmutation", remap = false)
public abstract class MixinTransmutationKnowledge {
    @Inject(method = "cacheFullKnowledge", at = @At("HEAD"), require = 1)
    private static void mco$beginKnowledge(CallbackInfo ci, @Share("knowledge") LocalRef<KnowledgeStackIndex> index) {
        index.set(new KnowledgeStackIndex());
    }

    @Redirect(method = "cacheFullKnowledge", at = @At(value = "INVOKE",
            target = "Lmoze_intel/projecte/utils/ItemHelper;containsItemStack(Ljava/util/List;Lnet/minecraft/item/ItemStack;)Z"), require = 1)
    private static boolean mco$containsKnowledge(List<ItemStack> knowledge, ItemStack candidate,
                                                 @Share("knowledge") LocalRef<KnowledgeStackIndex> index) {
        return index.get().contains(candidate);
    }

    @Redirect(method = "cacheFullKnowledge", at = @At(value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), require = 1)
    private static boolean mco$addKnowledge(List<ItemStack> knowledge, Object value,
                                            @Share("knowledge") LocalRef<KnowledgeStackIndex> index) {
        ItemStack stack = (ItemStack) value;
        boolean added = knowledge.add(stack);
        if (added) {
            index.get().add(stack);
        }
        return added;
    }
}
