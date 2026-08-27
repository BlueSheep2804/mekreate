package dev.bluesheep.mekcre.block.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.bluesheep.mekcre.blockentity.MechanicalInfuserBlockEntity
import dev.bluesheep.mekcre.registry.MekCrePartialModels
import dev.engine_room.flywheel.lib.model.baked.PartialModel
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.util.Mth

class MechanicalInfuserBlockRenderer(context: BlockEntityRendererProvider.Context) : KineticBlockEntityRenderer<MechanicalInfuserBlockEntity>(context) {
    companion object {
        val BITS: Array<PartialModel> = arrayOf(
            MekCrePartialModels.INFUSER_TOP,
            MekCrePartialModels.INFUSER_BOTTOM
        )
    }

    override fun renderSafe(
        be: MechanicalInfuserBlockEntity?,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)

        val squeeze = getSqueeze(be!!, partialTicks)

        ms.pushPose()
        for (bit in BITS) {
            ms.translate(0f, 6 * squeeze / 32f, 0f)
            CachedBuffers.partial(bit, be.blockState)
                .light<SuperByteBuffer>(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()))
        }
        ms.popPose()
    }

    private fun getSqueeze(be: MechanicalInfuserBlockEntity, partialTicks: Float): Float {
        val processingTicks = be.processingTicks
        val processingPT = processingTicks - partialTicks
        val processingSpeed = be.calculateProcessingSpeed()
        val processingRate = processingPT / processingSpeed

        val squeeze: Float
        if (processingTicks < 0) squeeze = 0f
        else if (processingRate > 0.7)  // 1 ~ 0.7
            squeeze = 0f
        else if (processingRate > 0.4)  // 0.7 ~ 0.4
            squeeze = Mth.lerp(Mth.clamp((processingRate - 0.4f) / (0.7f - 0.4f), 0f, 1f), -1f, 0f)
        else if (processingRate > 0.05)  // 0.4 ~ 0.05
            squeeze = -1f
        else  // 0.05 ~ 0
            squeeze = Mth.lerp(processingRate * 20, 0f, -1f)
        return Mth.clamp(squeeze, -1f, 0f)
    }
}
