package dev.mco.mixin.compactmachines3;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.dave.compactmachines3.utility.AnnotatedInstanceUtil;

@Pseudo
@Mixin(targets = "org.dave.compactmachines3.CompactMachines3", remap = false)
public abstract class MixinCompactMachines3 {

    @Inject(
            method = "postInit(Lnet/minecraftforge/fml/common/event/FMLPostInitializationEvent;)V",
            at = @At("RETURN"),
            remap = false
    )
    private void mco$clearAsmDataAfterPostInit(FMLPostInitializationEvent event, CallbackInfo ci) {
        AnnotatedInstanceUtil.asmData = null;
    }
}
