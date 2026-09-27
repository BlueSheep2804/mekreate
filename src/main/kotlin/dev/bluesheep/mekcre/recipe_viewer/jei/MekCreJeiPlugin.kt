package dev.bluesheep.mekcre.recipe_viewer.jei

import com.simibubi.create.compat.jei.ConversionRecipe
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.LocationUtil
import dev.bluesheep.mekcre.registry.MekCreBlocks
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

@JeiPlugin
class MekCreJeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("mekcre", "jei")
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        MysteriousItemConversionCategory.RECIPES.add(
            ConversionRecipe.create(
                MekCreBlocks.EMPTY_HEATER.asStack(),
                ItemStack(MekCreMachines.MAGMA_CUBE_HEATER.block)
            )
        )
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.jeiHelpers.getRecipeType(LocationUtil.rlCreate("pressing"))
            .ifPresent {
                registration.addRecipeCatalyst(MekCreMachines.MEKANICAL_PRESS, it)
                registration.addRecipeCatalysts(it, *MekCreMachines.MEKANICAL_PRESS_FACTORIES.entries.map { it.value }.toTypedArray())
            }
    }
}