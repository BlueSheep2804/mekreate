package dev.bluesheep.mekcre.block.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import dev.bluesheep.mekcre.blockentity.MagmaCubeHeaterBlockEntity
import dev.bluesheep.mekcre.registry.MekCrePartialModels
import dev.engine_room.flywheel.lib.model.baked.PartialModel
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.createmod.catnip.math.AngleHelper
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction

class MagmaCubeHeaterBlockRenderer(context: BlockEntityRendererProvider.Context) : SafeBlockEntityRenderer<MagmaCubeHeaterBlockEntity>() {
    override fun renderSafe(
        be: MagmaCubeHeaterBlockEntity?,
        partialTicks: Float,
        ms: PoseStack,
        bufferSource: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        if (be == null) return
        val magmaCubeModel = getMagmaCubeModel(be.currentHeatLevel)
        val horizontalAngle = AngleHelper.rad(be.headAngle.getValue(partialTicks).toDouble())

        CachedBuffers.partial(magmaCubeModel, be.blockState)
            .rotateCentered(horizontalAngle, Direction.UP)
            .light<SuperByteBuffer?>(LightTexture.FULL_BRIGHT)
            .renderInto(ms, bufferSource.getBuffer(RenderType.solid()))
    }

    private fun getMagmaCubeModel(heatLevel: BlazeBurnerBlock.HeatLevel): PartialModel {
        return when (heatLevel) {
            BlazeBurnerBlock.HeatLevel.SEETHING -> MekCrePartialModels.MAGMA_CUBE_SUPERHEATED
            BlazeBurnerBlock.HeatLevel.KINDLED, BlazeBurnerBlock.HeatLevel.FADING -> MekCrePartialModels.MAGMA_CUBE_HEATED
            else -> MekCrePartialModels.MAGMA_CUBE_IDLE
        }
    }
}
