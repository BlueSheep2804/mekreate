package dev.bluesheep.mekcre.datagen

import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import com.tterrag.registrate.providers.RegistrateLangProvider
import dev.bluesheep.mekcre.MekCreLang
import dev.bluesheep.mekcre.MekCreMod.MODID
import dev.bluesheep.mekcre.registry.MekCreMachines
import mekanism.api.text.ILangEntry
import net.neoforged.neoforge.registries.DeferredHolder

class MekCreLanguageProvider(val provider: RegistrateLangProvider) {
    init {
        add(MekCreLang.ITEM_GROUP, "Mekanism x Create")

        MekCreMachines.REGISTRY.blockRegister.primaryEntries.forEach { machine ->
            provider.addBlock(machine, format(machine.id.path))
            addContainer(machine, format(machine.id.path))
        }

        addDescription(MekCreMachines.MEKANICAL_PRESS, "A simple machine used for pressing items.")
        addDescription(MekCreMachines.MAGMA_CUBE_HEATER, "A Magma Cube that converts Mekanism heat into a heat source for Create.")

        add(MekCreLang.INFUSER_INFO, "Mechanical Infuser Info:")
        add(MekCreLang.INFUSER_INFO_CHEMICAL, "Chemical: %s")

        add(MekCreLang.HEAT_LEVEL_IDLE, "Idle")
        add(MekCreLang.HEAT_LEVEL_HEATED, "Heated")
        add(MekCreLang.HEAT_LEVEL_SUPERHEATED, "Superheated")
        add(MekCreLang.HEAT_LEVEL_CURRENT, "Current heat level: %s")
        add(MekCreLang.HEAT_LEVEL_CONFIGURED, "Configured heat level: %s")

        add(MekCreLang.ASSEMBLY_INFUSE_CHEMICAL, "Infuse %s")

        add(MekCreLang.HEATER_CAPTURE_ERROR, "It won't fit...")
    }

    private fun format(name: String): String {
        return name.split("_")
            .map { it.replaceFirstChar(Char::uppercase) }
            .joinToString(" ") { it }
    }

    private fun add(key: ILangEntry, value: String) {
        provider.add(key.translationKey, value)
    }

    private fun addDescription(machine: SimpleMachineRegistryObject<*>, value: String) {
        provider.add(machine.descriptionEntry.translationKey, value)
    }

    private fun addContainer(machine: DeferredHolder<*, *>, value: String) {
        provider.add(machine.id.toLanguageKey("container"), value)
    }
}