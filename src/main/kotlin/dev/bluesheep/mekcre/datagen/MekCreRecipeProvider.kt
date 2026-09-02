package dev.bluesheep.mekcre.datagen

import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder
import dev.bluesheep.mekcre.LocationUtil
import dev.bluesheep.mekcre.recipe.create.InfusingRecipe
import dev.bluesheep.mekcre.registry.MekCreItems
import mekanism.api.chemical.Chemical
import mekanism.api.recipes.ingredients.ChemicalStackIngredient
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess
import mekanism.common.registration.impl.DeferredChemical
import mekanism.common.registries.MekanismChemicals
import mekanism.common.registries.MekanismItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.neoforged.neoforge.common.Tags
import java.util.concurrent.CompletableFuture

class MekCreRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        SequencedAssemblyRecipeBuilder(LocationUtil.rl("sequenced_assembly/refined_obsidian"))
            .require(MekanismItems.REFINED_OBSIDIAN_DUST)
            .transitionTo(MekCreItems.INCOMPLETE_REFINED_OBSIDIAN)
            .addOutput(MekanismItems.REFINED_OBSIDIAN_INGOT, 1f)
            .loops(2)
            .addStep<StandardProcessingRecipe.Builder<InfusingRecipe>>({ recipeId ->
                StandardProcessingRecipe.Builder(
                    { InfusingRecipe(
                        it,
                        chemicalIngredient(MekanismChemicals.OSMIUM, 100)
                    ) },
                    recipeId
                )
            }, { it })
            .addStep(::PressingRecipe) { it }
            .addStep(::PressingRecipe) { it }
            .build(recipeOutput)

        SequencedAssemblyRecipeBuilder(LocationUtil.rl("sequenced_assembly/refined_glowstone"))
            .require(Tags.Items.DUSTS_GLOWSTONE)
            .transitionTo(MekCreItems.INCOMPLETE_REFINED_GLOWSTONE)
            .addOutput(MekanismItems.REFINED_GLOWSTONE_INGOT, 1f)
            .loops(2)
            .addStep<StandardProcessingRecipe.Builder<InfusingRecipe>>({ recipeId ->
                StandardProcessingRecipe.Builder(
                    { InfusingRecipe(
                        it,
                        chemicalIngredient(MekanismChemicals.OSMIUM, 100)
                    ) },
                    recipeId
                )
            }, { it })
            .addStep(::PressingRecipe) { it }
            .addStep(::PressingRecipe) { it }
            .build(recipeOutput)
    }

    private fun chemicalIngredient(chemical: DeferredChemical<Chemical>, amount: Long): ChemicalStackIngredient {
        return ChemicalStackIngredient.of(IngredientCreatorAccess.chemical().of(chemical), amount)
    }
}