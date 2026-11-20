package dev.mco.mixin.compactmachines3;

import dev.mco.ModpackComprehensiveOptimization;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Pseudo
@Mixin(targets = "org.dave.compactmachines3.CompactMachines3", remap = false)
public abstract class MixinCompactMachines3 {

    @Inject(
            method = "postInit(Lnet/minecraftforge/fml/common/event/FMLPostInitializationEvent;)V",
            at = @At("RETURN"),
            remap = false
    )
    private void mco$clearAsmDataAfterPostInit(FMLPostInitializationEvent event, CallbackInfo ci) {
        try {
            Class<?> utilClass = Class.forName("org.dave.compactmachines3.utility.AnnotatedInstanceUtil");
            Field asmDataField = utilClass.getField("asmData");
            if (asmDataField.get(null) != null) {
                asmDataField.set(null, null);
                ModpackComprehensiveOptimization.LOGGER.info("GCT-Core cleared CompactMachines3 AnnotatedInstanceUtil.asmData after postInit");
            }
        } catch (Throwable t) {
            ModpackComprehensiveOptimization.LOGGER.warn("Failed to clear CompactMachines3 AnnotatedInstanceUtil.asmData", t);
        }
    }
}
