package dev.bluesheep.mekcre.mixinImpl

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity
import dev.bluesheep.mekcre.registry.MekCreAttachmentTypes
import mekanism.api.heat.IHeatCapacitor
import mekanism.api.heat.IHeatHandler
import mekanism.common.capabilities.Capabilities
import mekanism.common.capabilities.heat.ITileHeatHandler
import net.minecraft.core.Direction

object BlazeBurnerBlockEntityMixinImpl {
    @JvmStatic
    fun tick(blockEntity: BlazeBurnerBlockEntity) {
        val level = blockEntity.level ?: return
        if (level.isClientSide) return

        val data = blockEntity.getData(MekCreAttachmentTypes.BLAZE_BURNER_HEAT)
        val handler = (blockEntity as ITileHeatHandler)

        when (blockEntity.heatLevelFromBlock) {
            HeatLevel.FADING, HeatLevel.KINDLED -> {
                handler.handleHeat(240.0)  // TODO: Config
            }
            HeatLevel.SEETHING -> {
                handler.handleHeat(2400.0)  // TODO: Config
            }
            else -> {}
        }

        handler.simulate()

        val previousHeat = data.heatCapacitor.heat
        handler.updateHeatCapacitors(null)

        if (previousHeat != data.heatCapacitor.heat) {
            blockEntity.setChanged()
        }
    }

    @JvmStatic
    fun getHeatCapacitors(blockEntity: BlazeBurnerBlockEntity): List<IHeatCapacitor> {
        val data = blockEntity.getData(MekCreAttachmentTypes.BLAZE_BURNER_HEAT)
        return listOf(data.heatCapacitor)
    }

    @JvmStatic
    fun getAdjacent(blockEntity: BlazeBurnerBlockEntity, side: Direction): IHeatHandler? {
        val adjacentPos = blockEntity.blockPos.relative(side)
        val level = blockEntity.level ?: return null
        if (!level.hasChunkAt(adjacentPos)) return null

        return Capabilities.HEAT.getCapability(
            level,
            adjacentPos,
            null,
            null,
            side.opposite
        )
    }
}