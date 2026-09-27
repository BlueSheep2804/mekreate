package dev.bluesheep.mekcre.datagen

import com.takenokoshi.mekaddonlib.registration.MachineRegistryObject
import com.tterrag.registrate.providers.RegistrateLangProvider
import dev.bluesheep.mekcre.MekCreLang
import dev.bluesheep.mekcre.block.attribute.AttributeDescription
import dev.bluesheep.mekcre.registry.MekCreMachines
import mekanism.api.text.ILangEntry

class MekCreLanguageProvider(val provider: RegistrateLangProvider) {
    init {
        add(MekCreLang.ITEM_GROUP, "Mekanism x Create")

        MekCreMachines.REGISTRY.machines.forEach { machine ->
            provider.addBlock(machine.block, format(machine.id.path))
            addContainer(machine, format(machine.id.path))
            val description = machine.blockType.get(AttributeDescription::class.java)?.value
            if (description != null) {
                addDescription(machine, description)
            }
        }

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

    private fun addDescription(machine: MachineRegistryObject<*, *, *, *>, value: String) {
        provider.add(machine.descriptionEntry.translationKey, value)
    }

    private fun addContainer(machine: MachineRegistryObject<*, *, *, *>, value: String) {
        provider.add(machine.id.toLanguageKey("container"), value)
    }
}