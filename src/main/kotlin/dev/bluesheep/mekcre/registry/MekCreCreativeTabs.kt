package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.MekCreMod
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Items
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object MekCreCreativeTabs {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MekCreMod.MODID)

    val TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = REGISTRY.register("tab") { ->
        CreativeModeTab.builder()
            .icon { Items.DIRT.defaultInstance }
            .displayItems { parameters, output ->
                MekCreMachines.REGISTRY.machines.forEach(output::accept)
                MekCreItems.REGISTRY.entries.forEach { output.accept(it.get()) }
            }
            .build()
    }
}