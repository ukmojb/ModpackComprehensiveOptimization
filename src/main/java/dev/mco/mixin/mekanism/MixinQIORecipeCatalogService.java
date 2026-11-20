package dev.mco.mixin.mekanism;

import dev.mco.internal.cache.QioRecipeScan;
import mekanism.qioprocessing.common.planning.QIORecipeCatalogService;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = QIORecipeCatalogService.class, remap = false)
public abstract class MixinQIORecipeCatalogService {
    // 此时目录已绑定世界、清除旧缓存并递增版本，首次按配置查询会自行构建索引。
    @Inject(method = "refresh(Lnet/minecraft/world/World;)V", at = @At(value = "FIELD",
            target = "Lmekanism/qioprocessing/common/planning/QIORecipeCatalogService;revision:J",
            opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER), cancellable = true)
    private void mco$deferIndexBuild(CallbackInfo ci) {
        if (QioRecipeScan.STARTUP.isBound()) ci.cancel();
    }
}
