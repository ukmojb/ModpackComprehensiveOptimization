package dev.mco.mixin.base;

import com.teamacronymcoders.base.util.files.DirectoryResourcePack;
import dev.mco.internal.cache.BaseResourceNamespaces;
import java.io.File;
import net.minecraft.client.resources.AbstractResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DirectoryResourcePack.class)
public abstract class MixinDirectoryResourcePack extends AbstractResourcePack {
    private MixinDirectoryResourcePack(File root) { super(root); }

    @Inject(method = "hasResourceName", at = @At("HEAD"), cancellable = true)
    private void mco$checkNamespace(String name, CallbackInfoReturnable<Boolean> result) {
        if (BaseResourceNamespaces.excludes(resourcePackFile, name)) result.setReturnValue(false);
    }
}
