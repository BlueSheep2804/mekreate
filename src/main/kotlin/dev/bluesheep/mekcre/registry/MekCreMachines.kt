package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.block.attribute.AttributeNoOcclusion
import dev.bluesheep.mekcre.block.attribute.AttributeStateHeatLevel
import dev.bluesheep.mekcre.blockentity.MagmaCubeHeaterBlockEntity
import dev.bluesheep.mekcre.blockentity.MekanicalPressBlockEntity
import mekanism.common.attachments.component.AttachedSideConfig
import mekanism.common.block.attribute.AttributeParticleFX
import mekanism.common.block.attribute.AttributeUpgradeSupport
import mekanism.common.config.MekanismConfig
import mekanism.common.lib.transmitter.TransmissionType
import net.minecraft.world.level.block.Block

object MekCreMachines {
    val REGISTRY = MachineDeferredRegister(MekCreMod.MODID)

    val MEKANICAL_PRESS: SimpleMachineRegistryObject<MekanicalPressBlockEntity> = REGISTRY.registerSimple(
        "mekanical_press",
        AttachedSideConfig.ELECTRIC_MACHINE,
        MekanicalPressBlockEntity::addContainersToItem,
        ::MekanicalPressBlockEntity,
        MekanicalPressBlockEntity::class.java
    ) { builder ->
        builder
            .withSideConfig(TransmissionType.ITEM, TransmissionType.ENERGY)
            .withEnergyConfig(MekanismConfig.usage.enrichmentChamber, MekanismConfig.storage.enrichmentChamber)
    }

    val MAGMA_CUBE_HEATER: SimpleMachineRegistryObject<MagmaCubeHeaterBlockEntity> = REGISTRY.registerSimple(
        "magma_cube_heater",
        AttachedSideConfig.create(emptyMap()),
        {},
        ::MagmaCubeHeaterBlockEntity,
        MagmaCubeHeaterBlockEntity::class.java
    ) { builder ->
        builder
            .with(
                AttributeStateHeatLevel,
                AttributeNoOcclusion
            )
//            .with(AttributeUpgradeSupport.create(Upgrade.SPEED))
            .without(AttributeParticleFX::class.java, AttributeUpgradeSupport::class.java)
            .withCustomShape(arrayOf(Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)))
    }
}