package dev.mco.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.mco.internal.cache.AstralTextureBatch;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SimpleTexture.class)
public abstract class MixinSimpleTexture {
    @WrapOperation(method = "loadTexture", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/IResourceManager;getResource(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/resources/IResource;"))
    private IResource mco$preparedResource(IResourceManager manager, ResourceLocation location, Operation<IResource> original,
                                           @Share("prepared") LocalRef<AstralTextureBatch.Pending> ref) {
        var pending = AstralTextureBatch.prepared(manager, location);
        ref.set(pending);
        return pending == null ? original.call(manager, location) : pending.takeResource();
    }

    @WrapOperation(method = "loadTexture", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/IResource;getInputStream()Ljava/io/InputStream;"))
    private InputStream mco$preparedStream(IResource resource, Operation<InputStream> original,
                                            @Share("prepared") LocalRef<AstralTextureBatch.Pending> ref) {
        return ref.get() == null ? original.call(resource) : ref.get().openStream();
    }

    @WrapOperation(method = "loadTexture", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureUtil;readBufferedImage(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;"))
    private BufferedImage mco$decodedImage(InputStream stream, Operation<BufferedImage> original,
                                           @Share("prepared") LocalRef<AstralTextureBatch.Pending> ref) throws IOException {
        return ref.get() == null ? original.call(stream) : ref.get().decoded();
    }
}
