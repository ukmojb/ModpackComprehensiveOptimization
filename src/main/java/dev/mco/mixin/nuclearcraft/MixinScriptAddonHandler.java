package dev.mco.mixin.nuclearcraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mco.ModpackComprehensiveOptimization;
import dev.mco.internal.cache.ContentTweakerResourceMerge;
import nc.handler.ScriptAddonHandler;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.io.IOException;

@Mixin(value = ScriptAddonHandler.class, remap = false)
public abstract class MixinScriptAddonHandler {
    @Unique
    private static final ScopedValue<ContentTweakerResourceMerge> mco$merge = ScopedValue.newInstance();

    @WrapMethod(method = "construct()V")
    private static void mco$constructAddons(Operation<Void> original) throws IOException {
        ContentTweakerResourceMerge merge = ContentTweakerResourceMerge.create(
                new File("resources/contenttweaker"), new File("resources/.contenttweaker"));
        if (merge == null) {
            original.call();
            return;
        }
        ScopedValue.where(mco$merge, merge).run(() -> original.call());
        ModpackComprehensiveOptimization.LOGGER.debug("NuclearCraft 保留了 {} 个原有资源，复制 {} 个文件，跳过 {} 次覆盖",
                merge.getInitialFileCount(), merge.getCopiedFiles(), merge.getSkippedFiles());
    }

    @Redirect(method = "copyAddons", at = @At(value = "INVOKE",
            target = "Lorg/apache/commons/io/FileUtils;copyDirectory(Ljava/io/File;Ljava/io/File;)V"), require = 1)
    private static void mco$mergeResources(File source, File destination) throws IOException {
        if (mco$merge.isBound()) {
            mco$merge.get().copyDirectory(source, destination);
        } else {
            FileUtils.copyDirectory(source, destination);
        }
    }
}
