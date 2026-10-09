package dev.bluesheep.mekcre.screen

import com.takenokoshi.mekaddonlib.inventory.container.MekALDynamicSizedContainer
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingSemiFactory
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError
import mekanism.client.gui.GuiConfigurableTile
import mekanism.client.gui.element.GuiUpArrow
import mekanism.client.gui.element.bar.GuiVerticalPowerBar
import mekanism.client.gui.element.progress.GuiProgress
import mekanism.client.gui.element.progress.ProgressType
import mekanism.client.gui.element.tab.GuiEnergyTab
import mekanism.common.inventory.warning.WarningTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class SingleInputProcessingSemiFactoryScreen<BE: SingleInputProcessingSemiFactory<*>>(
    container: MekALDynamicSizedContainer<BE>,
    inv: Inventory,
    title: Component
) : GuiConfigurableTile<BE, MekALDynamicSizedContainer<BE>>(
    container, inv, title
) {
    init {
        imageHeight += tile.extraHeight
        inventoryLabelY = imageHeight - 92
        dynamicSlots = true
    }

    override fun addGuiElements() {
        super.addGuiElements()
        addRenderableWidget(GuiUpArrow(this, 60-18, 47))
        addRenderableWidget(GuiVerticalPowerBar(this, tile.energyContainer, 164, 16))
            .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(RecipeError.NOT_ENOUGH_ENERGY))
        addRenderableWidget(GuiProgress(tile::getScaledProgress, ProgressType.BAR, this, 78-18, 47))
            .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT))
            .recipeViewerCategories(
                *tile.getRecipeViewerTypes()
            )
        addRenderableWidget(GuiEnergyTab(this, tile.energyContainer, tile::getActive))
    }

    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        renderInventoryText(guiGraphics)
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }
}