package dev.bluesheep.mekcre.datagen

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.LocationUtil
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class MekCreBlockModelProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : BlockStateProvider(
    output,
    MekCreMod.MODID,
    existingFileHelper
) {
    override fun registerStatesAndModels() {
        MekCreMachines.REGISTRY.blockRegister.primaryEntries.forEach { machine ->
            val texturePath = texturePath(machine.id)
            horizontalBlock(
                machine.get(),
                models().withExistingParent(machine.registeredName, LocationUtil.rlMekanism("block/machine"))
                    .texture("sides", texturePath.withSuffix("_side"))
                    .texture("front", texturePath.withSuffix("_front"))
                    .texture("up", texturePath.withSuffix("_top"))
                    .texture("down", texturePath.withSuffix("_bottom"))
            )
        }
    }

    private fun texturePath(resourceLocation: ResourceLocation): ResourceLocation {
        return resourceLocation.withPrefix("block/")
    }
}