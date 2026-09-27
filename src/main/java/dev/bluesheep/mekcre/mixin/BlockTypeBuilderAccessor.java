package dev.bluesheep.mekcre.mixin;

import mekanism.common.content.blocktype.BlockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockType.BlockTypeBuilder.class, remap = false)
public interface BlockTypeBuilderAccessor {
    @Accessor("holder")
    BlockType getHolder();
}
