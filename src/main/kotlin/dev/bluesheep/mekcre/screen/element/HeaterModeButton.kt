package dev.bluesheep.mekcre.screen.element

import com.simibubi.create.content.processing.recipe.HeatCondition
import dev.bluesheep.mekcre.LocationUtil
import mekanism.client.gui.IGuiWrapper
import mekanism.client.gui.element.button.MekanismImageButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class HeaterModeButton(
    gui: IGuiWrapper,
    x: Int,
    y: Int,
    onPress: () -> Unit,
    val heatLevelSupplier: () -> HeatCondition
) : MekanismImageButton(
    gui,
    x,
    y,
    18,
    16,
    HEATED,
    { _, _, _ ->
        onPress()
        true
    }
) {
    companion object {
        val NONE = LocationUtil.rlMinecraft("textures/item/gunpowder.png")
        val HEATED = LocationUtil.rlMinecraft("textures/item/coal.png")
        val SUPERHEATED = LocationUtil.rlCreate("textures/item/blaze_cake.png")
    }

    override fun getResource(): ResourceLocation {
        return when (heatLevelSupplier.invoke()) {
            HeatCondition.HEATED -> HEATED
            HeatCondition.SUPERHEATED -> SUPERHEATED
            else -> NONE
        }
    }

    override fun updateTooltip(mouseX: Int, mouseY: Int) {
        tooltip = Tooltip.create(getHeatLevelComponent())
    }

    private fun getHeatLevelComponent(): Component {
        return Component.translatable(
            "mekcre.gui.configured_heat_level",
            Component.translatable("mekcre.gui." + when (heatLevelSupplier.invoke()) {
                HeatCondition.HEATED -> "heated"
                HeatCondition.SUPERHEATED -> "superheated"
                else -> "idle"
            })
        )
    }
}
