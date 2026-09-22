package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.LocationUtil
import dev.engine_room.flywheel.lib.model.baked.PartialModel

object MekCrePartialModels {
    val INFUSER_TOP = block("mechanical_infuser/top")
    val INFUSER_BOTTOM = block("mechanical_infuser/bottom")

    val MAGMA_CUBE_IDLE = block("magma_cube_heater/idle")
    val MAGMA_CUBE_HEATED = block("magma_cube_heater/heated")
    val MAGMA_CUBE_SUPERHEATED = block("magma_cube_heater/superheated")

    private fun block(path: String): PartialModel {
        return PartialModel.of(LocationUtil.rl("block/${path}"))
    }

    fun init() {}
}