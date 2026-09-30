package dev.bluesheep.mekcre.recipe.cached

import com.simibubi.create.content.processing.recipe.ProcessingRecipe
import com.takenokoshi.mekaddonlib.recipe.cached.AbstractCachedRecipe
import dev.bluesheep.mekcre.recipe.output.ProcessingOutputHandler
import mekanism.api.recipes.cache.CachedRecipe
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess
import mekanism.api.recipes.inputs.IInputHandler
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import java.util.function.BooleanSupplier

class SingleInputCachedProcessingRecipe<R: ProcessingRecipe<*, *>>(
    recipe: R,
    recheckAllErrors: BooleanSupplier,
    val inputHandler: IInputHandler<ItemStack>,
    val outputHandler: ProcessingOutputHandler,
    val random: RandomSource
) : AbstractCachedRecipe<R>(recipe, recheckAllErrors) {
    val ingredient: Ingredient = recipe.ingredients.firstOrNull() ?: Ingredient.EMPTY
    val combinedOutput = mutableListOf<ItemStack>()
    private var recipeInput = ItemStack.EMPTY

    init {
        for (output in recipe.rollableResultsAsItemStacks) {
            val existing = combinedOutput.firstOrNull { ItemStack.isSameItemSameComponents(it, output) }
            if (existing != null) {
                existing.grow(output.count)
            } else {
                combinedOutput.add(output.copy())
            }
        }
    }

    override fun calculateOperationsThisTick(tracker: CachedRecipe.OperationTracker) {
        super.calculateOperationsThisTick(tracker)

        if (!tracker.shouldContinueChecking()) return

        recipeInput = inputHandler.getRecipeInput(IngredientCreatorAccess.item().from(ingredient))

        if (recipeInput.isEmpty) {
            tracker.mismatchedRecipe()
            return
        }

        if (!ingredient.test(recipeInput)) {
            tracker.mismatchedRecipe()
            return
        }
        inputHandler.calculateOperationsCanSupport(tracker, recipeInput)

        outputHandler.calculateOperationsCanSupport(tracker, combinedOutput)
    }

    override fun isInputValid(): Boolean {
        return ingredient.test(recipeInput)
    }

    override fun finishProcessing(operations: Int) {
        inputHandler.use(recipeInput, operations)

        repeat(operations) {
            val produced = recipe.rollResults(random)
            outputHandler.handleOutput(produced, 1)
        }
    }
}