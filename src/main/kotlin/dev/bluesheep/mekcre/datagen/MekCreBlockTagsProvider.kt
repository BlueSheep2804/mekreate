package dev.bluesheep.mekcre.datagen

import com.takenokoshi.mekaddonlib.registration.MachineRegistryObject
import com.tterrag.registrate.providers.RegistrateTagsProvider
import dev.bluesheep.mekcre.registry.MekCreMachines
import mekanism.common.block.attribute.AttributeTier
import mekanism.common.tier.FactoryTier
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block

class MekCreBlockTagsProvider(
    provider: RegistrateTagsProvider.IntrinsicImpl<Block>
) {
    init {
        val mekanismMachines = MekCreMachines.REGISTRY.machines
            .filter { (getTier(it)?.ordinal ?: 0) <= FactoryTier.ULTIMATE.ordinal }
            .map { it.block.get() }
            .toTypedArray()
        val evolvedMachines = MekCreMachines.REGISTRY.machines
            .filter { (getTier(it)?.ordinal ?: 0) > FactoryTier.ULTIMATE.ordinal }
            .map { it.id }
            .toTypedArray()
        provider.addTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(*mekanismMachines)
            .addOptionals(*evolvedMachines)
    }

    private fun getTier(machine: MachineRegistryObject<*, *, *, *>): FactoryTier? {
        return machine.blockType.get(AttributeTier::class.java)?.tier as? FactoryTier
    }

    private fun TagsProvider.TagAppender<Block>.addOptionals(vararg locations: ResourceLocation): TagsProvider.TagAppender<Block> {
        for (location in locations) {
            this.addOptional(location)
        }
        return this
    }
}