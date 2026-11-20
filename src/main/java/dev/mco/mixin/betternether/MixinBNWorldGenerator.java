package dev.mco.mixin.betternether;

import dev.mco.internal.cache.NetherCityCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.betternether.structures.city.CityStructureManager;
import paulevs.betternether.world.BNWorldGenerator;

@Mixin(value = BNWorldGenerator.class, remap = false)
public abstract class MixinBNWorldGenerator {
    @Redirect(method = "init(Lnet/minecraft/world/World;)V", at = @At(value = "INVOKE",
            target = "Lpaulevs/betternether/config/ConfigLoader;hasCities()Z"))
    private static boolean mco$deferCityInitialization() {
        return false;
    }

    @Redirect(method = "smoothChunk", at = @At(value = "FIELD",
            target = "Lpaulevs/betternether/world/BNWorldGenerator;cityManager:Lpaulevs/betternether/structures/city/CityStructureManager;"),
            require = 2)
    private static CityStructureManager mco$cityManager(World world, int chunkX, int chunkZ) {
        return NetherCityCache.forGeneration(world);
    }

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private static void mco$saveCity(World world, CallbackInfo ci) {
        NetherCityCache.save(world);
        ci.cancel();
    }

    @Inject(method = "getNearestCity", at = @At("HEAD"), cancellable = true)
    private static void mco$nearestCity(World world, int chunkX, int chunkZ, CallbackInfoReturnable<BlockPos> cir) {
        cir.setReturnValue(NetherCityCache.nearestCity(world, chunkX, chunkZ));
    }
}
