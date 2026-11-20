package dev.mco.mixin.betternether;

import dev.mco.internal.cache.NetherCityCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "paulevs.betternether.world.BNWorldGenerator", remap = false)
public abstract class MixinBNWorldGenerator {

    @Inject(method = "init(Lnet/minecraft/world/World;)V", at = @At("HEAD"), remap = false)
    private static void mco$recordNetherWorld(World world, CallbackInfo ci) {
        NetherCityCache.onNetherWorldLoad(world);
    }

    @Redirect(
            method = "init(Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lpaulevs/betternether/config/ConfigLoader;hasCities()Z",
                    remap = false
            ),
            remap = false
    )
    private static boolean mco$skipEagerCityManagerInit() {
        return false;
    }

    @Inject(method = "smoothChunk(Lnet/minecraft/world/World;II)V", at = @At("HEAD"), remap = false)
    private static void mco$ensureCityManagerOnNetherGen(World world, int cx, int cz, CallbackInfo ci) {
        NetherCityCache.ensureCityManagerForNether(world);
    }

    @Inject(method = "save(Lnet/minecraft/world/World;)V", at = @At("RETURN"), remap = false)
    private static void mco$demoteCityManagerWhenIdle(World world, CallbackInfo ci) {
        NetherCityCache.onAfterWorldSave(world);
    }

    @Inject(
            method = "getNearestCity(Lnet/minecraft/world/World;II)Lnet/minecraft/util/math/BlockPos;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void mco$computeNearestCityIfUnloaded(World world, int cx, int cz, CallbackInfoReturnable<BlockPos> cir) {
        BlockPos fallback = NetherCityCache.computeNearestCityIfUnloaded(world, cx, cz);
        if (fallback != null) {
            cir.setReturnValue(fallback);
        }
    }
}
