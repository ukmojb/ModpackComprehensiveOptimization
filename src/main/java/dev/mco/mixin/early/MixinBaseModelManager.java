package dev.mco.mixin.early;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mco.internal.cache.BaseResourceNamespaces;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.resources.IResourceManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelManager.class)
public abstract class MixinBaseModelManager {
    @WrapMethod(method = "onResourceManagerReload")
    private void mco$indexNamespaces(IResourceManager resources, Operation<Void> original) {
        BaseResourceNamespaces.duringModels(() -> original.call(resources));
    }
}
