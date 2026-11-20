package dev.mco;

import dev.mco.bootstrap.PatchSelection;
import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public final class OptionalMixins implements ILateMixinLoader {
    private record Patch(String name, String marker, String... mods) {
        String config() { return "mixins.mco." + name + ".json"; }

        boolean enabled(Context context) {
            for (String mod : mods) {
                if (!context.isModPresent(mod)) return false;
            }
            return (marker == null || OptionalMixins.class.getClassLoader().getResource(marker) != null)
                    && PatchSelection.enabled(context, mods[0]);
        }
    }

    private static final List<Patch> PATCHES = List.of(
            new Patch("additions", null, "additions"),
            new Patch("astralsorcery", null, "astralsorcery"),
            new Patch("avaritia", null, "avaritia"),
            new Patch("betternether", "paulevs/betternether/noise/PregennedOpenSimplexNoise.class", "betternether"),
            new Patch("chancecubes", null, "chancecubes"),
            new Patch("chancecubes.astral", null, "chancecubes", "astralsorcery"),
            new Patch("mantle", null, "mantle"),
            new Patch("compactmachines3", null, "compactmachines3"),
            new Patch("enderstorage", null, "enderstorage"),
            new Patch("extratrees", null, "extratrees", "jei"),
            new Patch("forestry", null, "forestry", "jei"),
            new Patch("geckolib", null, "geckolib3"),
            new Patch("hei", "mezz/jei/command/CommandTreeHEI.class", "jei"),
            new Patch("ingameinfoxml", "com/github/lunatrius/ingameinfo/client/gui/overlay/InfoItem$ItemKey.class", "ingameinfoxml"),
            new Patch("jaopca", null, "jaopca"),
            new Patch("mekanism", null, "mekanismqioprocessing"),
            new Patch("nuclearcraft", "nc/handler/ScriptAddonHandler.class", "nuclearcraft"),
            new Patch("projecte", null, "projecte"),
            new Patch("raids", null, "raids"),
            new Patch("recipestages", null, "recipestages", "crafttweaker"),
            new Patch("smelteryio", null, "smelteryio", "jei"),
            new Patch("spiceoflife", null, "spiceoflife"),
            new Patch("tcomplement", null, "tcomplement", "tconstruct", "jei"),
            new Patch("tconstruct", "mezz/jei/command/CommandTreeHEI.class", "tconstruct", "jei"),
            new Patch("thaumicconcilium", null, "thaumicconcilium"),
            new Patch("thermalexpansion", "mezz/jei/command/CommandTreeHEI.class", "thermalexpansion", "jei")
    );

    @Override
    public List<String> getMixinConfigs() {
        return PATCHES.stream().map(Patch::config).toList();
    }

    @Override
    public boolean shouldMixinConfigQueue(Context context) {
        return PATCHES.stream().anyMatch(patch -> patch.config().equals(context.mixinConfig()) && patch.enabled(context));
    }
}
