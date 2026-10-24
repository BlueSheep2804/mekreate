package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.block.attribute.AttributeDescription
import dev.bluesheep.mekcre.block.attribute.AttributeNoOcclusion
import dev.bluesheep.mekcre.block.attribute.AttributeStateHeatLevel
import dev.bluesheep.mekcre.blockentity.MagmaCubeHeaterBlockEntity
import dev.bluesheep.mekcre.blockentity.machine.WheelCrusherBlockEntity
import dev.bluesheep.mekcre.blockentity.machine.MekanicalMillBlockEntity
import dev.bluesheep.mekcre.blockentity.machine.MekanicalPressBlockEntity
import mekanism.common.attachments.component.AttachedSideConfig
import mekanism.common.block.attribute.AttributeParticleFX
import mekanism.common.block.attribute.AttributeUpgradeSupport
import mekanism.common.block.attribute.AttributeUpgradeable
import mekanism.common.config.MekanismConfig
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.tier.FactoryTier
import net.minecraft.world.level.block.Block

object MekCreMachines {
    val REGISTRY = MachineDeferredRegister(MekCreMod.MODID)

    init {
        MekCreFactoryType.ALL.forEach { it.register(REGISTRY) }
        MekCreSemiFactoryType.ALL.forEach { it.register(REGISTRY) }
    }

    val MEKANICAL_PRESS: SimpleMachineRegistryObject<MekanicalPressBlockEntity> = REGISTRY.registerSimple(
        "mekanical_press",
        AttachedSideConfig.ELECTRIC_MACHINE,
        MekanicalPressBlockEntity::addContainersToItem,
        ::MekanicalPressBlockEntity,
        MekanicalPressBlockEntity::class.java
    ) { builder ->
        builder
            .with(
                AttributeUpgradeable { MekCreFactoryType.PRESSING.factories[FactoryTier.BASIC]!!.block },
                AttributeDescription("A simple machine used for pressing items.")
            )
            .withSideConfig(TransmissionType.ITEM, TransmissionType.ENERGY)
            .withEnergyConfig(MekanismConfig.usage.enrichmentChamber, MekanismConfig.storage.enrichmentChamber)
    }

    val MEKANICAL_MILL: SimpleMachineRegistryObject<MekanicalMillBlockEntity> = REGISTRY.registerSimple(
        "mekanical_mill",
        AttachedSideConfig.ELECTRIC_MACHINE,
        MekanicalMillBlockEntity::addContainersToItem,
        ::MekanicalMillBlockEntity,
        MekanicalMillBlockEntity::class.java
    ) { builder ->
        builder
            .with(
                AttributeUpgradeable { MekCreFactoryType.MILLING.factories[FactoryTier.BASIC]!!.block },
                AttributeDescription("A simple machine used for milling items.")
            )
            .withSideConfig(TransmissionType.ITEM, TransmissionType.ENERGY)
            .withEnergyConfig(MekanismConfig.usage.enrichmentChamber, MekanismConfig.storage.enrichmentChamber)
    }

    val WHEEL_CRUSHER: SimpleMachineRegistryObject<WheelCrusherBlockEntity> = REGISTRY.registerSimple(
        "wheel_crusher",
        AttachedSideConfig.ELECTRIC_MACHINE,
        WheelCrusherBlockEntity::addContainersToItem,
        ::WheelCrusherBlockEntity,
        WheelCrusherBlockEntity::class.java
    ) { builder ->
        builder
            .with(
                AttributeUpgradeable { MekCreSemiFactoryType.WHEEL_CRUSHING.factories[FactoryTier.BASIC]!!.block },
                AttributeDescription("A simple machine used for crushing items.")
            )
            .withSideConfig(TransmissionType.ITEM, TransmissionType.ENERGY)
            .withEnergyConfig(MekanismConfig.usage.crusher, MekanismConfig.storage.crusher)
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
                AttributeNoOcclusion,
                AttributeDescription("A Magma Cube that converts Mekanism heat into a heat source for Create.")
            )
//            .with(AttributeUpgradeSupport.create(Upgrade.SPEED))
            .without(AttributeParticleFX::class.java, AttributeUpgradeSupport::class.java)
            .withCustomShape(arrayOf(Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)))
    }
}
