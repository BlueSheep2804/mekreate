package dev.bluesheep.mekcre.registry

import com.tterrag.registrate.util.entry.ItemEntry
import dev.bluesheep.mekcre.MekCreMod.REGISTRATE
import dev.bluesheep.mekcre.MekCreMod.rlCommon
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object MekCreItems {
    val OSMIUM_SHEET: ItemEntry<Item> = REGISTRATE.item(
        "osmium_sheet", ::Item
    )
        .tag(tagCommon("plates"))
        .tag(tagCommon("plates/osmium"))
        .register()

    private fun tagCommon(tag: String): TagKey<Item> {
        return TagKey.create(Registries.ITEM, rlCommon(tag))
    }

    fun register() {}
}