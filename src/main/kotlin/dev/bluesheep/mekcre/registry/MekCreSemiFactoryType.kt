package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.GuiSizedMachineRegistryObject
import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import dev.bluesheep.mekcre.block.attribute.AttributeNoOcclusion
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingSemiFactory
import dev.bluesheep.mekcre.blockentity.semifactory.CrusherSemiFactoryBlockEntity
import dev.bluesheep.mekcre.mixin.BlockTypeAccessor
import dev.bluesheep.mekcre.mixin.BlockTypeBuilderAccessor
import dev.bluesheep.mekcre.screen.SingleInputProcessingSemiFactoryScreen
import dev.bluesheep.mekcre.util.FactoryType
import dev.bluesheep.mekcre.util.TierExtensions.next
import dev.bluesheep.mekcre.util.TierExtensions.prev
import mekanism.api.math.MathUtils
import mekanism.client.ClientRegistrationUtil
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
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

data class MekCreSemiFactoryType<BE: SingleInputProcessingSemiFactory<*>>(
    override val name: String,
    val baseMachine: () -> BlockRegistryObject<*, *>,
    val beConstructor: SemiFactoryConstructor<BE>,
    val beClass: Class<BE>
): FactoryType<GuiSizedMachineRegistryObject<BE>> {
    companion object {
        val CRUSHING = MekCreSemiFactoryType(
            "crushing",
            { MekCreMachines.MEKANICAL_CRUSHER.block },
            ::CrusherSemiFactoryBlockEntity,
            CrusherSemiFactoryBlockEntity::class.java
        )

        val ALL = listOf(CRUSHING)
    }

    private val registeredFactories = mutableMapOf<FactoryTier, GuiSizedMachineRegistryObject<BE>>()

    override val factories: Map<FactoryTier, GuiSizedMachineRegistryObject<BE>>
        get() = registeredFactories

    override fun getUpgradeBaseMachine(tier: FactoryTier): BlockRegistryObject<*, *> {
        return if (tier == FactoryTier.BASIC) {
            baseMachine.invoke()
        } else {
            factories[tier.prev()]!!.block
        }
    }

    override fun register(registry: MachineDeferredRegister) {
        registeredFactories.putAll(
            FactoryTier.entries.associateWith { tier ->
                registry.registerGuiSized(
                    "${tier.name.lowercase()}_${name}_semi_factory",
                    AttachedSideConfig.ELECTRIC_MACHINE,
                    SingleInputProcessingSemiFactory.Companion::addContainersToItem,
                    { blockProvider, pos, state -> beConstructor.create(blockProvider, pos, state, tier.processes) },
                    beClass
                ) { builder ->
                    builder
                        .with(
                            AttributeTier(tier),
                            AttributeNoOcclusion
                        )
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
        )
    }

    override fun registerScreen(event: RegisterMenuScreensEvent) {
        registeredFactories.forEach { (tier, machine) ->
            ClientRegistrationUtil.registerScreen(
                event,
                machine.container,
                ::SingleInputProcessingSemiFactoryScreen
            )
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