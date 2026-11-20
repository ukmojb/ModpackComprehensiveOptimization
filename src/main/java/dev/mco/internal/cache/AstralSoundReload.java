package dev.mco.internal.cache;

import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class AstralSoundReload {
    private static IResourceManagerReloadListener registered;
    private static final ScopedValue<Set<BindableResource>> PENDING = ScopedValue.newInstance();

    public static void registered(IResourceManagerReloadListener listener) {
        registered = listener;
    }

    public static boolean refresh(Runnable reload) {
        if (registered == null) return false;
        Set<BindableResource> pending = Collections.newSetFromMap(new IdentityHashMap<>());
        try {
            ScopedValue.where(PENDING, pending).run(reload);
        } finally {
            for (BindableResource texture : pending) texture.invalidateAndReload();
        }
        return true;
    }

    public static boolean defer(BindableResource texture) {
        if (!PENDING.isBound() || texture.getResource() == TextureUtil.MISSING_TEXTURE) return false;
        PENDING.get().add(texture);
        return true;
    }

    public static void requested(BindableResource texture) {
        if (PENDING.isBound()) PENDING.get().remove(texture);
    }

    private AstralSoundReload() {}
}
