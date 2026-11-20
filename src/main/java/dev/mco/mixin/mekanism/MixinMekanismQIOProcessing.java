package dev.mco.mixin.mekanism;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mco.internal.cache.QioRecipeScan;
import mekanism.qioprocessing.common.MekanismQIOProcessing;
import mekanism.qioprocessing.common.planning.QIORecipeCatalogService;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MekanismQIOProcessing.class, remap = false)
public abstract class MixinMekanismQIOProcessing {
    @WrapOperation(method = "serverStarted", at = @At(value = "INVOKE",
            target = "Lmekanism/qioprocessing/common/planning/QIORecipeCatalogService;refresh(Lnet/minecraft/world/World;)V"))
    private void mco$deferStartupScan(QIORecipeCatalogService catalog, World world, Operation<Void> refresh) {
        ScopedValue.where(QioRecipeScan.STARTUP, true).run(() -> refresh.call(catalog, world));
    }
}
