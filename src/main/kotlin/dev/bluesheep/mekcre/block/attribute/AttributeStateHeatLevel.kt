package dev.bluesheep.mekcre.block.attribute

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock
import mekanism.common.block.attribute.AttributeState
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property

object AttributeStateHeatLevel : AttributeState {
    override fun fillBlockStateContainer(
        block: Block,
        properties: MutableList<Property<*>>
    ) {
        properties.add(BlazeBurnerBlock.HEAT_LEVEL)
    }

    override fun getDefaultState(state: BlockState): BlockState {
        return state.setValue(
            BlazeBurnerBlock.HEAT_LEVEL,
            BlazeBurnerBlock.HeatLevel.NONE
        )
    }

    override fun copyStateData(
        oldState: BlockState,
        newState: BlockState
    ): BlockState {
        return if (
            oldState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL) &&
            newState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)
        ) {
            newState.setValue(
                BlazeBurnerBlock.HEAT_LEVEL,
                oldState.getValue(BlazeBurnerBlock.HEAT_LEVEL)
            )
        } else {
            newState
        }
    }
}