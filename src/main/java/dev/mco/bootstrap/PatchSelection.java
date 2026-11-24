package dev.mco.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.Context;

public final class PatchSelection {
    private static final Logger LOGGER = LogManager.getLogger("mco");
    private record Target(String mod, String resource, String digest) {}
    private static final List<Target> TARGETS = List.of(
            new Target("base", "com/teamacronymcoders/base/recipesystem/loader/AssetJsonRecipeLoader.class", "a4d8c13ae0b5e8a3c3ac60857c0b6eebfbeda596fc9237672b4647dfe8ba0b0a"),
            new Target("libnine", "io/github/phantamanta44/libnine/client/model/L9CachingModelLoader.class", "94e769207064efff41c19a9bf08c2c8be6d2f419b57e1d327200e473e484fea3"),
            new Target("libnine", "io/github/phantamanta44/libnine/client/model/L9Models.class", "09bbcbc9f3da8e4d9fd9c5c6b78c710d45ba81a22fcaa66701c7ef4f61752f1d"),
            new Target("geckolib3", "software/bernie/geckolib3/geo/raw/tree/RawGeometryTree.class", "56233557774f06b8a27777f062a8407bba015b12dab4d71c06dd2e893d4be6e6"),
            new Target("mantle", "slimeknights/mantle/util/RecipeMatch$ItemCombination.class", "ecb5a1ee40a2eaa921c15d5690b7b5fdb93252edb781cd92db39882c9a5a806a"),
            new Target("chancecubes", "chanceCubes/sounds/CustomSoundsLoader.class", "eba4abd4fc2b054173bbf561f9bf1ea3fd47b9d73b668d5fb1b0dcb2119316b0"),
            new Target("farmers_future_delight", "xy177/farmersfuturedelight/client/ClientEventHandler.class", "7552a608df15b56286217dd15cb32116e636ae061f1f4bf6d66a104606a524d3"),
            new Target("farmers_future_delight", "xy177/farmersfuturedelight/api/WaterloggedBlockApi.class", "2b1ea16231757a4637e88a175cf0d240f3292aeaf917fd321dd35f90b42ce05f"),
            new Target("avaritia.recipes", "morph/avaritia/recipe/AvaritiaRecipeManager.class", "e647af22b872de5b07e2217dade3588d542b8167abf35e6c29e676294815cc16")
    );

    public static boolean enabled(Context context, String module) {
        if (!Boolean.parseBoolean(System.getProperty("mco." + module, "true"))
                || module.equals("avaritia.recipes") && !Boolean.parseBoolean(System.getProperty("mco.avaritia", "true"))) {
            LOGGER.info("{} 已关闭", module);
            return false;
        }
        if (!module.equals("atlas") && !context.isModPresent(module.equals("avaritia.recipes") ? "avaritia" : module)) {
            LOGGER.info("{} 未安装", module);
            return false;
        }
        if (!context.inDev()) {
            for (Target target : TARGETS) {
                if (target.mod().equals(module) && !matches(target)) {
                    LOGGER.warn("{} 的目标类与已验证版本不匹配，跳过补丁", target.resource());
                    return false;
                }
            }
        }
        LOGGER.info("启用 {}", module);
        return true;
    }

    private static boolean matches(Target target) {
        try (InputStream stream = PatchSelection.class.getClassLoader().getResourceAsStream(target.resource())) {
            if (stream == null) return false;
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(stream.readAllBytes());
            return target.digest().equals(HexFormat.of().formatHex(digest));
        } catch (IOException e) {
            LOGGER.warn("读取 {} 失败", target.resource(), e);
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private PatchSelection() {}
}
