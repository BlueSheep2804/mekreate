package dev.bluesheep.mekcre.screen

import dev.bluesheep.mekcre.blockentity.MagmaCubeHeaterBlockEntity
import dev.bluesheep.mekcre.screen.element.HeaterModeButton
import dev.bluesheep.mekcre.screen.element.HeaterModeElement
import net.neoforged.neoforge.network.PacketDistributor
import mekanism.client.gui.GuiMekanismTile
import mekanism.client.gui.element.tab.GuiHeatTab
import mekanism.client.gui.element.tab.GuiWarningTab
import mekanism.common.MekanismLang
import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.inventory.warning.IWarningTracker
import mekanism.common.network.to_server.PacketGuiInteract
import mekanism.common.util.MekanismUtils
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class MagmaCubeHeaterScreen(
    container: MekanismTileContainer<MagmaCubeHeaterBlockEntity>,
    inv: Inventory,
    title: Component
) : GuiMekanismTile<MagmaCubeHeaterBlockEntity, MekanismTileContainer<MagmaCubeHeaterBlockEntity>>(
    container, inv, title
) {
    init {
        dynamicSlots = true
    }

    override fun addGuiElements() {
        super.addGuiElements()
        addRenderableWidget(HeaterModeButton(this, 34, 38, {
            PacketDistributor.sendToServer(
                PacketGuiInteract(PacketGuiInteract.GuiInteraction.NEXT_MODE, tile)
            )
        }) { tile.heatCondition })
        addRenderableWidget(HeaterModeElement(this, 79, 38) { tile.currentHeatLevel })
        addRenderableWidget(GuiHeatTab(this) {
            val temp = MekanismUtils.getTemperatureDisplay(tile.totalTemperature, TemperatureUnit.KELVIN, true)
            val transfer = MekanismUtils.getTemperatureDisplay(tile.lastTransferLoss, TemperatureUnit.KELVIN, false)
            val environment = MekanismUtils.getTemperatureDisplay(tile.lastEnvironmentLoss, TemperatureUnit.KELVIN, false)
            return@GuiHeatTab listOf(
                MekanismLang.TEMPERATURE.translate(temp),
                MekanismLang.TRANSFERRED_RATE.translate(transfer),
                MekanismLang.DISSIPATED_RATE.translate(environment)
            )
        })
    }

    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        renderInventoryText(guiGraphics)
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }

    override fun addWarningTab(warningTracker: IWarningTracker?) {
        addRenderableWidget(GuiWarningTab(this, warningTracker, false))
    }
}
