package dev.bluesheep.mekcre.util

import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.util.TierExtensions.prev
import mekanism.common.registration.impl.BlockRegistryObject
import mekanism.common.tier.FactoryTier

enum class MekCreFactoryType(
    val baseMachine: () -> BlockRegistryObject<*, *>
) {
    PRESSING({ MekCreMachines.MEKANICAL_PRESS.block }),
    MILLING({ MekCreMachines.MEKANICAL_MILL.block });

    val factories
        get() = MekCreMachines.FACTORIES[this]!!

    fun getUpgradeBaseMachine(tier: FactoryTier): BlockRegistryObject<*, *> {
        return if (tier == FactoryTier.BASIC) {
            baseMachine.invoke()
        } else {
            factories[tier.prev()]!!.block
        }
    }
}