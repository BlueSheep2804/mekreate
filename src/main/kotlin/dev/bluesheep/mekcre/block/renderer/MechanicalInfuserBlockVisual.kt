package dev.bluesheep.mekcre.block.renderer

import com.simibubi.create.content.kinetics.base.ShaftVisual
import dev.bluesheep.mekcre.blockentity.MechanicalInfuserBlockEntity
import dev.engine_room.flywheel.api.visual.DynamicVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual

class MechanicalInfuserBlockVisual(
    context: VisualizationContext,
    blockEntity: MechanicalInfuserBlockEntity,
    partialTick: Float
) : ShaftVisual<MechanicalInfuserBlockEntity>(
    context,
    blockEntity,
    partialTick
), SimpleDynamicVisual {
    override fun beginFrame(ctx: DynamicVisual.Context?) {}
}