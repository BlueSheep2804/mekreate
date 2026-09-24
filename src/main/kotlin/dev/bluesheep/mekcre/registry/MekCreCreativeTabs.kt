package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.MekCreLang
import dev.bluesheep.mekcre.MekCreMod
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object MekCreCreativeTabs {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MekCreMod.MODID)

    val TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = REGISTRY.register("tab") { ->
        CreativeModeTab.builder()
            .title(MekCreLang.ITEM_GROUP.translate())
            .icon { MekCreItems.OSMIUM_SHEET.get().defaultInstance }
            .displayItems { parameters, output ->
                MekCreMachines.REGISTRY.machines.forEach(output::accept)
            }
            .build()
    }
}