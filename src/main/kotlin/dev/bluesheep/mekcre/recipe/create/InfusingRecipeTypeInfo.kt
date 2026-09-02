package dev.bluesheep.mekcre.recipe.create

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo
import dev.bluesheep.mekcre.LocationUtil
import dev.bluesheep.mekcre.registry.MekCreRecipeSerializers
import dev.bluesheep.mekcre.registry.MekCreRecipeTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType

class InfusingRecipeTypeInfo private constructor(private val type: () -> RecipeType<InfusingRecipe>, private val serializer: () -> RecipeSerializer<InfusingRecipe>) : IRecipeTypeInfo {
    companion object {
        val TYPE_INFO = InfusingRecipeTypeInfo({ MekCreRecipeTypes.INFUSING }, { MekCreRecipeSerializers.INFUSING })
    }

    override fun getId(): ResourceLocation {
        return LocationUtil.rl("infusing")
    }

    override fun <T : RecipeSerializer<*>> getSerializer(): T {
        return serializer.invoke() as T
    }

    override fun <I : RecipeInput, R : Recipe<I>> getType(): RecipeType<R> {
        return type.invoke() as RecipeType<R>
    }
}