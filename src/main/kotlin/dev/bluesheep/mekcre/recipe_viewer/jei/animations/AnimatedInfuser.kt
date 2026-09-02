package dev.bluesheep.mekcre.recipe_viewer.jei.animations

import com.mojang.math.Axis
import com.simibubi.create.AllBlocks
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics
import dev.bluesheep.mekcre.registry.MekCreBlocks
import dev.bluesheep.mekcre.registry.MekCrePartialModels
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.Direction

class AnimatedInfuser : AnimatedKinetics() {
    companion object {
        const val SCALE = 20.0
    }

    override fun draw(guiGraphics: GuiGraphics, xOffset: Int, yOffset: Int) {
        val stack = guiGraphics.pose()
        stack.pushPose()
        stack.translate(xOffset.toDouble(), yOffset.toDouble(), 100.0)
        stack.mulPose(Axis.XP.rotationDegrees(-15.5f))
        stack.mulPose(Axis.YP.rotationDegrees(22.5f))

        blockElement(shaft(Direction.Axis.Z))
            .rotateBlock(0.0, 0.0, getCurrentAngle().toDouble())
            .scale(SCALE)
            .render(guiGraphics)

        blockElement(MekCreBlocks.MECHANICAL_INFUSER.defaultState)
            .scale(SCALE)
            .render(guiGraphics)

        stack.pushPose()
        stack.translate(0.0, 6.0 * 1 / 32.0, 0.0)
        blockElement(MekCrePartialModels.INFUSER_TOP)
            .scale(SCALE)
            .render(guiGraphics)

        blockElement(MekCrePartialModels.INFUSER_BOTTOM)
            .scale(SCALE)
            .render(guiGraphics)
        stack.popPose()

        blockElement(AllBlocks.DEPOT.defaultState)
            .atLocal(0.0, 2.0, 0.0)
            .scale(SCALE)
            .render(guiGraphics)

        stack.popPose()
    }
}