package dev.bluesheep.mekcre.registry

import com.takenokoshi.mekaddonlib.registration.MekALRecipeTypeDeferredRegister
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.recipe.type.WrappedMillingRecipeType
import dev.bluesheep.mekcre.recipe.type.WrappedPressingRecipeType

object MekCreMekanismRecipeTypes {
    val REGISTRY = MekALRecipeTypeDeferredRegister(MekCreMod.MODID)

    val PRESS = WrappedPressingRecipeType()
    val MILLING = WrappedMillingRecipeType()
}