package dev.bluesheep.mekcre.mixin;

import mekanism.api.text.ILangEntry;
import mekanism.common.content.blocktype.BlockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockType.class, remap = false)
public interface BlockTypeAccessor {
    @Mutable
    @Accessor("description")
    void setDescription(ILangEntry value);
}
