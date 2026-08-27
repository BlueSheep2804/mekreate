package dev.bluesheep.mekcre.datagen

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.registry.MekCreBlocks
import dev.bluesheep.mekcre.registry.MekCreMachines
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class MekCreBlockTagsProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(
    output,
    lookupProvider,
    MekCreMod.MODID,
    existingFileHelper
) {
    override fun addTags(p0: HolderLookup.Provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(*MekCreMachines.REGISTRY.blockRegister.primaryEntries.map { it.value() }.toTypedArray())
            .add(MekCreBlocks.MECHANICAL_INFUSER.get())

        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(MekCreBlocks.MECHANICAL_INFUSER.get())
    }
}