package dev.mco.mixin.raids;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Pseudo
@Mixin(targets = "net.smileycorp.raids.common.util.RaidsCriterionTrigger", remap = false)
public abstract class MixinRaidsCriterionTrigger {

    @Shadow @Final private Map<PlayerAdvancements, Set<ICriterionTrigger.Listener>> listeners;

    @Inject(method = {"removeAllListeners", "func_192167_a"}, at = @At("HEAD"), cancellable = true, require = 1)
    private void mco$removePlayerAdvancements(PlayerAdvancements player, CallbackInfo ci) {
        listeners.remove(player);
        ci.cancel();
    }

    @Inject(method = {"removeListener", "func_192164_b"}, at = @At("RETURN"), require = 1)
    private void mco$removeEmptyListenerSet(PlayerAdvancements player, ICriterionTrigger.Listener listener,
                                             CallbackInfo ci) {
        Set<?> remaining = listeners.get(player);
        if (remaining != null && remaining.isEmpty()) {
            listeners.remove(player);
        }
    }
}
