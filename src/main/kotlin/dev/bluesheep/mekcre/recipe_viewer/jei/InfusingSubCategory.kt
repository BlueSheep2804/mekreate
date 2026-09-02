package dev.bluesheep.mekcre.recipe_viewer.jei

import com.simibubi.create.compat.jei.category.CreateRecipeCategory
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory
import com.simibubi.create.content.processing.sequenced.SequencedRecipe
import dev.bluesheep.mekcre.recipe.create.InfusingRecipe
import dev.bluesheep.mekcre.recipe_viewer.jei.animations.AnimatedInfuser
import mekanism.client.recipe_viewer.jei.ChemicalStackRenderer
import mekanism.client.recipe_viewer.jei.MekanismJEI
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.recipe.IFocusGroup
import net.minecraft.client.gui.GuiGraphics

class InfusingSubCategory : SequencedAssemblySubCategory(25) {
    val infuser = AnimatedInfuser()

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: SequencedRecipe<*>, focuses: IFocusGroup?, x: Int) {
        if (recipe.asAssemblyRecipe is InfusingRecipe) {
            val chemicalIngredient = (recipe.asAssemblyRecipe as InfusingRecipe).chemicalIngredient
            builder
                .addInputSlot(x + 4, 15)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(
                    MekanismJEI.TYPE_CHEMICAL,
                    chemicalIngredient.representations
                )
                .setCustomRenderer(
                    MekanismJEI.TYPE_CHEMICAL,
                    ChemicalStackRenderer(1000, 16, 16)
                )
        }
    }

    override fun draw(
        recipe: SequencedRecipe<*>,
        graphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double,
        index: Int
    ) {
        val stack = graphics.pose()
        infuser.offset = index
        stack.pushPose()
        stack.translate(-7.0, 50.0, 0.0)
        stack.scale(0.75f, 0.75f, 0.75f)
        infuser.draw(graphics, width / 2, 0)
        stack.popPose()
    }
}