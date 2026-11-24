package dev.mco.mixin.libnine;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mco.internal.cache.LibNineModelPaths;
import io.github.phantamanta44.libnine.client.model.L9CachingModelLoader;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = L9CachingModelLoader.class, remap = false)
public abstract class MixinCachingModelLoader {
    @WrapOperation(method = {"accepts", "loadModel"}, at = @At(value = "INVOKE",
            target = "Lio/github/phantamanta44/libnine/client/model/L9Models;getRealModelLocation(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/ResourceLocation;"))
    private ResourceLocation mco$modelPath(ResourceLocation location, Operation<ResourceLocation> original) {
        return LibNineModelPaths.resolve(location, () -> original.call(location));
    }
}
