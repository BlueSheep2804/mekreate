package dev.bluesheep.mekcre.blockentity

import com.simibubi.create.content.kinetics.press.PressingRecipe
import com.takenokoshi.mekaddonlib.blockentity.base.BEMultiScaledProgressMachine
import com.takenokoshi.mekaddonlib.recipe.cached.ICachedRecipe
import com.takenokoshi.mekaddonlib.recipe.type.IMekALRecipeTypeProvider
import dev.bluesheep.mekcre.recipe.PressingCachedRecipe
import dev.bluesheep.mekcre.recipe.output.ProcessingOutputHandler
import dev.bluesheep.mekcre.registry.MekCreRecipeTypes
import mekanism.api.IContentsListener
import mekanism.api.energy.IEnergyContainer
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError
import mekanism.api.recipes.inputs.InputHelper
import mekanism.common.attachments.containers.ContainerType
import mekanism.common.attachments.containers.item.ItemSlotsBuilder
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
import mekanism.common.registration.impl.ItemRegistryObject
import mekanism.common.tile.component.TileComponentEjector
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class MekanicalPressBlockEntity(blockProvider: Holder<Block>, pos: BlockPos, state: BlockState) : BEMultiScaledProgressMachine<PressingRecipe>(
    blockProvider,
    pos,
    state,
    listOf(
        RecipeError.NOT_ENOUGH_INPUT,
        RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
        RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT,
        RecipeError.NOT_ENOUGH_ENERGY
    ),
    1,
    { BASE_TICKS_REQUIRED }
) {
    companion object {
        const val BASE_TICKS_REQUIRED = (10 * SharedConstants.TICKS_PER_SECOND)

        fun addContainersToItem(holder: ItemRegistryObject<*>) {
            holder.addAttachmentOnlyContainers(ContainerType.ITEM) {
                ItemSlotsBuilder.builder()
                    .addInput(1)
                    .addOutput(2)
                    .addEnergy()
                    .build()
            }
        }
    }
    private lateinit var inputSlot: InputInventorySlot
    private lateinit var outputSlot: OutputInventorySlot
    private lateinit var secondaryOutputSlot: OutputInventorySlot
    private lateinit var energySlot: EnergyInventorySlot

    private val inputHandler = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT)
    private val outputHandler = ProcessingOutputHandler(
        RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
        outputSlot,
        secondaryOutputSlot
    )

    lateinit var energyContainer: MachineEnergyContainer<*>

    init {
        configComponent.setupItemIOConfig(
            listOf(inputSlot),
            listOf(outputSlot, secondaryOutputSlot),
            energySlot,
            false
        )
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer)
        ejectorComponent = TileComponentEjector(this)
            .setOutputData(configComponent, TransmissionType.ITEM)
    }

    override fun getInitialInventory(
        listener: IContentsListener?,
        recipeCacheListener: IContentsListener?,
        recipeCacheUnpauseListener: IContentsListener?
    ): IInventorySlotHolder? {
        inputSlot = InputInventorySlot.at(
            recipeCacheListener,
            56, 17
        )
        outputSlot = OutputInventorySlot.at(
            recipeCacheUnpauseListener,
            108, 35
        )
        secondaryOutputSlot = OutputInventorySlot.at(
            recipeCacheUnpauseListener,
            126, 35
        )
        energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::level, listener, 56, 53)
        val builder = InventorySlotHelper.forSideWithConfig(this)
        builder.addSlot(inputSlot)
            .tracksWarnings { it.warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)) }
        builder.addSlot(outputSlot)
            .tracksWarnings { it.warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)) }
        builder.addSlot(secondaryOutputSlot)
            .tracksWarnings { it.warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)) }
        builder.addSlot(energySlot)

        return builder.build()
    }

    override fun getInitialEnergyContainers(
        listener: IContentsListener?,
        recipeCacheListener: IContentsListener?,
        recipeCacheUnpauseListener: IContentsListener?
    ): IEnergyContainerHolder? {
        energyContainer = MachineEnergyContainer.input(
            this, recipeCacheUnpauseListener
        )

        val builder = EnergyContainerHelper.forSideWithConfig(this)
        builder.addContainer(energyContainer)

        return builder.build()
    }

    override fun getEnergyContainer(container: Int, side: Direction?): IEnergyContainer {
        return energyContainer
    }

    override fun onUpdateServer(): Boolean {
        val needsPacket = super.onUpdateServer()
        energySlot.fillContainerOrConvert()
        recipeCacheLookupMonitor.updateAndProcess()
        return needsPacket
    }

    override fun getRecipe(p0: Int): PressingRecipe? {
        if (inputSlot.isEmpty) return null

        level ?: return null
        return MekCreRecipeTypes.PRESS
            .inputCache
            .findFirstRecipe(level, inputSlot.stack)
    }

    override fun createNewCachedRecipe(
        recipe: PressingRecipe,
        cacheIndex: Int
    ): ICachedRecipe<PressingRecipe?> {
        return PressingCachedRecipe(
            recipe,
            recheckAllRecipeErrors,
            inputHandler,
            outputHandler,
            level!!.random
        )
            .setErrorsChanged(this::onErrorsChanged)
            .setCanHolderFunction(this::canFunction)
            .setActive(this::setActive)
            .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
            .setRequiredTicks(this::getTicksRequired)
            .setOnFinish(this::markForSave)
            .setOperatingTicksChanged(this::setOperatingTicks)
            .setBaselineMaxOperations(this::getOperationsPerTick)
    }

    override fun getRecipeType(): IMekALRecipeTypeProvider<*, PressingRecipe?, *> {
        return MekCreRecipeTypes.PRESS
    }
}