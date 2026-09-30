package dev.bluesheep.mekcre.recipe_viewer

import com.simibubi.create.content.kinetics.millstone.MillingRecipe
import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.takenokoshi.mekaddonlib.recipe_viewer.type.RVMekALRecipeTypeWrapper
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.registry.MekCreMekanismRecipeTypes
import dev.bluesheep.mekcre.LocationUtil

object MekCreRecipeViewerTypes {
    val PRESSING = RVMekALRecipeTypeWrapper(
        LocationUtil.rlCreate("pressing"),
        PressingRecipe::class.java,
        MekCreMekanismRecipeTypes.PRESS,
        0, 0, 20, 20,
        MekCreMachines.MEKANICAL_PRESS
    )
    val MILLING = RVMekALRecipeTypeWrapper(
        LocationUtil.rlCreate("milling"),
        MillingRecipe::class.java,
        MekCreMekanismRecipeTypes.MILLING,
        0, 0, 20, 20,
        MekCreMachines.MEKANICAL_MILL
    )
}