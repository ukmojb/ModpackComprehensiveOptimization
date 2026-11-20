package dev.mco.mixin.mekanism;

import mekanism.qioprocessing.common.content.workbench.QIOWorkbenchConfiguration;
import mekanism.qioprocessing.common.planning.QIORecipeCatalogService;
import mekanism.qioprocessing.common.planning.QIOWorkbenchRecipeCatalog;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = QIORecipeCatalogService.class, remap = false)
public abstract class MixinQIORecipeCatalogService {

    @Shadow
    private QIOWorkbenchRecipeCatalog.Snapshot snapshot;

    @Shadow
    private World world;

    @Shadow
    public abstract void refresh(World world);

    @Inject(
            method = "getSnapshot()Lmekanism/qioprocessing/common/planning/QIOWorkbenchRecipeCatalog$Snapshot;",
            at = @At("HEAD"),
            remap = false
    )
    private void mco$lazyInitSnapshot(CallbackInfoReturnable<QIOWorkbenchRecipeCatalog.Snapshot> cir) {
        this.mco$ensureInitializedForSnapshot();
    }

    @Inject(
            method = "getState()Lmekanism/qioprocessing/common/planning/QIORecipeCatalogService$State;",
            at = @At("HEAD"),
            remap = false
    )
    private void mco$lazyInitState(CallbackInfoReturnable<QIORecipeCatalogService.State> cir) {
        this.mco$ensureInitializedForSnapshot();
    }

    @Inject(
            method = "getSnapshot(Lmekanism/qioprocessing/common/content/workbench/QIOWorkbenchConfiguration;)Lmekanism/qioprocessing/common/planning/QIOWorkbenchRecipeCatalog$Snapshot;",
            at = @At("HEAD"),
            remap = false
    )
    private void mco$lazyInitConfiguredSnapshot(
            QIOWorkbenchConfiguration configuration,
            CallbackInfoReturnable<QIOWorkbenchRecipeCatalog.Snapshot> cir
    ) {
        this.mco$ensureInitializedForConfig();
    }

    @Unique
    private void mco$ensureInitializedForSnapshot() {
        if (this.snapshot == null) {
            World targetWorld = this.world != null ? this.world : DimensionManager.getWorld(0);
            if (targetWorld != null) {
                this.refresh(targetWorld);
            }
        }
    }

    @Unique
    private void mco$ensureInitializedForConfig() {
        if (this.snapshot == null && this.world == null) {
            World overworld = DimensionManager.getWorld(0);
            if (overworld != null) {
                this.refresh(overworld);
            }
        }
    }
}
