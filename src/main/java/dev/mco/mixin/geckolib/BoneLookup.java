package dev.mco.mixin.geckolib;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.geo.raw.pojo.Bone;
import software.bernie.geckolib3.geo.raw.pojo.RawGeoModel;
import software.bernie.geckolib3.geo.raw.tree.RawBoneGroup;
import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(value = RawGeometryTree.class, remap = false)
public abstract class BoneLookup {
    @Inject(method = "parseHierarchy", at = @At("HEAD"))
    private static void mco$begin(RawGeoModel model, ResourceLocation location, CallbackInfoReturnable<RawGeometryTree> result,
                                     @Share("bones") LocalRef<Map<String, RawBoneGroup>> index) {
        Set<String> names = new HashSet<>();
        for (Bone bone : model.getMinecraftGeometry()[0].getBones()) {
            if (bone == null || bone.getName() == null || !names.add(bone.getName())) return;
        }
        index.set(new HashMap<>());
    }

    @Redirect(method = "parseHierarchy", at = @At(value = "INVOKE",
            target = "Ljava/util/HashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), require = 2)
    private static Object mco$attach(HashMap<Object, Object> groups, Object name, Object group,
                                        @Share("bones") LocalRef<Map<String, RawBoneGroup>> index) {
        Object previous = groups.put(name, group);
        if (index.get() != null) index.get().put((String) name, (RawBoneGroup) group);
        return previous;
    }

    @Redirect(method = "parseHierarchy", at = @At(value = "INVOKE",
            target = "Lsoftware/bernie/geckolib3/geo/raw/tree/RawGeometryTree;getGroupFromHierarchy(Lsoftware/bernie/geckolib3/geo/raw/tree/RawGeometryTree;Ljava/lang/String;)Lsoftware/bernie/geckolib3/geo/raw/tree/RawBoneGroup;"))
    private static RawBoneGroup mco$parent(RawGeometryTree tree, String name,
                                              @Share("bones") LocalRef<Map<String, RawBoneGroup>> index) {
        return index.get() == null ? RawGeometryTree.getGroupFromHierarchy(tree, name) : index.get().get(name);
    }
}
