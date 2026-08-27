package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.MekCreMod
import dev.engine_room.flywheel.lib.model.baked.PartialModel

object MekCrePartialModels {
    val INFUSER_TOP = block("mechanical_infuser/top")
    val INFUSER_BOTTOM = block("mechanical_infuser/bottom")

    private fun block(path: String): PartialModel {
        return PartialModel.of(MekCreMod.rl("block/${path}"))
    }

    fun init() {}
}