package dev.bluesheep.mekcre.datagen

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllItems
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe
import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder
import dev.bluesheep.mekcre.LocationUtil
import dev.bluesheep.mekcre.recipe.create.InfusingRecipe
import dev.bluesheep.mekcre.registry.MekCreBlocks
import dev.bluesheep.mekcre.registry.MekCreItems
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.util.FactoryType
import dev.bluesheep.mekcre.registry.MekCreFactoryType
import dev.lapis256.mekanism_empowered.core.datagen.MekanismDataShapedRecipeBuilder
import fr.iglee42.evolvedmekanism.EvolvedMekanism
import fr.iglee42.evolvedmekanism.registries.EMTags
import mekanism.api.chemical.Chemical
import mekanism.api.recipes.ingredients.ChemicalStackIngredient
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess
import mekanism.common.registration.impl.DeferredChemical
import mekanism.common.registries.MekanismBlocks
import mekanism.common.registries.MekanismChemicals
import mekanism.common.registries.MekanismItems
import mekanism.common.tags.MekanismTags
import mekanism.common.tier.FactoryTier
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import java.util.concurrent.CompletableFuture

class MekCreRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        sequencedRecipes(recipeOutput)
        shapedRecipes(recipeOutput)

        pressingRecipeBuilder(LocationUtil.rl("osmium_plate"))
            .require(LocationUtil.tagItemCommon("ingots/osmium"))
            .output(MekCreItems.OSMIUM_SHEET)
            .build(recipeOutput)

        itemApplicationRecipeBuilder(LocationUtil.rl("osmium_casing_from_logs"))
            .require(Tags.Items.STRIPPED_LOGS)
            .require(LocationUtil.tagItemCommon("ingots/osmium"))
            .output(MekCreBlocks.OSMIUM_CASING)
            .build(recipeOutput)
        itemApplicationRecipeBuilder(LocationUtil.rl("osmium_casing_from_woods"))
            .require(Tags.Items.STRIPPED_WOODS)
            .require(LocationUtil.tagItemCommon("ingots/osmium"))
            .output(MekCreBlocks.OSMIUM_CASING)
            .build(recipeOutput)
    }

    private fun shapedRecipes(recipeOutput: RecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MekCreBlocks.EMPTY_HEATER)
            .pattern(" P ")
            .pattern("PBP")
            .pattern(" P ")
            .define('P', LocationUtil.tagItemCommon("plates/osmium"))
            .define('B', Items.BASALT)
            .unlockedByHasItem(LocationUtil.tagItemCommon("plates/osmium"))
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MekCreItems.KINETIC_CONTROL_CIRCUIT)
            .pattern("ACA")
            .define('A', AllItems.ANDESITE_ALLOY)
            .define('C', MekanismItems.BASIC_CONTROL_CIRCUIT)
            .unlockedByHasItem(MekanismItems.BASIC_CONTROL_CIRCUIT)
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MekCreMachines.MEKANICAL_PRESS)
            .pattern("ASA")
            .pattern("C@C")
            .pattern("AIA")
            .define('A', AllItems.ANDESITE_ALLOY)
            .define('S', AllBlocks.SHAFT)
            .define('C', MekCreItems.KINETIC_CONTROL_CIRCUIT)
            .define('@', MekanismBlocks.STEEL_CASING)
            .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
            .unlockedByHasItem(MekCreItems.KINETIC_CONTROL_CIRCUIT)
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MekCreBlocks.MECHANICAL_INFUSER)
            .pattern("FIF")
            .pattern("SCS")
            .pattern(" O ")
            .define('F', Items.FURNACE)
            .define('I', LocationUtil.tagItemCommon("plates/iron"))
            .define('S', AllBlocks.SHAFT)
            .define('C', MekCreBlocks.OSMIUM_CASING)
            .define('O', LocationUtil.tagItemCommon("plates/osmium"))
            .unlockedByHasItem(LocationUtil.tagItemCommon("plates/osmium"))
            .save(recipeOutput)

        MekCreFactoryType.ALL.forEach { type ->
            type.factories.forEach { (tier, machine) ->
                addFactoryRecipe(recipeOutput, type, machine, tier)
            }
        }
    }

    private fun sequencedRecipes(recipeOutput: RecipeOutput) {
        SequencedAssemblyRecipeBuilder(LocationUtil.rl("refined_obsidian_ingot"))
            .require(MekanismItems.REFINED_OBSIDIAN_DUST)
            .transitionTo(MekCreItems.INCOMPLETE_REFINED_OBSIDIAN_INGOT)
            .addOutput(MekanismItems.REFINED_OBSIDIAN_INGOT, 1f)
            .loops(2)
            .addStepInfusing(chemicalIngredient(MekanismChemicals.OSMIUM, 100))
            .addStep(::PressingRecipe) { it }
            .addStep(::PressingRecipe) { it }
            .build(recipeOutput)

        SequencedAssemblyRecipeBuilder(LocationUtil.rl("refined_glowstone_ingot"))
            .require(Tags.Items.DUSTS_GLOWSTONE)
            .transitionTo(MekCreItems.INCOMPLETE_REFINED_GLOWSTONE_INGOT)
            .addOutput(MekanismItems.REFINED_GLOWSTONE_INGOT, 1f)
            .loops(2)
            .addStepInfusing(chemicalIngredient(MekanismChemicals.OSMIUM, 100))
            .addStep(::PressingRecipe) { it }
            .addStep(::PressingRecipe) { it }
            .build(recipeOutput)

        SequencedAssemblyRecipeBuilder(LocationUtil.rl("contraption_control_circuit"))
            .require(MekCreItems.KINETIC_CONTROL_CIRCUIT)
            .transitionTo(MekCreItems.INCOMPLETE_CONTRAPTION_CONTROL_CIRCUIT)
            .addOutput(MekCreItems.CONTRAPTION_CONTROL_CIRCUIT, 120f)
            .addOutput(MekCreItems.KINETIC_CONTROL_CIRCUIT, 8f)
            .addOutput(AllItems.ANDESITE_ALLOY, 8f)
            .addOutput(AllBlocks.LARGE_COGWHEEL, 5f)
            .addOutput(AllItems.IRON_SHEET, 3f)
            .addOutput(AllItems.ROSE_QUARTZ, 2f)
            .addOutput(MekanismItems.BASIC_CONTROL_CIRCUIT, 2f)
            .addOutput(MekanismItems.REFINED_OBSIDIAN_DUST, 2f)
            .loops(4)
            .addStep(::DeployerApplicationRecipe) { rb -> rb.require(AllBlocks.LARGE_COGWHEEL) }
            .addStep(::DeployerApplicationRecipe) { rb -> rb.require(AllItems.ELECTRON_TUBE) }
            .addStepInfusing(chemicalIngredient(MekanismChemicals.REFINED_OBSIDIAN, 40))
            .build(recipeOutput)
    }

    // helpers
    private fun chemicalIngredient(chemical: DeferredChemical<Chemical>, amount: Long): ChemicalStackIngredient {
        return ChemicalStackIngredient.of(IngredientCreatorAccess.chemical().of(chemical), amount)
    }

    private fun SequencedAssemblyRecipeBuilder.addStepInfusing(ingredient: ChemicalStackIngredient): SequencedAssemblyRecipeBuilder {
        return this.addStep<StandardProcessingRecipe.Builder<InfusingRecipe>>({ recipeId ->
            StandardProcessingRecipe.Builder(
                { InfusingRecipe(it, ingredient) },
                recipeId
            )
        }, { it })
    }

    private fun ShapedRecipeBuilder.unlockedByHasItem(item: ItemLike): ShapedRecipeBuilder {
        val id = BuiltInRegistries.ITEM.getKey(item.asItem())
        return this.unlockedBy("has_${id.toLanguageKey().replace('.', '_')}", has(item))
    }

    private fun ShapedRecipeBuilder.unlockedByHasItem(tag: TagKey<Item>): ShapedRecipeBuilder {
        return this.unlockedBy("has_${tag.location.toLanguageKey().replace(Regex("[./]"), "_")}", has(tag))
    }

    private fun addFactoryRecipe(recipeOutput: RecipeOutput, factoryType: FactoryType<*>, machine: ItemLike, tier: FactoryTier) {
        val builder = MekanismDataShapedRecipeBuilder(machine)
            .pattern("ACA")
            .pattern("IMI")
            .pattern("ACA")
            .define('M', factoryType.getUpgradeBaseMachine(tier))
            .unlockedByHasItem(factoryType.getUpgradeBaseMachine(tier))
        when (tier.ordinal) {
            0 -> {
                builder.define('A', MekanismTags.Items.ALLOYS_BASIC)
                    .define('C', MekanismTags.Items.CIRCUITS_BASIC)
                    .define('I', LocationUtil.tagItemCommon("ingots/iron"))
            }
            1 -> {
                builder.define('A', MekanismTags.Items.ALLOYS_INFUSED)
                    .define('C', MekanismTags.Items.CIRCUITS_ADVANCED)
                    .define('I', LocationUtil.tagItemCommon("ingots/osmium"))
            }
            2 -> {
                builder.define('A', MekanismTags.Items.ALLOYS_REINFORCED)
                    .define('C', MekanismTags.Items.CIRCUITS_ELITE)
                    .define('I', LocationUtil.tagItemCommon("ingots/gold"))
            }
            3 -> {
                builder.define('A', MekanismTags.Items.ALLOYS_ATOMIC)
                    .define('C', MekanismTags.Items.CIRCUITS_ULTIMATE)
                    .define('I', LocationUtil.tagItemCommon("gems/diamond"))
            }
            4 -> {
                builder.define('A', EMTags.Items.ALLOYS_HYPERCHARGED)
                    .define('C', EMTags.Items.CIRCUITS_OVERCLOCKED)
                    .define('I', LocationUtil.tagItemCommon("ingots/uranium"))
            }
            5 -> {
                builder.define('A', EMTags.Items.ALLOYS_SUBATOMIC)
                    .define('C', EMTags.Items.CIRCUITS_QUANTUM)
                    .define('I', LocationUtil.tagItemCommon("ingots/tin"))
            }
            6 -> {
                builder.define('A', EMTags.Items.ALLOYS_SINGULAR)
                    .define('C', EMTags.Items.CIRCUITS_DENSE)
                    .define('I', LocationUtil.tagItemCommon("ingots/bronze"))
            }
            7 -> {
                builder.define('A', EMTags.Items.ALLOYS_EXOVERSAL)
                    .define('C', EMTags.Items.CIRCUITS_MULTIVERSAL)
                    .define('I', LocationUtil.tagItemCommon("ingots/netherite"))
            }
            8 -> {
                builder.define('A', EMTags.Items.ALLOYS_CREATIVE)
                    .define('C', EMTags.Items.CIRCUITS_CREATIVE_FORGE)
                    .define('I', Items.NETHER_STAR)
            }
        }

        val output = if (tier.ordinal > FactoryTier.ULTIMATE.ordinal) {
            recipeOutput.withConditions(ModLoadedCondition(EvolvedMekanism.MODID))
        } else recipeOutput

        builder.save(output, LocationUtil.rl("factory/${tier.name.lowercase()}/${factoryType.name.lowercase()}"))
    }

    private fun itemApplicationRecipeBuilder(recipeId: ResourceLocation): ItemApplicationRecipe.Builder<ItemApplicationRecipe> {
        return ItemApplicationRecipe.Builder(
            { ItemApplicationRecipe(AllRecipeTypes.ITEM_APPLICATION, it)},
            recipeId
        )
    }

    private fun pressingRecipeBuilder(recipeId: ResourceLocation): StandardProcessingRecipe.Builder<PressingRecipe> {
        return StandardProcessingRecipe.Builder(
            { PressingRecipe(it) },
            recipeId
        )
    }
}