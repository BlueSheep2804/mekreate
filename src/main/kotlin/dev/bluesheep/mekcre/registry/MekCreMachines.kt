package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.GuiSizedMachineRegistryObject
import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.block.attribute.AttributeDescription
import dev.bluesheep.mekcre.block.attribute.AttributeNoOcclusion
import dev.bluesheep.mekcre.block.attribute.AttributeStateHeatLevel
import dev.bluesheep.mekcre.blockentity.MagmaCubeHeaterBlockEntity
import dev.bluesheep.mekcre.blockentity.MekanicalPressBlockEntity
import dev.bluesheep.mekcre.blockentity.MekanicalPressFactoryBlockEntity
import dev.bluesheep.mekcre.mixin.BlockTypeAccessor
import dev.bluesheep.mekcre.mixin.BlockTypeBuilderAccessor
import mekanism.api.math.MathUtils
import mekanism.common.MekanismLang
import mekanism.common.attachments.component.AttachedSideConfig
import mekanism.common.block.attribute.AttributeParticleFX
import mekanism.common.block.attribute.AttributeTier
import mekanism.common.block.attribute.AttributeUpgradeSupport
import mekanism.common.block.attribute.AttributeUpgradeable
import mekanism.common.config.MekanismConfig
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.tier.FactoryTier
import net.minecraft.world.level.block.Block

object MekCreMachines {
    val REGISTRY = MachineDeferredRegister(MekCreMod.MODID)

    val MEKANICAL_PRESS_FACTORIES: Map<FactoryTier, GuiSizedMachineRegistryObject<MekanicalPressFactoryBlockEntity>> = buildMap {
        FactoryTier.entries.forEach { tier ->
            val machine = REGISTRY.registerGuiSized(
                "${tier.name.lowercase()}_pressing_factory",
                AttachedSideConfig.ELECTRIC_MACHINE,
                MekanicalPressFactoryBlockEntity.addContainersToItem(tier.processes),
                ::MekanicalPressFactoryBlockEntity,
                MekanicalPressFactoryBlockEntity::class.java
            ) { builder ->
                builder
                    .with(AttributeTier(tier))
                    .withSideConfig(TransmissionType.ITEM, TransmissionType.ENERGY)
                    .withEnergyConfig(
                        MekanismConfig.usage.enrichmentChamber
                    ) {
                        MathUtils.clampToLong(
                            (MekanismConfig.storage.enrichmentChamber.get() * 0.5)
                                .coerceAtLeast(MekanismConfig.usage.enrichmentChamber.get().toDouble()) * tier.processes.toDouble()
                        )
                    }
                ((builder as BlockTypeBuilderAccessor).holder as BlockTypeAccessor).setDescription(MekanismLang.DESCRIPTION_FACTORY)
                if (tier != FactoryTier.ULTIMATE) {
                    builder.with(AttributeUpgradeable { MEKANICAL_PRESS_FACTORIES[FactoryTier.entries[tier.ordinal + 1]]?.block })
                }
                return@registerGuiSized builder
            }
            this[tier] = machine
        }
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
                AttributeUpgradeable { MEKANICAL_PRESS_FACTORIES[FactoryTier.BASIC]!!.block },
                AttributeDescription("A simple machine used for pressing items.")
            )
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
                AttributeNoOcclusion,
                AttributeDescription("A Magma Cube that converts Mekanism heat into a heat source for Create.")
            )
//            .with(AttributeUpgradeSupport.create(Upgrade.SPEED))
            .without(AttributeParticleFX::class.java, AttributeUpgradeSupport::class.java)
            .withCustomShape(arrayOf(Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)))
    }
}