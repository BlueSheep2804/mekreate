package dev.bluesheep.mekcre

import com.mojang.logging.LogUtils
import dev.bluesheep.mekcre.registry.MekCreCreativeTabs
import dev.bluesheep.mekcre.registry.MekCreMachines
import dev.bluesheep.mekcre.registry.MekCreRecipeTypes
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(MekCreMod.MODID)
@EventBusSubscriber(modid = MekCreMod.MODID)
object MekCreMod {
    const val MODID: String = "mekcre"
    val LOGGER: Logger = LogUtils.getLogger()

    init {
        MekCreRecipeTypes.REGISTRY.register(MOD_BUS)
        MekCreMachines.REGISTRY.register(MOD_BUS)
        MekCreCreativeTabs.REGISTRY.register(MOD_BUS)
    }

    @SubscribeEvent
    fun commonSetup(event: FMLCommonSetupEvent) {}

    fun rl(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, path)
    }

    fun rlCreate(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("create", path)
    }

    fun rlMekanism(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("mekanism", path)
    }
}
