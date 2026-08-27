package dev.bluesheep.mekcre.block

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock
import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.foundation.block.IBE
import dev.bluesheep.mekcre.blockentity.MechanicalInfuserBlockEntity
import dev.bluesheep.mekcre.registry.MekCreBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class MechanicalInfuserBlock(properties: Properties) : HorizontalKineticBlock(properties), IRotate, IBE<MechanicalInfuserBlockEntity> {
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val preferredSide = getPreferredHorizontalFacing(context)
        if (preferredSide != null) {
            return defaultBlockState().setValue(HORIZONTAL_FACING, preferredSide)
        }
        return super.getStateForPlacement(context)
    }

    override fun getRotationAxis(state: BlockState?): Direction.Axis? {
        return state?.getValue(HORIZONTAL_FACING)?.axis
    }

    override fun hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean {
        return face.axis.equals(state.getValue(HORIZONTAL_FACING).axis)
    }

    override fun getBlockEntityClass(): Class<MechanicalInfuserBlockEntity> {
        return MechanicalInfuserBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out MechanicalInfuserBlockEntity> {
        return MekCreBlockEntities.MECHANICAL_INFUSER.get()
    }
}