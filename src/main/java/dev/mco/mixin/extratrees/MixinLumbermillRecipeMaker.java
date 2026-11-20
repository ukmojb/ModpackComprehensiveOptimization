package dev.mco.mixin.extratrees;

import dev.mco.internal.jei.LumbermillRecipeIndex;
import mezz.jei.api.IJeiHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "binnie.extratrees.integration.jei.lumbermill.LumbermillRecipeMaker", remap = false)
public abstract class MixinLumbermillRecipeMaker {

    @Inject(
            method = "create(Lmezz/jei/api/IJeiHelpers;)Ljava/util/List;",
            at = @At("HEAD"),
            remap = false,
            require = 1
    )
    private static void mco$beginLumbermillJei(IJeiHelpers jeiHelpers, CallbackInfoReturnable<List<?>> cir) {
        LumbermillRecipeIndex.beginLoad();
    }

    @Inject(
            method = "create(Lmezz/jei/api/IJeiHelpers;)Ljava/util/List;",
            at = @At("RETURN"),
            remap = false,
            require = 1
    )
    private static void mco$endLumbermillJei(IJeiHelpers jeiHelpers, CallbackInfoReturnable<List<?>> cir) {
        LumbermillRecipeIndex.endLoad();
    }
}
