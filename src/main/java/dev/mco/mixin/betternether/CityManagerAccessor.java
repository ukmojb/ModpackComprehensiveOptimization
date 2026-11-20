package dev.mco.mixin.betternether;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulevs.betternether.structures.city.CityStructureManager;
import paulevs.betternether.world.BNWorldGenerator;

@Mixin(value = BNWorldGenerator.class, remap = false)
public interface CityManagerAccessor {
    @Accessor("cityManager")
    static CityStructureManager getCityManager() { throw new AssertionError(); }

    @Accessor("cityManager")
    static void setCityManager(CityStructureManager manager) { throw new AssertionError(); }
}
