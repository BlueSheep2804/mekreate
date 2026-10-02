package dev.bluesheep.mekcre.recipe.type

import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe
import com.takenokoshi.mekaddonlib.recipe.inputcache.MekALSingleInputRecipeCache
import com.takenokoshi.mekaddonlib.recipe.type.MekALRecipeType
import dev.bluesheep.mekcre.LocationUtil
import net.minecraft.core.RegistryAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeManager

class WrappedCrushingRecipeType : MekALRecipeType<
        RecipeInput,
        CrushingRecipe,
        MekALSingleInputRecipeCache.MekALSingleItem<CrushingRecipe>
        >(
    LocationUtil.rl("wrapped_create_crushing"),
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
    ): List<RecipeHolder<CrushingRecipe?>?> {
        return recipeManager.getAllRecipesFor(
            AllRecipeTypes.CRUSHING.getType()
        )
    }
}
