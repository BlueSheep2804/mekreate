package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.LocationUtil
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.recipe.create.InfusingRecipe
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object MekCreRecipeTypes {
    val REGISTRY: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, MekCreMod.MODID)

    val INFUSING: RecipeType<InfusingRecipe> by REGISTRY.register("infusing") { ->
        RecipeType.simple(LocationUtil.rl("infusing"))
    }
}