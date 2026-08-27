package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.block.MechanicalInfuserBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object MekCreBlocks {
    val REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(MekCreMod.MODID)

    val MECHANICAL_INFUSER: DeferredBlock<MechanicalInfuserBlock> = REGISTRY.registerBlock(
        "mechanical_infuser",
        ::MechanicalInfuserBlock,
        BlockBehaviour.Properties.ofFullCopy(Blocks.ANDESITE).noOcclusion()
    )
}