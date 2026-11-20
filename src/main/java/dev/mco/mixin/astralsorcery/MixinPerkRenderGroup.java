package dev.mco.mixin.astralsorcery;

import hellfirepvp.astralsorcery.client.gui.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.gui.perk.PerkRenderGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PerkRenderGroup.class, remap = false)
public abstract class MixinPerkRenderGroup {

    @Shadow(remap = false)
    private List<BatchPerkContext.TextureObjectGroup> addedGroups;

    @Inject(
            method = "batchRegister(Lhellfirepvp/astralsorcery/client/gui/perk/BatchPerkContext;)V",
            at = @At("HEAD"),
            remap = false
    )
    private void mco$clearPreviousGroups(BatchPerkContext ctx, CallbackInfo ci) {
        if (this.addedGroups != null) {
            this.addedGroups.clear();
        }
    }
}
