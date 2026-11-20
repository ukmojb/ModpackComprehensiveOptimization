package dev.mco.mixin.astralsorcery;

import hellfirepvp.astralsorcery.client.ClientProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientProxy.class, remap = false)
public abstract class MixinClientProxy {

    @Redirect(
            method = "init()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lhellfirepvp/astralsorcery/client/gui/GuiJournalPerkTree;initializeDrawBuffer()V",
                    remap = false
            ),
            remap = false
    )
    private void mco$deferPerkTreeBufferInit() {
    }
}
