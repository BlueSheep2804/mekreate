package dev.bluesheep.mekcre.blockentity.machine

import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.takenokoshi.mekaddonlib.recipe.type.IMekALRecipeTypeProvider
import com.takenokoshi.mekaddonlib.recipe_viewer.type.RVMekALRecipeTypeWrapper
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingMachine
import dev.bluesheep.mekcre.recipe_viewer.MekCreRecipeViewerTypes
import dev.bluesheep.mekcre.registry.MekCreMekanismRecipeTypes
import mekanism.common.attachments.containers.ContainerType
import mekanism.common.attachments.containers.item.ItemSlotsBuilder
import mekanism.common.registration.impl.ItemRegistryObject
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class MekanicalPressBlockEntity(blockProvider: Holder<Block>, pos: BlockPos, state: BlockState) : SingleInputProcessingMachine<PressingRecipe>(
    blockProvider, pos, state, 1
) {
    companion object {
        fun addContainersToItem(holder: ItemRegistryObject<*>) {
            holder.addAttachmentOnlyContainers(ContainerType.ITEM) {
                ItemSlotsBuilder.builder()
                    .addInput(1)
                    .addOutput(2)
                    .addEnergy()
                    .build()
            }
        }
    }

    override fun getOutputCount(): Int = 2

    override fun getRecipeType(): IMekALRecipeTypeProvider<*, PressingRecipe?, *> {
        return MekCreMekanismRecipeTypes.PRESS
    }

    override fun getRecipeViewerTypes(): Array<RVMekALRecipeTypeWrapper<*, *, *>> {
        return arrayOf(MekCreRecipeViewerTypes.PRESSING)
    }
}