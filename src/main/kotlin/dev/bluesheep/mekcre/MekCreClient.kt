package dev.bluesheep.mekcre

import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.registry.MekCrePartialModels
import dev.bluesheep.mekcre.screen.MekanicalPressScreen
import mekanism.client.ClientRegistrationUtil
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@EventBusSubscriber(value = [Dist.CLIENT], modid = MekCreMod.MODID)
object MekCreClient {
    init {
        MekCrePartialModels.init()
    }

    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        ClientRegistrationUtil.registerScreen(
            event,
            MekCreMachines.MEKANICAL_PRESS.container,
            ::MekanicalPressScreen
        )
    }
}