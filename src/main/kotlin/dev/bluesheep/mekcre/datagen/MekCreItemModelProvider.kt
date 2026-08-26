package dev.bluesheep.mekcre.datagen

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.registry.MekCreMachines
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class MekCreItemModelProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(
    output,
    MekCreMod.MODID,
    existingFileHelper
) {
    override fun registerModels() {
        MekCreMachines.REGISTRY.blockRegister.primaryEntries.forEach { machine ->
            simpleBlockItem(machine.get())
        }
    }
}