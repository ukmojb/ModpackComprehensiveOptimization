package dev.mco.internal.cache;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

public final class AstralTextureBatch {
    private static final ScopedValue<AstralTextureBatch> BATCH = ScopedValue.newInstance();
    private static final ScopedValue<Pending> UPLOAD = ScopedValue.newInstance();
    private final ArrayDeque<Pending> pending = new ArrayDeque<>();

    public static void run(Runnable preload) {
        var batch = new AstralTextureBatch();
        try {
            ScopedValue.where(BATCH, batch).run(preload);
            batch.drain();
        } finally {
            while (!batch.pending.isEmpty()) batch.pending.removeFirst().close();
        }
    }

    public static void beforeSprites() {
        if (BATCH.isBound()) BATCH.get().drain();
    }

    private void drain() {
        while (!pending.isEmpty()) uploadNext();
    }

    private void uploadNext() {
        try (var next = pending.removeFirst()) {
            ScopedValue.where(UPLOAD, next).run(next.texture::allocateGlId);
        }
    }

    public static boolean enqueue(BindableResource texture) {
        if (!BATCH.isBound() || UPLOAD.isBound() || texture.isInitialized() || AssetLibrary.reloading) return false;
        var batch = BATCH.get();
        for (var next : batch.pending) if (next.texture == texture) return true;
        if (batch.pending.size() == 4) batch.uploadNext();
        var manager = Minecraft.getMinecraft().getResourceManager();
        var location = new ResourceLocation(texture.getPath());
        IResource resource;
        try {
            resource = manager.getResource(location);
        } catch (IOException | RuntimeException failure) {
            LogManager.getLogger("mco").debug("预读取 {} 失败，交由星辉加载", location, failure);
            return false;
        }
        try {
            byte[] bytes;
            try (var stream = resource.getInputStream()) {
                bytes = stream.readAllBytes();
            }
            var image = CompletableFuture.supplyAsync(() -> {
                try {
                    return TextureUtil.readBufferedImage(new ByteArrayInputStream(bytes));
                } catch (IOException failure) {
                    throw new CompletionException(failure);
                }
            });
            batch.pending.addLast(new Pending(texture, manager, location, resource, bytes, image));
            return true;
        } catch (IOException | RuntimeException failure) {
            closeResource(resource);
            LogManager.getLogger("mco").debug("读取 {} 的 PNG 失败，交由星辉加载", location, failure);
            return false;
        } catch (Error failure) {
            closeResource(resource);
            throw failure;
        }
    }

    public static Pending prepared(IResourceManager manager, ResourceLocation location) {
        if (!UPLOAD.isBound()) return null;
        var pending = UPLOAD.get();
        return pending.manager == manager && pending.location.equals(location) ? pending : null;
    }

    private static void closeResource(IResource resource) {
        try {
            resource.close();
        } catch (IOException failure) {
            LogManager.getLogger("mco").warn("关闭星辉贴图资源失败", failure);
        }
    }

    public static final class Pending implements AutoCloseable {
        private final BindableResource texture;
        private final IResourceManager manager;
        private final ResourceLocation location;
        private final IResource resource;
        private final byte[] bytes;
        private final CompletableFuture<BufferedImage> image;
        private boolean handedOff;

        private Pending(BindableResource texture, IResourceManager manager, ResourceLocation location,
                        IResource resource, byte[] bytes, CompletableFuture<BufferedImage> image) {
            this.texture = texture;
            this.manager = manager;
            this.location = location;
            this.resource = resource;
            this.bytes = bytes;
            this.image = image;
        }

        public IResource takeResource() {
            handedOff = true;
            return resource;
        }

        public BufferedImage decoded() throws IOException {
            try {
                return image.join();
            } catch (CompletionException failure) {
                if (failure.getCause() instanceof IOException io) throw io;
                if (failure.getCause() instanceof Error error) throw error;
                throw failure;
            }
        }

        public ByteArrayInputStream openStream() {
            return new ByteArrayInputStream(bytes);
        }

        @Override
        public void close() {
            if (!handedOff) closeResource(resource);
        }
    }
}
