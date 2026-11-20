package dev.mco.mixin.ingameinfoxml;

import dev.mco.internal.cache.IGIItemKeySnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Pseudo
@Mixin(targets = "com.github.lunatrius.ingameinfo.client.gui.overlay.InfoItem", remap = false)
public abstract class MixinInfoItem {

    @Redirect(method = {"getCachedItemStack", "getCachedTexture"},
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false, require = 2)
    private static Object mco$storeNbtSnapshot(Map<Object, Object> cache, Object key, Object value) {
        ((IGIItemKeySnapshot) key).mco$freezeNbtSnapshot();
        return cache.put(key, value);
    }
}
