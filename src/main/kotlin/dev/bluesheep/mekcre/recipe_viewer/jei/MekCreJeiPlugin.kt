package dev.bluesheep.mekcre.recipe_viewer.jei

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.registry.MekCreMachines
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeCatalystRegistration
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class MekCreJeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("mekcre", "jei")
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.jeiHelpers.getRecipeType(MekCreMod.rlCreate("pressing"))
            .ifPresent {
                registration.addRecipeCatalyst(MekCreMachines.MEKANICAL_PRESS, it)
            }
    }
}