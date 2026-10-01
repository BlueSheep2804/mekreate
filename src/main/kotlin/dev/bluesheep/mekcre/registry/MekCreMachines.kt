package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.BlockEntityConstructor
import com.takenokoshi.mekaddonlib.registration.GuiSizedMachineRegistryObject
import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.block.attribute.AttributeDescription
import dev.bluesheep.mekcre.block.attribute.AttributeNoOcclusion
import dev.bluesheep.mekcre.block.attribute.AttributeStateHeatLevel
import dev.bluesheep.mekcre.blockentity.MagmaCubeHeaterBlockEntity
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingFactory
import dev.bluesheep.mekcre.blockentity.machine.MekanicalPressBlockEntity
import dev.bluesheep.mekcre.blockentity.factory.PressingFactoryBlockEntity
import dev.bluesheep.mekcre.blockentity.factory.MillingFactoryBlockEntity
import dev.bluesheep.mekcre.blockentity.machine.MekanicalMillBlockEntity
import dev.bluesheep.mekcre.mixin.BlockTypeAccessor
import dev.bluesheep.mekcre.mixin.BlockTypeBuilderAccessor
import mekanism.api.math.MathUtils
import mekanism.common.MekanismLang
import mekanism.common.attachments.component.AttachedSideConfig
import mekanism.common.block.attribute.AttributeParticleFX
import mekanism.common.block.attribute.AttributeTier
import mekanism.common.block.attribute.AttributeUpgradeSupport
import mekanism.common.block.attribute.AttributeUpgradeable
import mekanism.common.block.prefab.BlockTile
import mekanism.common.config.MekanismConfig
import mekanism.common.content.blocktype.Machine
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.registration.impl.ItemRegistryObject
import mekanism.common.tier.FactoryTier
import net.minecraft.world.level.block.Block

object MekCreMachines {
    val REGISTRY = MachineDeferredRegister(MekCreMod.MODID)

    val FACTORIES: Map<String, Map<FactoryTier, GuiSizedMachineRegistryObject<out SingleInputProcessingFactory<*>>>> = mapOf(
        registerFactory(
            "pressing",
            PressingFactoryBlockEntity::addContainersToItem,
            ::PressingFactoryBlockEntity,
            PressingFactoryBlockEntity::class.java
        ),
        registerFactory(
            "milling",
            MillingFactoryBlockEntity::addContainersToItem,
            ::MillingFactoryBlockEntity,
            MillingFactoryBlockEntity::class.java
        )
    )

    val MEKANICAL_PRESS: SimpleMachineRegistryObject<MekanicalPressBlockEntity> = REGISTRY.registerSimple(
        "mekanical_press",
        AttachedSideConfig.ELECTRIC_MACHINE,
        MekanicalPressBlockEntity::addContainersToItem,
        ::MekanicalPressBlockEntity,
        MekanicalPressBlockEntity::class.java
    ) { builder ->
        builder
            .with(
                AttributeUpgradeable { FACTORIES["pressing"]!![FactoryTier.BASIC]!!.block },
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
                AttributeUpgradeable { FACTORIES["milling"]!![FactoryTier.BASIC]!!.block },
                AttributeDescription("A simple machine used for milling items.")
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

    private fun <BE: SingleInputProcessingFactory<*>> registerFactory(
        name: String,
        holder: (FactoryTier) -> ((ItemRegistryObject<*>) -> Unit),
        beConstructor: BlockEntityConstructor<BE, Machine<BE>, BlockTile.BlockTileModel<BE, Machine<BE>>>,
        beClass: Class<BE>
    ): Pair<String, Map<FactoryTier, GuiSizedMachineRegistryObject<BE>>> {
        return name to FactoryTier.entries.associateWith { tier ->
            REGISTRY.registerGuiSized(
                "${tier.name.lowercase()}_${name}_factory",
                AttachedSideConfig.ELECTRIC_MACHINE,
                holder(tier),
                beConstructor,
                beClass
            ) { builder ->
                builder
                    .with(AttributeTier(tier))
                    .withSideConfig(TransmissionType.ITEM, TransmissionType.ENERGY)
                    .withEnergyConfig(
                        MekanismConfig.usage.enrichmentChamber
                    ) {
                        MathUtils.clampToLong(
                            (MekanismConfig.storage.enrichmentChamber.get() * 0.5)
                                .coerceAtLeast(
                                    MekanismConfig.usage.enrichmentChamber.get().toDouble()
                                ) * tier.processes.toDouble()
                        )
                    }
                ((builder as BlockTypeBuilderAccessor).holder as BlockTypeAccessor).setDescription(MekanismLang.DESCRIPTION_FACTORY)
                if (tier != FactoryTier.ULTIMATE) {
                    builder.with(AttributeUpgradeable {
                        REGISTRY.machines.firstOrNull { it.name == "${tier.next().name.lowercase()}_${name}_factory" }?.block
                    })
                }
                return@registerGuiSized builder
            }
        }
    }

    private fun FactoryTier.next(): FactoryTier {
        return FactoryTier.entries[(this.ordinal + 1) % FactoryTier.entries.size]
    }
}