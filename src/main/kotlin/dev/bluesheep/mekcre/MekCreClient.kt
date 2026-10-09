package dev.bluesheep.mekcre

import com.takenokoshi.mekaddonlib.registration.GuiSizedMachineRegistryObject
import dev.bluesheep.mekcre.block.renderer.MagmaCubeHeaterBlockRenderer
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingFactory
import dev.bluesheep.mekcre.blockentity.base.SingleInputProcessingSemiFactory
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.registry.MekCrePartialModels
import dev.bluesheep.mekcre.screen.MagmaCubeHeaterScreen
import dev.bluesheep.mekcre.screen.SingleInputProcessingFactoryScreen
import dev.bluesheep.mekcre.screen.SingleInputProcessingMachineScreen
import dev.bluesheep.mekcre.screen.SingleInputProcessingSemiFactoryScreen
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
        MekCreMachines.FACTORIES.forEach { (name, machines) ->
            machines.forEach { (tier, machine) -> registerFactoryScreen(event, machine) }
        }
        MekCreMachines.SEMI_FACTORIES.forEach { (name, machines) ->
            machines.forEach { (tier, machine) -> registerSemiFactoryScreen(event, machine) }
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
            MekCreMachines.MEKANICAL_CRUSHER.container,
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

    private fun <BE: SingleInputProcessingFactory<*>> registerFactoryScreen(event: RegisterMenuScreensEvent, machine: GuiSizedMachineRegistryObject<BE>) {
        ClientRegistrationUtil.registerScreen(
            event,
            machine.container,
            ::SingleInputProcessingFactoryScreen
        )
    }

    private fun <BE: SingleInputProcessingSemiFactory<*>> registerSemiFactoryScreen(event: RegisterMenuScreensEvent, machine: GuiSizedMachineRegistryObject<BE>) {
        ClientRegistrationUtil.registerScreen(
            event,
            machine.container,
            ::SingleInputProcessingSemiFactoryScreen
        )
    }
}
