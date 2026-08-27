package dev.bluesheep.mekcre.registry

import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import dev.bluesheep.mekcre.MekCreMod.REGISTRATE
import dev.bluesheep.mekcre.block.renderer.MechanicalInfuserBlockRenderer
import dev.bluesheep.mekcre.block.renderer.MechanicalInfuserBlockVisual
import dev.bluesheep.mekcre.blockentity.MechanicalInfuserBlockEntity
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer

object MekCreBlockEntities {
    val MECHANICAL_INFUSER: BlockEntityEntry<MechanicalInfuserBlockEntity> = REGISTRATE
        .blockEntity("mechanical_infuser", ::MechanicalInfuserBlockEntity)
        .visual {
            SimpleBlockEntityVisualizer.Factory { context, blockEntity, partialTick ->
                MechanicalInfuserBlockVisual(context, blockEntity, partialTick)
            }
        }
        .validBlock(MekCreBlocks.MECHANICAL_INFUSER::get)
        .renderer {
            NonNullFunction { context -> MechanicalInfuserBlockRenderer(context) }
        }
        .register()

    fun register() {}
}
