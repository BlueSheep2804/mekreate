package dev.bluesheep.mekcre

import mekanism.api.text.ILangEntry
import net.minecraft.network.chat.Component

enum class MekCreLang(private val translationKey: String) : ILangEntry {
    ITEM_GROUP("itemGroup.${MekCreMod.MODID}"),
    HEAT_LEVEL_IDLE("gui", "heat_level.idle"),
    HEAT_LEVEL_HEATED("gui", "heat_level.heated"),
    HEAT_LEVEL_SUPERHEATED("gui", "heat_level.superheated"),
    HEAT_LEVEL_CURRENT("gui", "heat_level.current"),
    HEAT_LEVEL_CONFIGURED("gui", "heat_level.configured"),
    INFUSER_INFO("gui", "goggles.infuser_info"),
    INFUSER_INFO_CHEMICAL("gui", "goggles.infuser_info.chemical"),
    ASSEMBLY_INFUSE_CHEMICAL("recipe", "assembly.infuse_chemical"),;

    constructor(type: String, path: String) : this("$type.${MekCreMod.MODID}.$path")

    override fun getTranslationKey(): String {
        return translationKey
    }

    fun forGoggles(tooltip: MutableList<in Component>) {
        forGoggles(tooltip, 0)
    }

    fun forGoggles(tooltip: MutableList<in Component>, indents: Int) {
        tooltip.add(
            Component.literal(" ".repeat(4 + indents))
                .append(this.translate())
        )
    }
}