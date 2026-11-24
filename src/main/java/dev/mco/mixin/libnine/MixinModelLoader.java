package dev.mco.mixin.libnine;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mco.internal.cache.LibNineModelPaths;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ModelLoader;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelLoader.class)
public abstract class MixinModelLoader {
    @WrapMethod(method = "setupModelRegistry")
    private IRegistry<ModelResourceLocation, IBakedModel> mco$cachePaths(Operation<IRegistry<ModelResourceLocation, IBakedModel>> original) {
        return LibNineModelPaths.duringBake(original::call);
    }
}
