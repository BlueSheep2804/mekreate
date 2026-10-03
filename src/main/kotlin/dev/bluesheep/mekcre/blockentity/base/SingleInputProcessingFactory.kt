package dev.bluesheep.mekcre.blockentity.base

import com.simibubi.create.content.processing.recipe.ProcessingRecipe
import com.takenokoshi.mekaddonlib.blockentity.base.BEMultiScaledProgressFactory
import com.takenokoshi.mekaddonlib.recipe.cached.ICachedRecipe
import com.takenokoshi.mekaddonlib.recipe.inputcache.MekALSingleInputRecipeCache
import com.takenokoshi.mekaddonlib.recipe_viewer.type.RVMekALRecipeTypeWrapper
import dev.bluesheep.mekcre.blockentity.upgradeData.ProcessingMachineUpgradeData
import dev.bluesheep.mekcre.recipe.cached.SingleInputCachedProcessingRecipe
import dev.bluesheep.mekcre.recipe.output.ProcessingOutputHandler
import dev.bluesheep.mekcre.registry.MekCreMekanismRecipeTypes
import dev.bluesheep.mekcre.util.MachineSlotUtil
import mekanism.api.Action
import mekanism.api.IContentsListener
import mekanism.api.energy.IEnergyContainer
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError
import mekanism.api.recipes.inputs.InputHelper
import mekanism.common.capabilities.energy.MachineEnergyContainer
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder
import mekanism.common.capabilities.holder.slot.InventorySlotHelper
import mekanism.common.inventory.slot.EnergyInventorySlot
import mekanism.common.inventory.slot.InputInventorySlot
import mekanism.common.inventory.slot.OutputInventorySlot
import mekanism.common.inventory.warning.WarningTracker
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.tier.FactoryTier
import mekanism.common.tile.component.TileComponentEjector
import mekanism.common.upgrade.IUpgradeData
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.util.ItemStackMap

abstract class SingleInputProcessingFactory<R: ProcessingRecipe<*, *>>(blockProvider: Holder<Block>, pos: BlockPos, state: BlockState) : BEMultiScaledProgressFactory<R>(
    blockProvider, pos, state,
    listOf(
        RecipeError.NOT_ENOUGH_INPUT,
        RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
        RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    ),
    setOf(
        RecipeError.NOT_ENOUGH_ENERGY
    ),
    1,
    { BASE_TICKS_REQUIRED }
) {
    companion object {
        const val BASE_TICKS_REQUIRED = (10 * SharedConstants.TICKS_PER_SECOND)
    }

    private lateinit var inputSlot: Array<InputInventorySlot>
    private lateinit var outputSlot: Array<Array<OutputInventorySlot>>
    lateinit var energySlot: EnergyInventorySlot

    private val inputHandler = inputSlot.map {
        InputHelper.getInputHandler(it, RecipeError.NOT_ENOUGH_INPUT)
    }
    private val outputHandler = outputSlot.map { slot ->
        ProcessingOutputHandler(
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            *slot
        )
    }

    lateinit var energyContainer: MachineEnergyContainer<*>

    init {
        configComponent.setupItemIOConfig(
            inputSlot.toList(),
            outputSlot.flatten(),
            energySlot,
            false
        )
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer)
        ejectorComponent = TileComponentEjector(this)
            .setOutputData(configComponent, TransmissionType.ITEM)
    }

    abstract fun getOutputCount(): Int

    abstract fun getRecipeViewerTypes(): Array<RVMekALRecipeTypeWrapper<*, *, *>>

    override fun getInitialInventory(
        listener: IContentsListener?,
        recipeCacheListeners: Array<out IContentsListener?>,
        allRecipeCacheListener: IContentsListener?,
        recipeCacheUnpauseListeners: Array<out IContentsListener?>,
        allRecipeCacheUnpauseListeners: IContentsListener?
    ): IInventorySlotHolder? {
        val baseX = MachineSlotUtil.getBaseX(tier)
        val baseXMult = MachineSlotUtil.getBaseXMult(tier)

        inputSlot = Array(tier.processes) { i ->
            InputInventorySlot.at(
                recipeCacheListeners[i],
                baseX + (i * baseXMult), 13
            )
        }
        outputSlot = MachineSlotUtil.multipleOutputSlotForFactory(recipeCacheUnpauseListeners, getOutputCount(), tier)

        energySlot = if (tier.ordinal > FactoryTier.ULTIMATE.ordinal) {
            val imageWidth = 176 + 38 * (tier.ordinal - 4 + 1) + 9
            EnergyInventorySlot.fillOrConvert(
                energyContainer,
                this::level,
                listener,
                8 + (imageWidth / 2 - 180 / 2) - 22,
                124 + 18 * getOutputCount() + 1
            )
        } else {
            EnergyInventorySlot.fillOrConvert(
                energyContainer,
                this::level,
                listener,
                7,
                13
            )
        }

        val builder = InventorySlotHelper.forSideWithConfig(this)
        for (i in 0 until tier.processes) {
            builder.addSlot(inputSlot[i])
                .tracksWarnings {
                    it.warning(
                        WarningTracker.WarningType.NO_MATCHING_RECIPE,
                        getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, i)
                    )
                }
            repeat(getOutputCount()) { slotIndex ->
                builder.addSlot(outputSlot[i][slotIndex])
                    .tracksWarnings {
                        it.warning(
                            WarningTracker.WarningType.NO_SPACE_IN_OUTPUT,
                            getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, i)
                        )
                    }
            }
        }
        builder.addSlot(energySlot)

        return builder.build()
    }

    override fun getInitialEnergyContainers(
        listener: IContentsListener?,
        recipeCacheListeners: Array<out IContentsListener?>?,
        allRecipeCacheListener: IContentsListener?,
        recipeCacheUnpauseListeners: Array<out IContentsListener?>?,
        allRecipeCacheUnpauseListeners: IContentsListener?
    ): IEnergyContainerHolder? {
        energyContainer = MachineEnergyContainer.input(
            this, allRecipeCacheUnpauseListeners
        )

        val builder = EnergyContainerHelper.forSideWithConfig(this)
        builder.addContainer(energyContainer)

        return builder.build()
    }

    override fun getEnergyContainer(container: Int, side: Direction?): IEnergyContainer {
        return energyContainer
    }

    override fun getExtraWidth(): Int {
        return when (tier.processes) {
            9 -> 34
            in 10..Int.MAX_VALUE -> 38 * (tier.ordinal - FactoryTier.ULTIMATE.ordinal) + 9
            else -> 0
        }
    }

    override fun getExtraHeight(): Int {
        return if (getOutputCount() > 1) {
            19 + 18 * (getOutputCount() - 2)
        } else 0
    }

    override fun onUpdateServer(): Boolean {
        val needsPacket = super.onUpdateServer()
        energySlot.fillContainerOrConvert()
        updateAndProcess(energyContainer)
        return needsPacket
    }

    override fun getRecipe(index: Int): R? {
        val slot = inputSlot[index]
        if (slot.isEmpty) return null

        level ?: return null
        return (recipeType.inputCache as MekALSingleInputRecipeCache.MekALSingleItem<out R>)
            .findFirstRecipe(level, slot.stack)
    }

    override fun createNewCachedRecipe(
        recipe: R,
        index: Int
    ): ICachedRecipe<R?> {
        return SingleInputCachedProcessingRecipe(
            recipe,
            recheckAllRecipeErrors[index],
            inputHandler[index],
            outputHandler[index],
            level!!.random
        )
            .setErrorsChanged { errorTracker.onErrorsChanged(it, index) }
            .setCanHolderFunction(this::canFunction)
            .setActive(this::setActive)
            .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
            .setRequiredTicks { ticksRequired[index] }
            .setOnFinish(this::markForSave)
            .setOperatingTicksChanged { operatingTicks[index] = it }
            .setBaselineMaxOperations { operationsPerTicks[index] }
    }

    override fun getUpgradeData(provider: HolderLookup.Provider?): IUpgradeData {
        return ProcessingMachineUpgradeData(
            provider,
            redstone,
            controlType,
            energyContainer,
            operatingTicks,
            energySlot,
            inputSlot,
            outputSlot.toList(),
            sorting,
            components
        )
    }

    override fun parseUpgradeData(provider: HolderLookup.Provider?, data: IUpgradeData) {
        if (data is ProcessingMachineUpgradeData) {
            redstone = data.redstone
            controlType = data.controlType
            energyContainer.energy = data.energyContainer.energy
            data.progress.forEachIndexed { index, i ->
                operatingTicks[index] = i
            }
            energySlot.deserializeNBT(provider, data.energySlot.serializeNBT(provider))
            data.inputSlots.forEachIndexed { index, slot ->
                inputSlot[index].deserializeNBT(provider, slot.serializeNBT(provider))
            }
            data.outputSlots.forEachIndexed { slotIndex, slots ->
                slots.forEachIndexed { index, slot ->
                    outputSlot[slotIndex][index].stack = slot.stack
                }
            }
            sorting = data.sorting
            for (component in components) {
                component.read(data.components, provider)
            }
        }
    }

    override fun runSort() {
        // 同じアイテム・コンポーネントを持つ入力のラインをまとめる。
        // キーは後でスロットを変更しても影響を受けないコピーにする。
        val groups = ItemStackMap.createTypeAndTagMap<MutableList<Int>>()
        val emptyProcesses = mutableListOf<Int>()

        for (index in inputSlot.indices) {
            val slot = inputSlot[index]

            if (slot.isEmpty) {
                emptyProcesses.add(index)
            } else {
                groups.computeIfAbsent(slot.stack.copyWithCount(1)) {
                    mutableListOf()
                }.add(index)
            }
        }

        for ((item, processes) in groups) {
            val totalCount = processes.sumOf { inputSlot[it].count }

            // 現在の Press は1回につき1個消費するので、
            // totalCount 本を超えてラインを増やさない。
            if (emptyProcesses.isNotEmpty() &&
                processes.size < totalCount) {

                // 実際の処理と同じ方法でレシピを選ぶ。
                val recipe = MekCreMekanismRecipeTypes.PRESS
                    .inputCache
                    .findFirstRecipe(level, item)

                if (recipe != null) {
                    // 分配判定では確率抽選しない。
                    val results = recipe.rollableResultsAsItemStacks

                    // この機械が扱える出力は2枠まで。
                    if (results.size <= 2) {
                        val iterator = emptyProcesses.iterator()

                        while (iterator.hasNext() &&
                            processes.size < totalCount) {
                            val index = iterator.next()
                            var compatible = true

                            for (resultIndex in results.indices) {
                                val expected = results[resultIndex]
                                val stored = outputSlot[resultIndex][index].stack

                                // 出力しない枠は、残っているアイテムに
                                // 干渉しないため許可する。
                                if (!expected.isEmpty &&
                                    !stored.isEmpty &&
                                    !ItemStack.isSameItemSameComponents(
                                        expected,
                                        stored
                                    )
                                ) {
                                    compatible = false
                                    break
                                }
                            }

                            if (compatible) {
                                processes.add(index)
                                iterator.remove()
                            }
                        }
                    }
                }
            }

            // 空きがなくても、既存の同種入力どうしを均等化する。
            if (processes.size <= 1) continue

            val perProcess = totalCount / processes.size
            val remainder = totalCount % processes.size

            for ((order, index) in processes.withIndex()) {
                val slot = inputSlot[index]
                val targetCount =
                    perProcess + if (order < remainder) 1 else 0

                when {
                    targetCount == 0 -> {
                        if (!slot.isEmpty) {
                            slot.setEmpty()
                        }
                    }

                    slot.isEmpty -> {
                        slot.setStack(item.copyWithCount(targetCount))
                    }

                    slot.count != targetCount -> {
                        slot.setStackSize(targetCount, Action.EXECUTE)
                    }
                }
            }
        }
    }
}