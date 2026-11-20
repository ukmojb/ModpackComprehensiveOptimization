package dev.mco;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "mco", name = "ModpackComprehensiveOptimization", version = ModpackComprehensiveOptimization.VERSION,
        acceptedMinecraftVersions = "[1.12.2]")
public final class ModpackComprehensiveOptimization {
    public static final String VERSION = BuildInfo.VERSION;
    public static final Logger LOGGER = LogManager.getLogger("mco");
}
