package dev.bluesheep.mekcre

import com.mojang.logging.LogUtils
import com.simibubi.create.AllBlockEntityTypes
import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.providers.ProviderType
import dev.bluesheep.mekcre.blockentity.MechanicalInfuserBlockEntity
import dev.bluesheep.mekcre.datagen.*
import dev.bluesheep.mekcre.registry.MekCreAttachmentTypes
import dev.bluesheep.mekcre.registry.MekCreBlockEntities
import dev.bluesheep.mekcre.registry.MekCreBlocks
import dev.bluesheep.mekcre.registry.MekCreCreativeTabs
import dev.bluesheep.mekcre.registry.MekCreItems
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.registry.MekCreMekanismRecipeTypes
import dev.bluesheep.mekcre.registry.MekCreRecipeSerializers
import dev.bluesheep.mekcre.registry.MekCreRecipeTypes
import mekanism.common.capabilities.Capabilities
import mekanism.common.capabilities.heat.ITileHeatHandler
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(MekCreMod.MODID)
@EventBusSubscriber(modid = MekCreMod.MODID)
object MekCreMod {
    const val MODID: String = "mekcre"
    val LOGGER: Logger = LogUtils.getLogger()
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MODID)
        .defaultCreativeTab(MekCreCreativeTabs.TAB.key)

    init {
        REGISTRATE.registerEventListeners(MOD_BUS)
        MekCreBlocks.register()
        MekCreBlockEntities.register()
        MekCreItems.register()

        MekCreRecipeTypes.REGISTRY.register(MOD_BUS)
        MekCreRecipeSerializers.REGISTRY.register(MOD_BUS)

        MekCreMekanismRecipeTypes.REGISTRY.register(MOD_BUS)
        MekCreMachines.REGISTRY.register(MOD_BUS)
        MekCreCreativeTabs.REGISTRY.register(MOD_BUS)
        MekCreAttachmentTypes.REGISTRY.register(MOD_BUS)
    }

    @SubscribeEvent
    fun commonSetup(event: FMLCommonSetupEvent) {}

    @SubscribeEvent
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.CHEMICAL.block,
            MekCreBlockEntities.MECHANICAL_INFUSER.get(),
            MechanicalInfuserBlockEntity::capabilityProvider
        )

        event.registerBlockEntity(
            Capabilities.HEAT,
            AllBlockEntityTypes.HEATER.get()
        ) { blockEntity, direction ->
            return@registerBlockEntity blockEntity as ITileHeatHandler
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun gatherDataHighest(event: GatherDataEvent) {
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, ::MekCreBlockTagsProvider)
        REGISTRATE.addDataGenerator(ProviderType.LANG, ::MekCreLanguageProvider)
    }

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val packOutput = event.generator.packOutput
        event.addProvider(MekCreLootTableProvider(packOutput, event.lookupProvider))
        event.addProvider(MekCreRecipeProvider(packOutput, event.lookupProvider))

        event.addProvider(MekCreBlockModelProvider(packOutput, event.existingFileHelper))
        event.addProvider(MekCreItemModelProvider(packOutput, event.existingFileHelper))
    }

}
