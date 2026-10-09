package dev.bluesheep.mekcre.util

import com.takenokoshi.mekaddonlib.registration.GuiSizedMachineRegistryObject
import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingSemiFactory
import dev.bluesheep.mekcre.blockentity.semifactory.CrusherSemiFactoryBlockEntity
import dev.bluesheep.mekcre.mixin.BlockTypeAccessor
import dev.bluesheep.mekcre.mixin.BlockTypeBuilderAccessor
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.util.TierExtensions.next
import mekanism.api.math.MathUtils
import mekanism.common.MekanismLang
import mekanism.common.attachments.component.AttachedSideConfig
import mekanism.common.block.attribute.AttributeTier
import mekanism.common.block.attribute.AttributeUpgradeable
import mekanism.common.config.MekanismConfig
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.registration.impl.BlockRegistryObject
import mekanism.common.tier.FactoryTier
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

data class MekCreSemiFactoryType<BE: SingleInputProcessingSemiFactory<*>>(
    val name: String,
    val baseMachine: () -> BlockRegistryObject<*, *>,
    val beConstructor: SemiFactoryConstructor<BE>,
    val beClass: Class<BE>
) {
    companion object {
        val CRUSHING = MekCreSemiFactoryType(
            "crushing",
            { MekCreMachines.MEKANICAL_CRUSHER.block },
            ::CrusherSemiFactoryBlockEntity,
            CrusherSemiFactoryBlockEntity::class.java
        )

        val ALL = listOf(CRUSHING)
    }

    val factories
        get() = MekCreMachines.SEMI_FACTORIES[this]!!

    fun register(registry: MachineDeferredRegister): Map<FactoryTier, GuiSizedMachineRegistryObject<BE>> {
        return FactoryTier.entries.associateWith { tier ->
            registry.registerGuiSized(
                "${tier.name.lowercase()}_${name}_semi_factory",
                AttachedSideConfig.ELECTRIC_MACHINE,
                SingleInputProcessingSemiFactory.Companion::addContainersToItem,
                { blockProvider, pos, state -> beConstructor.create(blockProvider, pos, state, tier.processes) },
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
                if (tier.ordinal < FactoryTier.entries.size - 1) {
                    builder.with(AttributeUpgradeable {
                        factories[tier.next()]?.block
                    })
                }
                return@registerGuiSized builder
            }
        }
    }

    fun interface SemiFactoryConstructor<BE: SingleInputProcessingSemiFactory<*>> {
        fun create(
            blockProvider: BlockRegistryObject<*, *>,
            pos: BlockPos,
            state: BlockState,
            baselineMaxOperations: Int
        ): BE
    }
}