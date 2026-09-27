package dev.bluesheep.mekcre.util

import mekanism.common.tier.FactoryTier

object FactoryGuiUtil {
    fun getBaseX(tier: FactoryTier): Int {
        return when (tier) {
            FactoryTier.BASIC -> 55
            FactoryTier.ADVANCED -> 35
            FactoryTier.ELITE -> 29
            else -> 27
        }
    }

    fun getBaseXMult(tier: FactoryTier): Int {
        return when (tier) {
            FactoryTier.BASIC -> 38
            FactoryTier.ADVANCED -> 26
            else -> 19
        }
    }
}