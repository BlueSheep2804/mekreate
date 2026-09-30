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
        if (toOutput.isEmpty()) return
        val emptySlots = slots.filter { it.isEmpty }
        val emptySlotNeeded = mutableListOf<Int>()
        toOutput.forEachIndexed { index, stack ->
            val stackableSlots = slots.filter { ItemStack.isSameItemSameComponents(it.stack, stack) }
            if (stackableSlots.isNotEmpty()) {
                var remaining = stack
                for (slot in stackableSlots) {
                    remaining = slot.insertItem(remaining, Action.SIMULATE, AutomationType.INTERNAL)
                    if (remaining.isEmpty) break
                }
                if (!remaining.isEmpty) {
                    emptySlotNeeded.add(index)
                }
            } else {
                emptySlotNeeded.add(index)
            }
        }

        if (emptySlotNeeded.isNotEmpty() && emptySlots.size < emptySlotNeeded.size) {
            tracker.addError(notEnoughSpaceError)
            tracker.updateOperations(0)
        } else {
            tracker.updateOperations(1)
        }
    }
}
