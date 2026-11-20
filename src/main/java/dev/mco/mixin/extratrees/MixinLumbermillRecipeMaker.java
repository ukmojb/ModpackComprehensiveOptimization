package dev.mco.mixin.extratrees;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mco.internal.jei.LumbermillRecipeIndex;
import mezz.jei.api.IJeiHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import java.util.List;

@Pseudo
@Mixin(targets = "binnie.extratrees.integration.jei.lumbermill.LumbermillRecipeMaker", remap = false)
public abstract class MixinLumbermillRecipeMaker {

    @WrapMethod(method = "create(Lmezz/jei/api/IJeiHelpers;)Ljava/util/List;", remap = false)
    private static List<?> mco$withRecipeIndex(IJeiHelpers helpers, Operation<List<?>> original) {
        LumbermillRecipeIndex.beginLoad();
        try {
            return original.call(helpers);
        } finally {
            LumbermillRecipeIndex.endLoad();
        }
    }
}
