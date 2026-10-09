package dev.bluesheep.mekcre.recipe.output

import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.inventory.IInventorySlot
import mekanism.api.recipes.cache.CachedRecipe
import mekanism.api.recipes.outputs.IOutputHandler
import net.minecraft.world.item.ItemStack

class ProcessingOutputHandler(
    val notEnoughSpaceError: CachedRecipe.OperationTracker.RecipeError,
    vararg val slots: IInventorySlot
) : IOutputHandler<List<ItemStack>> {
    override fun handleOutput(toOutput: List<ItemStack>, operations: Int) {
//        toOutput.forEachIndexed { index, stack ->
//            OutputHelperAccessor.handleOutput(slots[index], stack, operations)
//        }

        toOutput.forEach { stack ->
            val stackableSlots = slots.filter { ItemStack.isSameItemSameComponents(it.stack, stack) }
            var remaining = stack.copy()
            if (operations > 1) {
                remaining.count *= operations
            }
            for (slot in stackableSlots) {
                remaining = slot.insertItem(remaining, Action.EXECUTE, AutomationType.INTERNAL)
                if (remaining.isEmpty) break
            }
            if (!remaining.isEmpty) {
                for (slot in slots.filter { it.isEmpty }) {
                    remaining = slot.insertItem(remaining, Action.EXECUTE, AutomationType.INTERNAL)
                    if (remaining.isEmpty) break
                }
            }
        }
    }

    override fun calculateOperationsCanSupport(
        tracker: CachedRecipe.OperationTracker,
        toOutput: List<ItemStack>
    ) {
        val outputs = toOutput.filterNot { it.isEmpty }
        if (outputs.isEmpty()) return

        // Cache insertion limits without changing the real inventory.
        val capacities = outputs.map { stack ->
            IntArray(slots.size) { index ->
                val probe = stack.copyWithCount(Int.MAX_VALUE)
                Int.MAX_VALUE - slots[index].insertItem(probe, Action.SIMULATE, AutomationType.INTERNAL).count
            }
        }

        fun canFit(operations: Int): Boolean {
            val contents = slots.map { it.stack }.toMutableList()
            val reserved = IntArray(slots.size)
            outputs.forEachIndexed { outputIndex, stack ->
                var remaining = stack.count.toLong() * operations
                // Match handleOutput: fill matching stacks before claiming empty slots.
                for (empty in listOf(false, true)) {
                    for (index in slots.indices) {
                        val current = contents[index]
                        if (empty) {
                            if (!current.isEmpty) continue
                        } else if (current.isEmpty || !ItemStack.isSameItemSameComponents(current, stack)) {
                            continue
                        }
                        val available = (capacities[outputIndex][index] - reserved[index]).coerceAtLeast(0)
                        val inserted = minOf(remaining, available.toLong()).toInt()
                        if (inserted > 0) {
                            contents[index] = stack
                            reserved[index] += inserted
                            remaining -= inserted
                        }
                        if (remaining == 0L) break
                    }
                    if (remaining == 0L) break
                }
                if (remaining > 0) return false
            }
            return true
        }

        // OperationTracker has no maximum getter, so calculate the output limit independently.
        var supported = 0
        var upper = outputs.indices.minOf { index ->
            (capacities[index].sumOf { it.toLong() } / outputs[index].count)
                .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        }
        while (supported < upper) {
            val candidate = supported + ((upper.toLong() - supported + 1) / 2).toInt()
            if (canFit(candidate)) {
                supported = candidate
            } else {
                upper = candidate - 1
            }
        }
        tracker.updateOperations(supported)
        if (supported == 0) {
            tracker.addError(notEnoughSpaceError)
        }
    }
}
