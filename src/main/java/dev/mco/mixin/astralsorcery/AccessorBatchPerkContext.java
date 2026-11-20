package dev.mco.mixin.astralsorcery;

import hellfirepvp.astralsorcery.client.gui.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.util.BufferBatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.TreeMap;

@Mixin(value = BatchPerkContext.class, remap = false)
public interface AccessorBatchPerkContext {

    @Accessor("bufferGroups")
    TreeMap<BatchPerkContext.TextureObjectGroup, BufferBatch> mco$getBufferGroups();
}
