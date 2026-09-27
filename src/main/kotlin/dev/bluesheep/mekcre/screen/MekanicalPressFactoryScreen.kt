package dev.bluesheep.mekcre.screen

import com.takenokoshi.mekaddonlib.gui.tab.GuiMekALFactorySortingTab
import com.takenokoshi.mekaddonlib.inventory.container.MekALDynamicSizedContainer
import dev.bluesheep.mekcre.blockentity.MekanicalPressFactoryBlockEntity
import dev.bluesheep.mekcre.recipe_viewer.MekCreRecipeViewerTypes
import dev.bluesheep.mekcre.util.FactoryGuiUtil
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError
import mekanism.client.gui.GuiConfigurableTile
import mekanism.client.gui.element.bar.GuiVerticalPowerBar
import mekanism.client.gui.element.progress.GuiProgress
import mekanism.client.gui.element.progress.ProgressType
import mekanism.client.gui.element.tab.GuiEnergyTab
import mekanism.common.inventory.warning.WarningTracker
import mekanism.common.tier.FactoryTier
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class MekanicalPressFactoryScreen(
    container: MekALDynamicSizedContainer<MekanicalPressFactoryBlockEntity>,
    inv: Inventory,
    title: Component
) : GuiConfigurableTile<MekanicalPressFactoryBlockEntity, MekALDynamicSizedContainer<MekanicalPressFactoryBlockEntity>>(
    container, inv, title
) {
    init {
        imageWidth += tile.extraWidth
        imageHeight += tile.extraHeight
        inventoryLabelY = 95
        if (tile.tier == FactoryTier.ULTIMATE) {
            inventoryLabelX = 26
        }
        titleLabelY = 4
        dynamicSlots = true
    }

    override fun addGuiElements() {
        super.addGuiElements()
        addRenderableWidget(GuiMekALFactorySortingTab(this, tile))
        addRenderableWidget(GuiVerticalPowerBar(this, tile.energyContainer, imageWidth - 12, 16, 52 + tile.extraHeight))
            .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(RecipeError.NOT_ENOUGH_ENERGY, 0))
        addRenderableWidget(GuiEnergyTab(this, tile.energyContainer, tile::getActive))

        repeat(tile.tier.processes) { i ->
            val baseX = FactoryGuiUtil.getBaseX(tile.tier)
            val baseXMult = FactoryGuiUtil.getBaseXMult(tile.tier)

            addRenderableWidget(GuiProgress(
                fun(): Double { return tile.getScaledProgress(i) },
                ProgressType.DOWN,
                this,
                4 + baseX + (i * baseXMult),
                33
            ))
                .recipeViewerCategories(MekCreRecipeViewerTypes.PRESSING)
        }
    }

    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        renderInventoryText(guiGraphics)
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }
}