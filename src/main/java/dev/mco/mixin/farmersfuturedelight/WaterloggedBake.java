package dev.mco.mixin.farmersfuturedelight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mco.internal.cache.WaterloggedModelIndex;
import net.minecraftforge.client.event.ModelBakeEvent;
import org.spongepowered.asm.mixin.Mixin;
import xy177.farmersfuturedelight.client.ClientEventHandler;

@Mixin(value = ClientEventHandler.class, remap = false)
public abstract class WaterloggedBake {
    @WrapMethod(method = "bakeWaterloggedPlantModels(Lnet/minecraftforge/client/event/ModelBakeEvent;)V", remap = false)
    private static void mco$indexBake(ModelBakeEvent event, Operation<Void> original) {
        WaterloggedModelIndex.bake(() -> original.call(event));
    }
}
