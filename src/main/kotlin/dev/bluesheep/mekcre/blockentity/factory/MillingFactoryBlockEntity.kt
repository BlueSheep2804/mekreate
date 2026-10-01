package dev.bluesheep.mekcre.blockentity.factory

import com.simibubi.create.content.kinetics.millstone.MillingRecipe
import com.takenokoshi.mekaddonlib.recipe.type.IMekALRecipeTypeProvider
import com.takenokoshi.mekaddonlib.recipe_viewer.type.RVMekALRecipeTypeWrapper
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingFactory
import dev.bluesheep.mekcre.recipe_viewer.MekCreRecipeViewerTypes
import dev.bluesheep.mekcre.registry.MekCreMekanismRecipeTypes
import mekanism.common.attachments.containers.ContainerType
import mekanism.common.attachments.containers.item.ItemSlotsBuilder
import mekanism.common.registration.impl.ItemRegistryObject
import mekanism.common.tier.FactoryTier
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class MillingFactoryBlockEntity(blockProvider: Holder<Block>, pos: BlockPos, state: BlockState) : SingleInputProcessingFactory<MillingRecipe>(
    blockProvider, pos, state
) {
    companion object {
        fun addContainersToItem(tier: FactoryTier): (ItemRegistryObject<*>) -> Unit {
            return fun (holder: ItemRegistryObject<*>) {
                holder.addAttachmentOnlyContainers(ContainerType.ITEM) {
                    val builder = ItemSlotsBuilder.builder()
                    repeat(tier.processes) {
                        builder.addInput(1)
                        builder.addOutput(4)
                    }
                    builder.addEnergy()

                    return@addAttachmentOnlyContainers builder.build()
                }
            }
        }
    }

    override fun getOutputCount(): Int = 4

    override fun getRecipeType(): IMekALRecipeTypeProvider<*, MillingRecipe?, *> {
        return MekCreMekanismRecipeTypes.MILLING
    }

    override fun getRecipeViewerTypes(): Array<RVMekALRecipeTypeWrapper<*, *, *>> {
        return arrayOf(MekCreRecipeViewerTypes.MILLING)
    }
}