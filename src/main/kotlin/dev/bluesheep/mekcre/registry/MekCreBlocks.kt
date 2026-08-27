package dev.bluesheep.mekcre.registry

import com.simibubi.create.content.decoration.encasing.CasingBlock
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem
import com.simibubi.create.foundation.block.connected.AllCTTypes
import com.simibubi.create.foundation.block.connected.CTSpriteShifter
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.BuilderTransformers
import com.simibubi.create.foundation.data.ModelGen.customItemModel
import com.simibubi.create.foundation.data.TagGen.axeOrPickaxe
import com.tterrag.registrate.util.entry.BlockEntry
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.MekCreMod.REGISTRATE
import dev.bluesheep.mekcre.block.MechanicalInfuserBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.MapColor

object MekCreBlocks {
    val MECHANICAL_INFUSER: BlockEntry<MechanicalInfuserBlock> = REGISTRATE.block(
        "mechanical_infuser",
        ::MechanicalInfuserBlock
    )
        .initialProperties { Blocks.ANDESITE }
        .properties { it.noOcclusion() }
        .transform(axeOrPickaxe())
        .blockstate(BlockStateGen.horizontalBlockProvider(true))
        .item(::AssemblyOperatorBlockItem)
        .transform(customItemModel())
        .register()

    val OSMIUM_CASING: BlockEntry<CasingBlock> = REGISTRATE.block(
        "osmium_casing",
        ::CasingBlock
    )
        .properties { it.mapColor(MapColor.WOOD) }
        .transform(BuilderTransformers.casing {
            CTSpriteShifter.getCT(
                AllCTTypes.OMNIDIRECTIONAL,
                MekCreMod.rl("block/osmium_casing"),
                MekCreMod.rl("block/osmium_casing_connected")
            )
        })
        .register()

    fun register() {}
}