package dev.bluesheep.mekcre.recipe_viewer

import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.takenokoshi.mekaddonlib.recipe_viewer.type.RVMekALRecipeTypeWrapper
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.registry.MekCreRecipeTypes

object MekCreRecipeViewerTypes {
    val PRESSING = RVMekALRecipeTypeWrapper(
        MekCreMod.rlCreate("pressing"),
        PressingRecipe::class.java,
        MekCreRecipeTypes.PRESS,
        0, 0, 20, 20,
        MekCreMachines.MEKANICAL_PRESS
    )
}