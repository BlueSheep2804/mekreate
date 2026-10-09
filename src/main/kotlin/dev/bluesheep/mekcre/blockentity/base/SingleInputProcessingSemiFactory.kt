package dev.bluesheep.mekcre.blockentity.base

import com.simibubi.create.content.processing.recipe.ProcessingRecipe
import com.takenokoshi.mekaddonlib.blockentity.interfaces.IHasGuiSizeOffset
import dev.bluesheep.mekcre.blockentity.upgradeData.ProcessingMachineUpgradeData
import mekanism.api.IContentsListener
import mekanism.api.energy.IEnergyContainer
import mekanism.common.attachments.containers.ContainerType
import mekanism.common.attachments.containers.item.ItemSlotsBuilder
import mekanism.common.inventory.slot.EnergyInventorySlot
import mekanism.common.inventory.slot.InputInventorySlot
import mekanism.common.registration.impl.ItemRegistryObject
import mekanism.common.upgrade.IUpgradeData
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

abstract class SingleInputProcessingSemiFactory<R : ProcessingRecipe<*, *>>(
    blockProvider: Holder<Block>,
    pos: BlockPos,
    state: BlockState,
    baselineMaxOperations: Int
) : SingleInputProcessingMachine<R>(
    blockProvider, pos, state, baselineMaxOperations
), IHasGuiSizeOffset {
    companion object {
        fun addContainersToItem(holder: ItemRegistryObject<*>) {
            holder.addAttachmentOnlyContainers(ContainerType.ITEM) {
                ItemSlotsBuilder.builder()
                    .addInput(1)
                    .addOutput(16)
                    .addEnergy()
                    .build()
            }
        }
    }

    override fun getOutputCount(): Int = 16

    override fun getExtraHeight(): Int {
        return 18
    }

    override fun getInputInventorySlot(recipeCacheListener: IContentsListener?): InputInventorySlot {
        return InputInventorySlot.at(
            recipeCacheListener,
            38, 26
        )
    }

    override fun getEnergySlot(energyContainer: IEnergyContainer, listener: IContentsListener?): EnergyInventorySlot {
        return EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 38, 62)
    }

    override fun parseUpgradeData(provider: HolderLookup.Provider?, data: IUpgradeData) {
        if (data is ProcessingMachineUpgradeData) {
            redstone = data.redstone
            controlType = data.controlType
            energyContainer.energy = data.energyContainer.energy
            operatingTicks = data.progress.firstOrNull() ?: 0
            energySlot.deserializeNBT(provider, data.energySlot.serializeNBT(provider))
            data.inputSlots.firstOrNull()?.let {
                inputSlot.deserializeNBT(provider, it.serializeNBT(provider))
            }
            data.outputSlots.firstOrNull()?.forEachIndexed { index, slot ->
                outputSlot[index].stack = slot.stack
            }
            for (component in components) {
                component.read(data.components, provider)
            }
        }
    }
}