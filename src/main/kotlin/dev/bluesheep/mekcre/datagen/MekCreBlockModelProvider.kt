package dev.bluesheep.mekcre.datagen

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.LocationUtil.rl
import dev.bluesheep.mekcre.LocationUtil.rlCreate
import dev.bluesheep.mekcre.LocationUtil.rlMekanism
import dev.bluesheep.mekcre.LocationUtil.rlMinecraft
import dev.bluesheep.mekcre.registry.MekCreFactoryType
import dev.bluesheep.mekcre.registry.MekCreSemiFactoryType
import fr.iglee42.evolvedmekanism.EvolvedMekanism
import mekanism.common.tier.FactoryTier
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder
import net.neoforged.neoforge.common.data.ExistingFileHelper

class MekCreBlockModelProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : BlockStateProvider(
    output,
    MekCreMod.MODID,
    existingFileHelper
) {
    val factoryLedModels = FactoryTier.entries.associateWith { tier ->
        models().withExistingParent(
            "composite/front_led/${tier.name.lowercase()}",
            if (tier.ordinal > FactoryTier.ULTIMATE.ordinal) {
                ResourceLocation.fromNamespaceAndPath(EvolvedMekanism.MODID, "block/factory/front_led/${tier.name.lowercase()}")
            } else {
                rlMekanism("block/factory/front_led/${tier.name.lowercase()}")
            }
        )
    }

    override fun registerStatesAndModels() {
        MekCreFactoryType.ALL.forEach { type ->
            val base = BlockModelBuilder(
                rl("base"),
                models().existingFileHelper
            ).parent(models().getExistingFile(rl("block/factory/${type.name.lowercase()}")))
            type.factories.forEach { (tier, machine) ->
                horizontalBlock(
                    machine.block.get(),
                    models().getBuilder("block/${machine.id.path}")
                        .parent(models().getExistingFile(rlMinecraft("block/block")))
                        .texture("particle", rl("block/factory/${type.name.lowercase()}/front"))
                        .customLoader { builder, helper ->
                            CompositeModelBuilder.begin(builder, helper)
                        }
                        .child("base", base)
                        .child("front_led", factoryLedModels[tier])
                        .end()
                )
            }
        }

        MekCreSemiFactoryType.ALL.forEach { type ->
            val base = BlockModelBuilder(
                rl("base"),
                models().existingFileHelper
            ).parent(models().getExistingFile(rl("block/semifactory/${type.name.lowercase()}")))
            type.factories.forEach { (tier, machine) ->
                horizontalBlock(
                    machine.block.get(),
                    models().getBuilder("block/${machine.id.path}")
                        .parent(models().getExistingFile(rlMinecraft("block/block")))
                        .texture("particle", rl("block/semifactory/${type.name.lowercase()}/front"))
                        .customLoader { builder, helper ->
                            CompositeModelBuilder.begin(builder, helper)
                        }
                        .child("base", base)
                        .child("front_led", factoryLedModels[tier])
                        .end()
                )
            }
        }

        horizontalBlock(
            MekCreMachines.MAGMA_CUBE_HEATER.block.get(),
            models().withExistingParent("magma_cube_heater", rlCreate("block/blaze_burner/block"))
                .renderType("cutout")
        )

        MekCreMachines.REGISTRY.blockRegister.primaryEntries.forEach { machine ->
            if (registeredBlocks.contains(machine.get())) return@forEach
            val texturePath = texturePath(machine.id)
            horizontalBlock(
                machine.get(),
                models().withExistingParent(machine.registeredName, rlMekanism("block/machine"))
                    .texture("sides", texturePath.withSuffix("_side"))
                    .texture("south", texturePath.withSuffix("_back"))
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