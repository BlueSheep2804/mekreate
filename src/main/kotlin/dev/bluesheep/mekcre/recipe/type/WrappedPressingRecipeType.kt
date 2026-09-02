package dev.bluesheep.mekcre.recipe.type

import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.takenokoshi.mekaddonlib.recipe.inputcache.MekALSingleInputRecipeCache
import com.takenokoshi.mekaddonlib.recipe.type.MekALRecipeType
import dev.bluesheep.mekcre.LocationUtil
import net.minecraft.core.RegistryAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.SingleRecipeInput

class WrappedPressingRecipeType : MekALRecipeType<
        SingleRecipeInput,
        PressingRecipe,
        MekALSingleInputRecipeCache.MekALSingleItem<PressingRecipe>
        >(
    LocationUtil.rl("wrapped_create_pressing"),
    { type ->
        MekALSingleInputRecipeCache.MekALSingleItem(type) { recipe ->
            recipe.ingredients
                .firstOrNull()
                ?.items
                ?.map(ItemStack::getItem)
                ?: emptyList()
        }
    }
) {
    override fun getRecipesUncached(
        recipeManager: RecipeManager,
        registryAccess: RegistryAccess?
    ): List<RecipeHolder<PressingRecipe?>?> {
        return recipeManager.getAllRecipesFor(
            AllRecipeTypes.PRESSING.getType()
        )
    }
}