package dev.mco.mixin.ingameinfoxml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.List;

@Pseudo
@Mixin(targets = "com.github.lunatrius.ingameinfo.InGameInfoCore", remap = false)
public abstract class MixinInGameInfoCore {

    @Redirect(method = "onTickClient",
            at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"),
            remap = false, require = 1)
    private boolean mco$skipDuplicateRows(List<?> info, Collection<?> rows) {
        return false;
    }
}
