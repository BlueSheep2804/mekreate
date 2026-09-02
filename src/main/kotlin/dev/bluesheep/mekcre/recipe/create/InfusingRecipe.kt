package dev.bluesheep.mekcre.recipe.create

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe
import dev.bluesheep.mekcre.recipe_viewer.jei.InfusingSubCategory
import dev.bluesheep.mekcre.registry.MekCreBlocks
import mekanism.api.recipes.ingredients.ChemicalStackIngredient
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput
import net.minecraft.network.chat.Component
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import java.util.function.Supplier

class InfusingRecipe(
    params: ProcessingRecipeParams,
    val chemicalIngredient: ChemicalStackIngredient
) : StandardProcessingRecipe<SingleItemChemicalRecipeInput>(InfusingRecipeTypeInfo.TYPE_INFO, params), IAssemblyRecipe {
    override fun getMaxInputCount(): Int {
        return 1
    }

    override fun getMaxOutputCount(): Int {
        return 1
    }

    override fun matches(
        recipeInput: SingleItemChemicalRecipeInput,
        p1: Level
    ): Boolean {
        if (recipeInput.isEmpty) return false
        return (
            ingredients.first().test(recipeInput.item())
                && chemicalIngredient.test(recipeInput.chemical())
        )
    }

    override fun getDescriptionForAssembly(): Component {
        return Component.translatable("mekcre.recipe.assembly.infuse_chemical", chemicalIngredient.representations.first().textComponent)
    }

    override fun addRequiredMachines(list: MutableSet<ItemLike>) {
        list.add(MekCreBlocks.MECHANICAL_INFUSER)
    }

    override fun addAssemblyIngredients(list: MutableList<Ingredient>) {
        list.add(ingredients[0])
    }

    override fun getJEISubCategory(): Supplier<Supplier<SequencedAssemblySubCategory>> {
        return Supplier { Supplier { InfusingSubCategory() } }
    }
}