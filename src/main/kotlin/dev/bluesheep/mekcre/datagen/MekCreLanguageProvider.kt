package dev.bluesheep.mekcre.datagen

import com.takenokoshi.mekaddonlib.registration.SimpleMachineRegistryObject
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.registry.MekCreBlocks
import dev.bluesheep.mekcre.registry.MekCreMachines
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.registries.DeferredHolder

class MekCreLanguageProvider(output: PackOutput) : LanguageProvider(output, MekCreMod.MODID, "en_us") {
    override fun addTranslations() {
        MekCreMachines.REGISTRY.blockRegister.primaryEntries.forEach { machine ->
            addBlock(machine, format(machine.id.path))
            addContainer(machine, format(machine.id.path))
        }

        MekCreBlocks.REGISTRY.entries.forEach { block ->
            addBlock(block, format(block.id.path))
        }

        addDescription(MekCreMachines.MEKANICAL_PRESS, "A simple machine used for pressing items.")
    }

    private fun format(name: String): String {
        return name.split("_")
            .map { it.replaceFirstChar(Char::uppercase) }
            .joinToString(" ") { it }
    }

    private fun addDescription(machine: SimpleMachineRegistryObject<*>, value: String) {
        add(machine.descriptionEntry.translationKey, value)
    }

    private fun addContainer(machine: DeferredHolder<*, *>, value: String) {
        add(machine.id.toLanguageKey("container"), value)
    }
}