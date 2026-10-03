package dev.bluesheep.mekcre.util

import mekanism.common.tier.FactoryTier

object TierExtensions {
    fun FactoryTier.next(): FactoryTier {
        return FactoryTier.entries[(this.ordinal + 1) % FactoryTier.entries.size]
    }

    fun FactoryTier.prev(): FactoryTier {
        return FactoryTier.entries[(this.ordinal - 1) % FactoryTier.entries.size]
    }
}
