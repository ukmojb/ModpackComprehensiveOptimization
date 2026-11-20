package dev.mco.mixin.ingameinfoxml;

import dev.mco.internal.cache.IGIItemKeySnapshot;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.github.lunatrius.ingameinfo.client.gui.overlay.InfoItem$ItemKey", remap = false)
public abstract class MixinItemKey implements IGIItemKeySnapshot {

    @Shadow(remap = false)
    @Final
    @Mutable
    private NBTTagCompound tagCompound;

    @Redirect(method = "<init>(Lnet/minecraft/item/ItemStack;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NBTTagCompound;copy()Lnet/minecraft/nbt/NBTTagCompound;"),
            remap = true, require = 1)
    private NBTTagCompound mco$borrowLookupNbt(NBTTagCompound tag) {
        return tag;
    }

    @Override
    @Unique
    public void mco$freezeNbtSnapshot() {
        if (tagCompound != null) {
            tagCompound = tagCompound.copy();
        }
    }
}
