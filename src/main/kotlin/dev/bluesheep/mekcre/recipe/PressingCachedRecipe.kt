package dev.bluesheep.mekcre.recipe

import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.takenokoshi.mekaddonlib.recipe.cached.AbstractCachedRecipe
import dev.bluesheep.mekcre.recipe.output.ProcessingOutputHandler
import mekanism.api.recipes.cache.CachedRecipe
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess
import mekanism.api.recipes.inputs.IInputHandler
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import java.util.function.BooleanSupplier

class PressingCachedRecipe(
    recipe: PressingRecipe,
    recheckAllErrors: BooleanSupplier,
    val inputHandler: IInputHandler<ItemStack>,
    val outputHandler: ProcessingOutputHandler,
    val random: RandomSource
) : AbstractCachedRecipe<PressingRecipe>(recipe, recheckAllErrors) {
    val ingredient: Ingredient = recipe.ingredients.firstOrNull() ?: Ingredient.EMPTY
    private var recipeInput = ItemStack.EMPTY

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

        outputHandler.calculateOperationsCanSupport(tracker, recipe.rollableResultsAsItemStacks)
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