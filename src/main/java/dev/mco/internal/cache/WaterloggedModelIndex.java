package dev.mco.internal.cache;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class WaterloggedModelIndex {
    private static final ThreadLocal<WaterloggedModelIndex> CURRENT = new ThreadLocal<>();
    private static long registrationVersion;

    private final Map<String, Set<String>> names = new HashMap<>();
    private long version = -1;
    private boolean checkExplicitSet;
    private final Set<Block> unnamed = new HashSet<>();

    private WaterloggedModelIndex() {}

    public static void bake(Runnable original) {
        WaterloggedModelIndex parent = CURRENT.get();
        CURRENT.set(new WaterloggedModelIndex());
        try {
            original.run();
        } finally {
            if (parent == null) CURRENT.remove();
            else CURRENT.set(parent);
        }
    }

    public static WaterloggedModelIndex current() {
        return CURRENT.get();
    }

    // 登记和查询方法共用 WaterloggedBlockApi 的类锁。
    public static void registrationsChanged() {
        registrationVersion++;
    }

    public boolean contains(ResourceLocation model, Set<ResourceLocation> models, Set<Block> blocks) {
        if (version != registrationVersion) {
            names.clear();
            unnamed.clear();
            checkExplicitSet = false;
            for (ResourceLocation id : models) {
                if (id.getClass() == ResourceLocation.class) add(id);
                else checkExplicitSet = true;
            }
            for (Block block : blocks) {
                ResourceLocation name = block.getRegistryName();
                if (name == null) unnamed.add(block);
                else add(name);
            }
            version = registrationVersion;
        } else if (!unnamed.isEmpty()) {
            // 注册名称可能在 registerRenderedBlock 之后首次分配。
            unnamed.removeIf(block -> {
                ResourceLocation name = block.getRegistryName();
                if (name == null) return false;
                add(name);
                return true;
            });
        }
        Set<String> paths = names.get(model.getNamespace());
        if (paths != null && paths.contains(model.getPath())) return true;
        return checkExplicitSet && models.contains(new ResourceLocation(model.getNamespace(), model.getPath()));
    }

    private void add(ResourceLocation id) {
        names.computeIfAbsent(id.getNamespace(), key -> new HashSet<>()).add(id.getPath());
    }
}
