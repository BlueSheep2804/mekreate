package dev.bluesheep.mekcre.datagen

import com.tterrag.registrate.providers.RegistrateTagsProvider
import dev.bluesheep.mekcre.registry.MekCreMachines
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block

class MekCreBlockTagsProvider(
    provider: RegistrateTagsProvider.IntrinsicImpl<Block>
) {
    init {
        provider.addTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(*MekCreMachines.REGISTRY.blockRegister.primaryEntries.map { it.value() }.toTypedArray())
    }
}