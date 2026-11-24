package dev.mco.bootstrap;

import com.cleanroommc.discovery.CleanroomModDiscoverer;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import zone.rong.mixinbooter.Context;

import java.util.Map;

public final class EarlyMixins implements IMixinConnector {
    private static final Map<String, String> TARGETS = Map.of(
            "mixins.mco.atlas.json", "atlas",
            "mixins.mco.farmersfuturedelight.json", "farmers_future_delight",
            "mixins.mco.early.additions.json", "additions",
            "mixins.mco.early.astralsorcery.json", "astralsorcery",
            "mixins.mco.early.jaopca.json", "jaopca");

    @Override
    public void connect() {
        var mods = CleanroomModDiscoverer.instance().presentMods();
        String baseModels = "mixins.mco.early.base.json";
        if (new Context(baseModels, mods).isModPresent("base") && Boolean.parseBoolean(System.getProperty("mco.base", "true"))) {
            Mixins.addConfiguration(baseModels);
        }
        TARGETS.forEach((config, module) -> {
            if (PatchSelection.enabled(new Context(config, mods), module)) Mixins.addConfiguration(config);
        });
    }
}
