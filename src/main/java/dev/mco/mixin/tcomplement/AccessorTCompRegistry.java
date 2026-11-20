package dev.mco.mixin.tcomplement;

import knightminer.tcomplement.library.IBlacklist;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.List;

@Pseudo
@Mixin(targets = "knightminer.tcomplement.library.TCompRegistry", remap = false)
public interface AccessorTCompRegistry {
    @Accessor("meltingOverrides")
    static List<MeltingRecipe> mco$meltingOverrides() {
        throw new AssertionError("Mixin accessor");
    }

    @Accessor("highOvenOverrides")
    static List<MeltingRecipe> mco$highOvenOverrides() {
        throw new AssertionError("Mixin accessor");
    }

    @Accessor("meltingBlacklist")
    static List<IBlacklist> mco$meltingBlacklist() {
        throw new AssertionError("Mixin accessor");
    }
}
