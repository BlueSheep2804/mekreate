package dev.bluesheep.mekcre.registry

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem
import com.tterrag.registrate.util.entry.ItemEntry
import dev.bluesheep.mekcre.LocationUtil
import dev.bluesheep.mekcre.MekCreMod.REGISTRATE
import net.minecraft.world.item.Item

object MekCreItems {
    val OSMIUM_SHEET: ItemEntry<Item> = REGISTRATE.item(
        "osmium_sheet", ::Item
    )
        .tag(LocationUtil.tagItemCommon("plates"))
        .tag(LocationUtil.tagItemCommon("plates/osmium"))
        .register()

    val INCOMPLETE_REFINED_OBSIDIAN: ItemEntry<SequencedAssemblyItem> = sequencedIngredient("incomplete_refined_obsidian")
    val INCOMPLETE_REFINED_GLOWSTONE: ItemEntry<SequencedAssemblyItem> = sequencedIngredient("incomplete_refined_glowstone")

    private fun sequencedIngredient(name: String): ItemEntry<SequencedAssemblyItem> {
        return REGISTRATE.item(name, ::SequencedAssemblyItem)
            .removeTab(MekCreCreativeTabs.TAB.key)
            .register()
    }

    fun register() {}
}