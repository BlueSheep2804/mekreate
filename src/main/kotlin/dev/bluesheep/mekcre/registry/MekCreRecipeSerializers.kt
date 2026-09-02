package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.recipe.create.InfusingRecipe
import dev.bluesheep.mekcre.recipe.create.InfusingRecipeSerializer
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object MekCreRecipeSerializers {
    val REGISTRY: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MekCreMod.MODID)

    val INFUSING: RecipeSerializer<InfusingRecipe> by REGISTRY.register("infusing") { ->
        InfusingRecipeSerializer()
    }
}