package dev.mco.mixin.astralsorcery;

import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiJournalPerkTree.class, remap = false)
public interface PerkTreeTextures {
    @Accessor("textureSearchTextBG")
    static AbstractRenderableTexture getTextureSearchTextBG() {
        throw new AssertionError();
    }
}
