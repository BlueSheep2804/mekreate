package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.blockentity.MekanicalPressBlockEntity
import mekanism.common.attachments.component.AttachedSideConfig
import mekanism.common.config.MekanismConfig
import mekanism.common.lib.transmitter.TransmissionType

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
}