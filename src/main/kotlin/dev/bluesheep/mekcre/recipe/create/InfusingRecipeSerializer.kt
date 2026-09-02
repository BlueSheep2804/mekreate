package dev.bluesheep.mekcre.recipe.create

import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams
import mekanism.api.recipes.ingredients.ChemicalStackIngredient
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.crafting.RecipeSerializer

class InfusingRecipeSerializer : RecipeSerializer<InfusingRecipe> {
    val recipeCodec: MapCodec<InfusingRecipe> = RecordCodecBuilder.mapCodec<InfusingRecipe> { instance ->
        instance.group(
            ProcessingRecipeParams.CODEC.forGetter { recipe ->
                recipe.params
            },
            ChemicalStackIngredient.CODEC
                .fieldOf("chemical")
                .forGetter { recipe ->
                    recipe.chemicalIngredient
                }
        ).apply(instance, ::InfusingRecipe)
    }.validate { recipe ->
        val errors = recipe.validate()

        if (errors.isEmpty()) {
            DataResult.success(recipe)
        } else {
            DataResult.error(
                { errors.joinToString("\n") },
                recipe
            )
        }
    }

    val recipeStreamCodec: StreamCodec<RegistryFriendlyByteBuf, InfusingRecipe> = StreamCodec.composite(
        ProcessingRecipeParams.STREAM_CODEC,
        { recipe -> recipe.params },
        ChemicalStackIngredient.STREAM_CODEC,
        { recipe -> recipe.chemicalIngredient },
        ::InfusingRecipe
    )

    override fun codec(): MapCodec<InfusingRecipe> {
        return recipeCodec
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, InfusingRecipe> {
        return recipeStreamCodec
    }
}
