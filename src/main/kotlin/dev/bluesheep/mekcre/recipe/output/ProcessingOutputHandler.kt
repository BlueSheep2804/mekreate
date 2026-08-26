package dev.bluesheep.mekcre.recipe.output

import dev.bluesheep.mekcre.mixin.OutputHelperAccessor
import mekanism.api.inventory.IInventorySlot
import mekanism.api.recipes.cache.CachedRecipe
import mekanism.api.recipes.outputs.IOutputHandler
import net.minecraft.world.item.ItemStack

class ProcessingOutputHandler(
    val notEnoughSpaceError: CachedRecipe.OperationTracker.RecipeError,
    vararg val slots: IInventorySlot
) : IOutputHandler<List<ItemStack>> {
    override fun handleOutput(toOutput: List<ItemStack>, operations: Int) {
        toOutput.forEachIndexed { index, stack ->
            OutputHelperAccessor.handleOutput(slots[index], stack, operations)
        }
    }

    override fun calculateOperationsCanSupport(
        tracker: CachedRecipe.OperationTracker,
        toOutput: List<ItemStack>
    ) {
        if (toOutput.isEmpty()) return
        toOutput.forEachIndexed { index, stack ->
            OutputHelperAccessor.calculateOperationsCanSupport(tracker, notEnoughSpaceError, slots[index], stack)
        }
    }
}