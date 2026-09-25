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

    val KINETIC_CONTROL_CIRCUIT: ItemEntry<Item> = REGISTRATE.item("kinetic_control_circuit", ::Item)
        .tag(LocationUtil.tagItemCommon("circuits"))
        .tag(LocationUtil.tagItemCommon("circuits/kinetic"))
        .register()
    val CONTRAPTION_CONTROL_CIRCUIT: ItemEntry<Item> = REGISTRATE.item("contraption_control_circuit", ::Item)
        .tag(LocationUtil.tagItemCommon("circuits"))
        .tag(LocationUtil.tagItemCommon("circuits/contraption"))
        .register()

    val INCOMPLETE_REFINED_OBSIDIAN_INGOT: ItemEntry<SequencedAssemblyItem> = sequencedIngredient("incomplete_refined_obsidian_ingot")
    val INCOMPLETE_REFINED_GLOWSTONE_INGOT: ItemEntry<SequencedAssemblyItem> = sequencedIngredient("incomplete_refined_glowstone_ingot")
    val INCOMPLETE_CONTRAPTION_CONTROL_CIRCUIT: ItemEntry<SequencedAssemblyItem> = sequencedIngredient("incomplete_contraption_control_circuit")

    private fun sequencedIngredient(name: String): ItemEntry<SequencedAssemblyItem> {
        return REGISTRATE.item(name, ::SequencedAssemblyItem)
            .removeTab(MekCreCreativeTabs.TAB.key)
            .register()
    }

    fun register() {}
}