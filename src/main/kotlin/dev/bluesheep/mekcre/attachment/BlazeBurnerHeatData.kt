package dev.bluesheep.mekcre.attachment

import mekanism.common.capabilities.heat.BasicHeatCapacitor
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.common.util.INBTSerializable

class BlazeBurnerHeatData : INBTSerializable<CompoundTag> {
    val heatCapacitor: BasicHeatCapacitor = BasicHeatCapacitor.create(
        100.0,
        5.0,
        100.0,
        null
    ) {}

    override fun serializeNBT(registries: HolderLookup.Provider): CompoundTag {
        return heatCapacitor.serializeNBT(registries)
    }

    override fun deserializeNBT(registries: HolderLookup.Provider, tag: CompoundTag) {
        heatCapacitor.deserializeNBT(registries, tag)
    }
}