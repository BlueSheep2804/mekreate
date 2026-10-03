package dev.bluesheep.mekcre.screen

import com.takenokoshi.mekaddonlib.gui.tab.GuiMekALFactorySortingTab
import com.takenokoshi.mekaddonlib.inventory.container.MekALDynamicSizedContainer
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingFactory
import dev.bluesheep.mekcre.util.MachineSlotUtil
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError
import mekanism.client.gui.GuiConfigurableTile
import mekanism.client.gui.element.bar.GuiVerticalPowerBar
import mekanism.client.gui.element.progress.GuiProgress
import mekanism.client.gui.element.progress.ProgressType
import mekanism.client.gui.element.tab.GuiEnergyTab
import mekanism.common.inventory.warning.WarningTracker
import mekanism.common.tier.FactoryTier
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class SingleInputProcessingFactoryScreen<BE: SingleInputProcessingFactory<*>>(
    container: MekALDynamicSizedContainer<BE>,
    inv: Inventory,
    title: Component
) : GuiConfigurableTile<BE, MekALDynamicSizedContainer<BE>>(
    container, inv, title
) {
    init {
        imageWidth += tile.extraWidth
        imageHeight += tile.extraHeight
        inventoryLabelY = imageHeight - 92
        if (tile.tier == FactoryTier.ULTIMATE) {
            inventoryLabelX = 26
        } else if (tile.tier.ordinal > FactoryTier.ULTIMATE.ordinal) {
            inventoryLabelX = imageWidth / 2 - Minecraft.getInstance().font.width(playerInventoryTitle) / 2
        }
        titleLabelY = 4
        dynamicSlots = true
    }

    override fun addGuiElements() {
        super.addGuiElements()
        addRenderableWidget(GuiMekALFactorySortingTab(this, tile))
        addRenderableWidget(if (tile.tier.ordinal > FactoryTier.ULTIMATE.ordinal) {
            GuiVerticalPowerBar(this, tile.energyContainer, tile.energySlot.guiX + 5, imageHeight - 52 - 4 - 18 - 9, 52)
        } else {
            GuiVerticalPowerBar(this, tile.energyContainer, imageWidth - 12, 16, 52 + tile.extraHeight)
        })
            .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(RecipeError.NOT_ENOUGH_ENERGY, 0))
        addRenderableWidget(GuiEnergyTab(this, tile.energyContainer, tile::getActive))

        repeat(tile.tier.processes) { i ->
            val baseX = MachineSlotUtil.getBaseX(tile.tier)
            val baseXMult = MachineSlotUtil.getBaseXMult(tile.tier)

            addRenderableWidget(
                GuiProgress(
                    fun(): Double { return tile.getScaledProgress(i) },
                    ProgressType.DOWN,
                    this,
                    4 + baseX + (i * baseXMult),
                    33
                )
            )
                .recipeViewerCategories(*tile.getRecipeViewerTypes())
        }
    }

    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        renderInventoryText(guiGraphics)
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }
}