package dev.mco.mixin.betternether;

import com.smd.gctcore.common.integration.betternether.BetterNetherCityCacheHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "paulevs.betternether.structures.city.CityStructureManager", remap = false)
public abstract class MixinCityStructureManager {

    @Inject(
            method = "makeStructure(IILpaulevs/betternether/structures/city/BigStructureCity;)Lpaulevs/betternether/structures/city/BigStructureCity;",
            at = @At("RETURN"),
            remap = false
    )
    private void mco$clearScratchNoiseAfterMake(CallbackInfoReturnable<?> cir) {
        BetterNetherCityCacheHelper.clearNoiseScratch(this);
    }
}
