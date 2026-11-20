package dev.mco.mixin.compactmachines3;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.compactmachines3.integration.CapabilityNullHandler;
import org.dave.compactmachines3.utility.AnnotatedInstanceUtil;
import org.dave.compactmachines3.world.data.provider.ExtraTileDataProvider;
import org.spongepowered.asm.mixin.Unique;

@Pseudo
@Mixin(targets = "org.dave.compactmachines3.CompactMachines3", remap = false)
public abstract class MixinCompactMachines3 {

    @Inject(
            method = "postInit(Lnet/minecraftforge/fml/common/event/FMLPostInitializationEvent;)V",
            at = @At("RETURN"),
            remap = false
    )
    private void mco$clearAsmDataAfterPostInit(FMLPostInitializationEvent event, CallbackInfo ci) {
        ASMDataTable discovered = AnnotatedInstanceUtil.asmData;
        ASMDataTable handlers = new ASMDataTable();
        mco$copyHandlers(discovered, handlers, CapabilityNullHandler.class.getName());
        mco$copyHandlers(discovered, handlers, ExtraTileDataProvider.class.getName());
        AnnotatedInstanceUtil.setAsmData(handlers);
    }

    @Unique
    private static void mco$copyHandlers(ASMDataTable discovered, ASMDataTable handlers, String annotation) {
        for (ASMDataTable.ASMData entry : discovered.getAll(annotation)) {
            handlers.addASMData(null, annotation, entry.getClassName(), entry.getObjectName(), entry.getAnnotationInfo());
        }
    }
}
