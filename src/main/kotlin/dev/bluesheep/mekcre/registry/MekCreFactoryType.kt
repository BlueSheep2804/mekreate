package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.BlockEntityConstructor
import com.takenokoshi.mekaddonlib.registration.GuiSizedMachineRegistryObject
import com.takenokoshi.mekaddonlib.registration.MachineDeferredRegister
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingFactory
import dev.bluesheep.mekcre.blockentity.factory.MillingFactoryBlockEntity
import dev.bluesheep.mekcre.blockentity.factory.PressingFactoryBlockEntity
import dev.bluesheep.mekcre.mixin.BlockTypeAccessor
import dev.bluesheep.mekcre.mixin.BlockTypeBuilderAccessor
import dev.bluesheep.mekcre.registry.MekCreMachines.REGISTRY
import dev.bluesheep.mekcre.screen.SingleInputProcessingFactoryScreen
import dev.bluesheep.mekcre.util.FactoryType
import dev.bluesheep.mekcre.util.TierExtensions.next
import dev.bluesheep.mekcre.util.TierExtensions.prev
import mekanism.api.math.MathUtils
import mekanism.client.ClientRegistrationUtil
import mekanism.common.MekanismLang
import mekanism.common.attachments.component.AttachedSideConfig
import mekanism.common.block.attribute.AttributeTier
import mekanism.common.block.attribute.AttributeUpgradeable
import mekanism.common.block.prefab.BlockTile
import mekanism.common.config.MekanismConfig
import mekanism.common.content.blocktype.Machine
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.registration.impl.BlockRegistryObject
import mekanism.common.registration.impl.ItemRegistryObject
import mekanism.common.tier.FactoryTier
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

data class MekCreFactoryType<BE: SingleInputProcessingFactory<*>>(
    override val name: String,
    val baseMachine: () -> BlockRegistryObject<*, *>,
    val holder: (FactoryTier) -> ((ItemRegistryObject<*>) -> Unit),
    val beConstructor: BlockEntityConstructor<BE, Machine<BE>, BlockTile.BlockTileModel<BE, Machine<BE>>>,
    val beClass: Class<BE>
): FactoryType<GuiSizedMachineRegistryObject<BE>> {
    companion object {
        val PRESSING = MekCreFactoryType(
            "pressing",
            { MekCreMachines.MEKANICAL_PRESS.block },
            PressingFactoryBlockEntity::addContainersToItem,
            ::PressingFactoryBlockEntity,
            PressingFactoryBlockEntity::class.java
        )
        val MILLING = MekCreFactoryType(
            "milling",
            { MekCreMachines.MEKANICAL_MILL.block },
            MillingFactoryBlockEntity::addContainersToItem,
            ::MillingFactoryBlockEntity,
            MillingFactoryBlockEntity::class.java
        )

        val ALL = listOf(PRESSING, MILLING)
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
                REGISTRY.registerGuiSized(
                    "${tier.name.lowercase()}_${name.lowercase()}_factory",
                    AttachedSideConfig.ELECTRIC_MACHINE,
                    holder.invoke(tier),
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
                ::SingleInputProcessingFactoryScreen
            )
        }
    }
}