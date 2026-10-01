package dev.bluesheep.mekcre.blockentity.upgradeData

import mekanism.api.energy.IEnergyContainer
import mekanism.api.inventory.IInventorySlot
import mekanism.common.inventory.slot.EnergyInventorySlot
import mekanism.common.tile.component.ITileComponent
import mekanism.common.tile.interfaces.IRedstoneControl
import mekanism.common.upgrade.IUpgradeData
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag

class ProcessingMachineUpgradeData(
    val provider: HolderLookup.Provider?,
    val redstone: Boolean,
    val controlType: IRedstoneControl.RedstoneControl,
    val energyContainer: IEnergyContainer,
    val progress: IntArray,
    val energySlot: EnergyInventorySlot,
    val inputSlots: Array<out IInventorySlot>,
    val outputSlots: List<Array<out IInventorySlot>>,
    val sorting: Boolean,
    components: List<ITileComponent>
) : IUpgradeData {
    val components: CompoundTag = CompoundTag().apply {
        components.forEach { component ->
            component.write(this, provider)
        }
    }
}