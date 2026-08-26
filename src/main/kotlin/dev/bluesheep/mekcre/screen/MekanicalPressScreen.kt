package dev.bluesheep.mekcre.screen

import dev.bluesheep.mekcre.blockentity.MekanicalPressBlockEntity
import dev.bluesheep.mekcre.recipe_viewer.MekCreRecipeViewerTypes
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError
import mekanism.client.gui.GuiConfigurableTile
import mekanism.client.gui.element.GuiUpArrow
import mekanism.client.gui.element.bar.GuiVerticalPowerBar
import mekanism.client.gui.element.progress.GuiProgress
import mekanism.client.gui.element.progress.ProgressType
import mekanism.client.gui.element.tab.GuiEnergyTab
import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.inventory.warning.WarningTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class MekanicalPressScreen(
    container: MekanismTileContainer<MekanicalPressBlockEntity>,
    inv: Inventory,
    title: Component
) : GuiConfigurableTile<MekanicalPressBlockEntity, MekanismTileContainer<MekanicalPressBlockEntity>>(
    container, inv, title
) {
    init {
        dynamicSlots = true
    }

    override fun addGuiElements() {
        super.addGuiElements()
        addRenderableWidget(GuiUpArrow(this, 60, 38))
        addRenderableWidget(GuiVerticalPowerBar(this, tile.energyContainer, 164, 16))
            .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(RecipeError.NOT_ENOUGH_ENERGY))
        addRenderableWidget(GuiProgress(tile::getScaledProgress, ProgressType.BAR, this, 78, 38))
            .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT))
            .recipeViewerCategories(
                MekCreRecipeViewerTypes.PRESSING
            )
        addRenderableWidget(GuiEnergyTab(this, tile.energyContainer, tile::getActive))
    }

    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        renderInventoryText(guiGraphics)
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }
}