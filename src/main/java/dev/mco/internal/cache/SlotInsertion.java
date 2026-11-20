package dev.mco.internal.cache;

import net.minecraft.client.renderer.texture.Stitcher;

public interface SlotInsertion {
    boolean mco$insert(Stitcher.Holder sprite, int width, int height);
}
