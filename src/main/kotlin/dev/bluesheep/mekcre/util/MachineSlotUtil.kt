package dev.bluesheep.mekcre.util

import mekanism.api.IContentsListener
import mekanism.common.inventory.slot.OutputInventorySlot
import mekanism.common.tier.FactoryTier

object MachineSlotUtil {
    const val OUTPUT_BASE_X = 108
    const val OUTPUT_BASE_Y = 35

    fun multipleOutputSlot(recipeCacheListener: IContentsListener?, size: Int): Array<OutputInventorySlot> {
        val positions = positions(size)
        return Array(size) {
            val pos = positions[it]
            OutputInventorySlot.at(
                recipeCacheListener,
                pos.first, pos.second
            )
        }
    }

    fun multipleOutputSlotForFactory(recipeCacheListeners: Array<out IContentsListener?>, size: Int, tier: FactoryTier): Array<Array<OutputInventorySlot>> {
        val positions = positionsFactory(size, tier)
        return Array(tier.processes) { i ->
            Array(size) {
                val pos = positions[i][it]
                OutputInventorySlot.at(
                    recipeCacheListeners[i],
                    pos.first, pos.second
                )
            }
        }
    }

    private fun positions(size: Int): Array<Pair<Int, Int>> {
        return Array(size) {
            return@Array when (size) {
                in 1..2 -> OUTPUT_BASE_X + 18 * (it % 2) to OUTPUT_BASE_Y + 18 * (it / 2)
                in 3..4 -> OUTPUT_BASE_X + 18 * (it % 2) to OUTPUT_BASE_Y - 9 + 18 * (it / 2)
                in 5..9 -> OUTPUT_BASE_X + 18 * (it % 3) to OUTPUT_BASE_Y - 18 + 18 * (it / 3)
                in 10..16 -> OUTPUT_BASE_X - 18 + 18 * (it % 4) to OUTPUT_BASE_Y - 18 + 18 * (it / 4)
                else -> OUTPUT_BASE_X to OUTPUT_BASE_Y
            }
        }
    }

    private fun positionsFactory(size: Int, tier: FactoryTier): Array<Array<Pair<Int, Int>>> {
        val baseX = getBaseX(tier)
        val baseXMult = getBaseXMult(tier)
        return Array(tier.processes) { processIndex ->
            Array(size) { index ->
                (baseX + processIndex * baseXMult) to 57 + 18 * index
            }
        }
    }

    fun getBaseX(tier: FactoryTier): Int {
        // Enumを網羅するとelseが無視される(コンパイル時に消える…?)
        return when (tier.ordinal) {
            FactoryTier.BASIC.ordinal -> 55
            FactoryTier.ADVANCED.ordinal -> 35
            FactoryTier.ELITE.ordinal -> 29
            FactoryTier.ULTIMATE.ordinal -> 27
            else -> 9
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
