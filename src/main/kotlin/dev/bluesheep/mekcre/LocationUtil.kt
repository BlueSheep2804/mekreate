package dev.bluesheep.mekcre

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object LocationUtil {
    fun rl(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MekCreMod.MODID, path)
    }

    fun rlMinecraft(path: String): ResourceLocation {
        return ResourceLocation.withDefaultNamespace(path)
    }

    fun rlCommon(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("c", path)
    }

    fun rlMekanism(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("mekanism", path)
    }

    fun rlCreate(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("create", path)
    }

    fun tagItemCommon(path: String): TagKey<Item> {
        return TagKey.create(Registries.ITEM, rlCommon(path))
    }
}
