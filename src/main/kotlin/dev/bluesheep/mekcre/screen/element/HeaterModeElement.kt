package dev.bluesheep.mekcre.screen.element

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel
import dev.bluesheep.mekcre.LocationUtil
import mekanism.client.gui.IGuiWrapper
import mekanism.client.gui.element.GuiTexturedElement
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class HeaterModeElement(gui: IGuiWrapper, x: Int, y: Int, val heatLevelSupplier: () -> HeatLevel) : GuiTexturedElement(
    IDLE,
    gui,
    x+1,
    y+1,
    16,
    16
) {
    companion object {
        val IDLE = LocationUtil.rl("textures/gui/element/heater_idle.png")
        val HEATED = LocationUtil.rl("textures/gui/element/heater_heated.png")
        val SUPERHEATED = LocationUtil.rl("textures/gui/element/heater_superheated.png")
    }

    override fun drawBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks)
        guiGraphics.blit(getResource(), relativeX, relativeY, 0f, 0f, 16, 16, 16, 16)
    }

    override fun updateTooltip(mouseX: Int, mouseY: Int) {
        tooltip = Tooltip.create(getHeatLevelComponent())
    }

    override fun getResource(): ResourceLocation {
        return when (heatLevelSupplier.invoke()) {
            HeatLevel.SEETHING -> SUPERHEATED
            HeatLevel.FADING, HeatLevel.KINDLED -> HEATED
            else -> IDLE
        }
    }

    private fun getHeatLevelComponent(): Component {
        return Component.translatable(
            "mekcre.gui.current_heat_level",
            Component.translatable("mekcre.gui." + when (heatLevelSupplier.invoke()) {
                HeatLevel.SEETHING -> "superheated"
                HeatLevel.FADING, HeatLevel.KINDLED -> "heated"
                else -> "idle"
            })
        )
    }
}