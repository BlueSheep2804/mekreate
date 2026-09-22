package dev.bluesheep.mekcre.datagen

import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import com.tterrag.registrate.providers.RegistrateLangProvider
import dev.bluesheep.mekcre.MekCreMod.MODID
import dev.bluesheep.mekcre.registry.MekCreMachines
import net.neoforged.neoforge.registries.DeferredHolder

class MekCreLanguageProvider(val provider: RegistrateLangProvider) {
    init {
        provider.add("itemGroup.${MODID}", "Mekanism x Create")

        MekCreMachines.REGISTRY.blockRegister.primaryEntries.forEach { machine ->
            provider.addBlock(machine, format(machine.id.path))
            addContainer(machine, format(machine.id.path))
        }

        addDescription(MekCreMachines.MEKANICAL_PRESS, "A simple machine used for pressing items.")
        addDescription(MekCreMachines.MAGMA_CUBE_HEATER, "A Magma Cube that converts Mekanism heat into a heat source for Create.")

        provider.add("mekcre.gui.goggles.infuser_info", "Mechanical Infuser Info:")
        provider.add("mekcre.gui.goggles.infuser_info.chemical", "Chemical: %s")

        provider.add("mekcre.gui.idle", "Idle")
        provider.add("mekcre.gui.heated", "Heated")
        provider.add("mekcre.gui.superheated", "Superheated")
        provider.add("mekcre.gui.current_heat_level", "Current heat level: %s")
        provider.add("mekcre.gui.configured_heat_level", "Configured heat level: %s")

        provider.add("mekcre.recipe.assembly.infuse_chemical", "Infuse %s")
    }

    private fun format(name: String): String {
        return name.split("_")
            .map { it.replaceFirstChar(Char::uppercase) }
            .joinToString(" ") { it }
    }

    private fun addDescription(machine: SimpleMachineRegistryObject<*>, value: String) {
        provider.add(machine.descriptionEntry.translationKey, value)
    }

    private fun addContainer(machine: DeferredHolder<*, *>, value: String) {
        provider.add(machine.id.toLanguageKey("container"), value)
    }
}