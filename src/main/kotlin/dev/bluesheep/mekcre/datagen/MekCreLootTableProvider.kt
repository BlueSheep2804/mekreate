package dev.bluesheep.mekcre.datagen

import dev.bluesheep.mekcre.registry.MekCreMachines
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture

class MekCreLootTableProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>
) : LootTableProvider(
    output,
    emptySet(),
    listOf(
        SubProviderEntry(
            ::BlockProvider,
            LootContextParamSets.BLOCK
        )
    ),
    registries
) {
    private class BlockProvider(lookupProvider: HolderLookup.Provider) : BlockLootSubProvider(
        emptySet(),
        FeatureFlags.DEFAULT_FLAGS,
        lookupProvider
    ) {
        override fun getKnownBlocks(): Iterable<Block?> {
            return MekCreMachines.REGISTRY.blockRegister.primaryEntries.map { it.get() }
        }

        override fun generate() {
            MekCreMachines.REGISTRY.blockRegister.primaryEntries.forEach { machine ->
                dropSelf(machine.get())
            }
        }
    }
}