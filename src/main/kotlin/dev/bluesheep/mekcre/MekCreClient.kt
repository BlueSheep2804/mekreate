package dev.bluesheep.mekcre

import dev.bluesheep.mekcre.block.renderer.MagmaCubeHeaterBlockRenderer
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.registry.MekCrePartialModels
import dev.bluesheep.mekcre.screen.MagmaCubeHeaterScreen
import dev.bluesheep.mekcre.screen.SingleInputProcessingMachineScreen
import dev.bluesheep.mekcre.registry.MekCreFactoryType
import dev.bluesheep.mekcre.registry.MekCreSemiFactoryType
import mekanism.client.ClientRegistrationUtil
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@EventBusSubscriber(value = [Dist.CLIENT], modid = MekCreMod.MODID)
object MekCreClient {
    init {
        MekCrePartialModels.init()
    }

    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        MekCreFactoryType.ALL.forEach { factoryType ->
            factoryType.registerScreen(event)
        }
        MekCreSemiFactoryType.ALL.forEach { factoryType ->
            factoryType.registerScreen(event)
        }

        ClientRegistrationUtil.registerScreen(
            event,
            MekCreMachines.MEKANICAL_PRESS.container,
            ::SingleInputProcessingMachineScreen
        )
        ClientRegistrationUtil.registerScreen(
            event,
            MekCreMachines.MEKANICAL_MILL.container,
            ::SingleInputProcessingMachineScreen
        )
        ClientRegistrationUtil.registerScreen(
            event,
            MekCreMachines.WHEEL_CRUSHER.container,
            ::SingleInputProcessingMachineScreen
        )
        ClientRegistrationUtil.registerScreen(
            event,
            MekCreMachines.MAGMA_CUBE_HEATER.container,
            ::MagmaCubeHeaterScreen
        )
    }

    @SubscribeEvent
    fun setup(event: FMLClientSetupEvent) {
        BlockEntityRenderers.register(
            MekCreMachines.MAGMA_CUBE_HEATER.tile.get(),
            ::MagmaCubeHeaterBlockRenderer
        )
    }
}
