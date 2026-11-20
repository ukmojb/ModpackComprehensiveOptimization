package dev.mco.mixin.astralsorcery;

import dev.mco.mixin.early.AccessorBufferBuilder;
import io.netty.util.internal.PlatformDependent;
import dev.mco.ModpackComprehensiveOptimization;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.util.BufferBatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.TreeMap;

@Mixin(GuiJournalPerkTree.class)
public abstract class MixinGuiJournalPerkTree extends GuiScreenJournal {

    @Shadow(remap = false)
    @Final
    @Mutable
    private static BufferBatch drawBufferConnections;

    @Shadow(remap = false)
    private static BatchPerkContext drawBuffer;

    @Shadow(remap = false)
    private static BatchPerkContext.TextureObjectGroup searchContext;

    @Shadow(remap = false)
    private static BatchPerkContext.TextureObjectGroup sealContext;

    protected MixinGuiJournalPerkTree(int bookmarkIndex) {
        super(bookmarkIndex);
    }

    @Shadow(remap = false)
    public static void initializeDrawBuffer() {
        throw new AssertionError();
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lhellfirepvp/astralsorcery/client/util/BufferBatch;make()Lhellfirepvp/astralsorcery/client/util/BufferBatch;",
                    remap = false
            ),
            remap = false
    )
    private static BufferBatch mco$deferStaticConnectionBuffer() {
        return null;
    }

    @Inject(method = "initGui()V", at = @At("HEAD"))
    private void mco$ensureBuffersOnInit(CallbackInfo ci) {
        mco$ensureDrawBuffers();
    }

    @Inject(method = "drawPerkTree(F)V", at = @At("HEAD"), remap = false)
    private void mco$ensureBuffersOnDraw(float partialTicks, CallbackInfo ci) {
        mco$ensureDrawBuffers();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        mco$releaseDrawBuffers();
    }

    @Unique
    private static void mco$ensureDrawBuffers() {
        boolean allocated = false;
        if (drawBufferConnections == null) {
            drawBufferConnections = BufferBatch.make();
            allocated = true;
        }
        if (drawBuffer == null || searchContext == null || sealContext == null) {
            initializeDrawBuffer();
            allocated = true;
        }
        if (allocated) {
            ModpackComprehensiveOptimization.LOGGER.debug("Allocated lazy AstralSorcery perk tree draw buffers");
        }
    }

    @Unique
    private static void mco$release(BufferBatch batch) {
        AccessorBufferBuilder builder = (AccessorBufferBuilder) (Object) batch.getBuffer();
        PlatformDependent.freeDirectBuffer(builder.mco$getByteBuffer());
    }

    @Unique
    private static void mco$releaseDrawBuffers() {
        int released = 0;
        if (drawBufferConnections != null) {
            mco$release(drawBufferConnections);
            drawBufferConnections = null;
            released++;
        }
        if (drawBuffer != null) {
            TreeMap<BatchPerkContext.TextureObjectGroup, BufferBatch> groups =
                    ((AccessorBatchPerkContext) (Object) drawBuffer).mco$getBufferGroups();
            if (groups != null) {
                for (BufferBatch batch : groups.values()) {
                    if (batch != null) {
                        mco$release(batch);
                        released++;
                    }
                }
                groups.clear();
            }
            drawBuffer = null;
        }
        searchContext = null;
        sealContext = null;
        if (released > 0) {
            ModpackComprehensiveOptimization.LOGGER.debug("Released {} AstralSorcery perk tree direct buffer batches on GUI close", released);
        }
    }
}
