package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.MekCreMod
import net.minecraft.world.item.BlockItem
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object MekCreItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(MekCreMod.MODID)

    val MECHANICAL_INFUSER: DeferredItem<BlockItem> = REGISTRY.registerSimpleBlockItem(MekCreBlocks.MECHANICAL_INFUSER)
}