package dev.mco.mixin.chancecubes;

import chanceCubes.sounds.CustomSoundsLoader;
import dev.mco.internal.cache.AstralSoundReload;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import dev.mco.internal.cache.EmptySoundPack;

import java.io.File;

@Mixin(value = CustomSoundsLoader.class, remap = false)
public abstract class SoundPackRefresh {
    @Unique private static boolean mco$assembled;

    @Redirect(method = "assemble", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/fml/client/FMLClientHandler;refreshResources([Lnet/minecraftforge/client/resource/IResourceType;)V"))
    private void mco$refresh(FMLClientHandler client, IResourceType[] types, @Local(index = 3) File installedPack) {
        boolean firstAssembly = !mco$assembled;
        mco$assembled = true;
        if (firstAssembly && Loader.instance().getLoaderState() == LoaderState.PREINITIALIZATION
                && types.length == 1 && types[0] == VanillaResourceType.SOUNDS
                && EmptySoundPack.isEmpty(installedPack)) {
            // GeckoLib 首次登记监听器时需要 Forge 已挂载当前完整资源包列表。
            if (Loader.isModLoaded("astralsorcery")) {
                if (AstralSoundReload.refresh(() -> client.refreshResources(types))) return;
            } else if (!Loader.isModLoaded("geckolib3")) {
                return;
            }
        }
        client.refreshResources(types);
    }
}
