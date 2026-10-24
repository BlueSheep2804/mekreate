package dev.bluesheep.mekcre.blockentity.semifactory

import com.simibubi.create.content.kinetics.crusher.CrushingRecipe
import com.takenokoshi.mekaddonlib.recipe.type.IMekALRecipeTypeProvider
import com.takenokoshi.mekaddonlib.recipe_viewer.type.RVMekALRecipeTypeWrapper
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingSemiFactory
import dev.bluesheep.mekcre.recipe_viewer.MekCreRecipeViewerTypes
import dev.bluesheep.mekcre.registry.MekCreMekanismRecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class WheelCrusherSemiFactoryBlockEntity(blockProvider: Holder<Block>, pos: BlockPos, state: BlockState, baselineMaxOperations: Int) : SingleInputProcessingSemiFactory<CrushingRecipe>(
    blockProvider, pos, state, baselineMaxOperations
) {
    override fun getRecipeViewerTypes(): Array<RVMekALRecipeTypeWrapper<*, *, *>> {
        return arrayOf(MekCreRecipeViewerTypes.CRUSHING)
    }

    override fun getRecipeType(): IMekALRecipeTypeProvider<*, CrushingRecipe?, *> {
        return MekCreMekanismRecipeTypes.CRUSHING
    }
}