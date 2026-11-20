package dev.mco.mixin.nuclearcraft;

import dev.mco.ModpackComprehensiveOptimization;
import dev.mco.internal.cache.ContentTweakerResourceMerge;
import nc.handler.ScriptAddonHandler;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(value = ScriptAddonHandler.class, remap = false)
public abstract class MixinScriptAddonHandler {
    @Unique private static ContentTweakerResourceMerge mco$resourceMerge;
    @Unique private static long mco$constructStart;

    @Inject(method = "construct", at = @At("HEAD"), require = 1)
    private static void mco$beginMerge(CallbackInfo ci) throws IOException {
        mco$constructStart = System.nanoTime();
        mco$resourceMerge = null;
        mco$resourceMerge = ContentTweakerResourceMerge.create(
                    new File("resources/contenttweaker"), new File("resources/.contenttweaker"));
    }

    @Redirect(method = "copyAddons", at = @At(value = "INVOKE",
            target = "Lorg/apache/commons/io/FileUtils;copyDirectory(Ljava/io/File;Ljava/io/File;)V"), require = 1)
    private static void mco$mergeResources(File source, File destination) throws IOException {
        if (mco$resourceMerge == null) FileUtils.copyDirectory(source, destination);
        else mco$resourceMerge.copyDirectory(source, destination);
    }

    @Inject(method = "construct", at = @At("RETURN"), require = 1)
    private static void mco$finishMerge(CallbackInfo ci) {
        ContentTweakerResourceMerge merge = mco$resourceMerge;
        mco$resourceMerge = null;
        if (merge != null) {
            ModpackComprehensiveOptimization.LOGGER.info("[mco/NuclearCraft] Constructed addons in {}ms; preserved={}, copied={}, skipped={}.",
                    (System.nanoTime() - mco$constructStart) / 1_000_000,
                    merge.getInitialFileCount(), merge.getCopiedFiles(), merge.getSkippedFiles());
        }
    }
}
