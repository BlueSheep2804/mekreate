package dev.bluesheep.mekcre.registry

import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.attachment.BlazeBurnerHeatData
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object MekCreAttachmentTypes {
    val REGISTRY: DeferredRegister<AttachmentType<*>> = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MekCreMod.MODID)

    val BLAZE_BURNER_HEAT: DeferredHolder<AttachmentType<*>, AttachmentType<BlazeBurnerHeatData>> = REGISTRY.register(
        "blaze_burner_heat"
    ) { ->
        AttachmentType.serializable(::BlazeBurnerHeatData).build()
    }
}