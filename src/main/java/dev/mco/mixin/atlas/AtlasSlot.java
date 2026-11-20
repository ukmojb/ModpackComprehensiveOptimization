package dev.mco.mixin.atlas;

import net.minecraft.client.renderer.texture.Stitcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;

@Mixin(Stitcher.Slot.class)
public abstract class AtlasSlot implements dev.mco.internal.cache.SlotInsertion {
    @Shadow @Final private int originX, originY, width, height;
    @Shadow private Stitcher.Holder holder;
    @Shadow private List<Stitcher.Slot> subSlots;
    @Unique private int mco$failedWidth = -1;
    @Unique private int mco$failedHeight = -1;

    @Inject(method = "addSlot", at = @At("HEAD"), cancellable = true)
    private void mco$place(Stitcher.Holder sprite, CallbackInfoReturnable<Boolean> result) {
        if (holder == null) result.setReturnValue(mco$insert(sprite, sprite.getWidth(), sprite.getHeight()));
    }

    @Override
    public boolean mco$insert(Stitcher.Holder sprite, int w, int h) {
        if (holder != null || w > width || h > height) return false;
        if (w == width && h == height) {
            holder = sprite;
            return true;
        }
        // 插入只会占用或细分空槽。某尺寸失败后，后续细分也无法为该尺寸创造空间。
        if (w == mco$failedWidth && h == mco$failedHeight) return false;
        if (subSlots == null) mco$split(w, h);
        for (int i = 0; i < subSlots.size(); i++) {
            if (((dev.mco.internal.cache.SlotInsertion) subSlots.get(i)).mco$insert(sprite, w, h)) return true;
        }
        mco$failedWidth = w;
        mco$failedHeight = h;
        return false;
    }

    @Unique
    private void mco$split(int w, int h) {
        int right = width - w;
        int bottom = height - h;
        subSlots = new ArrayList<>(3);
        subSlots.add(new Stitcher.Slot(originX, originY, w, h));
        if (right > 0 && bottom > 0) {
            if (Math.max(height, right) >= Math.max(width, bottom)) {
                subSlots.add(new Stitcher.Slot(originX, originY + h, w, bottom));
                subSlots.add(new Stitcher.Slot(originX + w, originY, right, height));
            } else {
                subSlots.add(new Stitcher.Slot(originX + w, originY, right, h));
                subSlots.add(new Stitcher.Slot(originX, originY + h, width, bottom));
            }
        } else if (right > 0) {
            subSlots.add(new Stitcher.Slot(originX + w, originY, right, h));
        } else if (bottom > 0) {
            subSlots.add(new Stitcher.Slot(originX, originY + h, w, bottom));
        }
    }
}
