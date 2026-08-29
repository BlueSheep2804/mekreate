package dev.bluesheep.mekcre.blockentity

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import mekanism.api.Action
import mekanism.api.chemical.*
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe
import mekanism.api.recipes.MekanismRecipeTypes
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs

class MechanicalInfuserBlockEntity(
    type: BlockEntityType<MechanicalInfuserBlockEntity>,
    pos: BlockPos,
    state: BlockState
) : KineticBlockEntity(
    type,
    pos,
    state
), IMekanismChemicalHandler {
    companion object {
        fun capabilityProvider(blockEntity: MechanicalInfuserBlockEntity, direction: Direction?): IChemicalHandler? {
            return if (direction != Direction.DOWN) {
                blockEntity
            } else null
        }
    }
    val inputInfusionTank: IChemicalTank = BasicChemicalTank.inputModern(1000, { true }, this::setChanged)
    private var beltProcessing: BeltProcessingBehaviour? = null
    var processingTicks: Int = -1
    private var previousSpeed = 0f
    private val metallurgicRecipeType = MekanismRecipeTypes.TYPE_METALLURGIC_INFUSING.get()

    override fun getChemicalTanks(side: Direction?): List<IChemicalTank?> {
        return if (side != Direction.DOWN) {
            listOf(inputInfusionTank)
        } else listOf(null)
    }

    override fun onContentsChanged() {
        inputInfusionTank.onContentsChanged()
    }

    override fun addBehaviours(behaviors: MutableList<BlockEntityBehaviour?>) {
        beltProcessing = BeltProcessingBehaviour(this)
            .whenItemEnters(::onItemReceived)
            .whileItemHeld(::whenItemHeld)
        behaviors.add(beltProcessing)
    }

    fun onItemReceived(
        transported: TransportedItemStack,
        handler: TransportedItemStackHandlerBehaviour?
    ): ProcessingResult {
        if (isVirtual) return ProcessingResult.PASS
        val recipes = getRecipe(transported.stack)
        if (recipes.isEmpty()) return ProcessingResult.PASS
        if (inputInfusionTank.isEmpty) return ProcessingResult.HOLD
        if (!recipes.any { it.chemicalInput.testType(inputInfusionTank.stack) }) return ProcessingResult.PASS

        return ProcessingResult.HOLD
    }

    fun whenItemHeld(
        transported: TransportedItemStack,
        handler: TransportedItemStackHandlerBehaviour
    ): ProcessingResult {
        if (processingTicks > 0) return ProcessingResult.HOLD
        var recipes = getRecipe(transported.stack)
        if (recipes.isEmpty()) return ProcessingResult.PASS

        val infusionStack = inputInfusionTank.stack

        if (inputInfusionTank.isEmpty) return ProcessingResult.HOLD
        recipes = recipes.filter { it.chemicalInput.testType(infusionStack) }
        if (recipes.isEmpty()) return ProcessingResult.PASS
        if (recipes.all {
            it.chemicalInput.getNeededAmount(infusionStack) > inputInfusionTank.stored
        }) return ProcessingResult.HOLD

        val recipe = getRecipe(transported.stack, infusionStack)
        recipe ?: return ProcessingResult.PASS

        if (processingTicks <= -1) {
            processingTicks = calculateProcessingSpeed()
            notifyUpdate()
            return ProcessingResult.HOLD
        }

        val output = recipe.getOutput(transported.stack, infusionStack)
        if (!output.isEmpty) {
            transported.clearFanProcessingData()
            val outputList = mutableListOf<TransportedItemStack>()
            var held: TransportedItemStack? = null
            val result = transported.copy()
            result.stack = output
            if (!transported.stack.isEmpty) {
                held = transported.copy()
                held.stack.shrink(recipe.itemInput.getNeededAmount(held.stack).toInt())
            }
            outputList.add(result)
            handler.handleProcessingOnItem(
                transported,
                TransportedResult.convertToAndLeaveHeld(outputList, held)
            )
            inputInfusionTank.shrinkStack(recipe.chemicalInput.getNeededAmount(infusionStack), Action.EXECUTE)
        }

        notifyUpdate()
        return ProcessingResult.HOLD
    }

    override fun tick() {
        super.tick()

        if (getSpeed() == 0f) return

        if (processingTicks >= 0) {
            processingTicks--
        }

        if (processingTicks in 1..<16 && level!!.isClientSide) {
            var vec = VecHelper.getCenterOf(worldPosition)
            vec = vec.subtract(0.0, 1.5, 0.0)
            val color = Vec3.fromRGB24(inputInfusionTank.stack.chemicalColorRepresentation).toVector3f()
            val particle = DustParticleOptions(color, 1f)
            level!!.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, 0.0, -.2, 0.0)
        }

        notifyUpdate()
    }

    override fun remove() {
        level?.invalidateCapabilities(blockPos)
        super.remove()
    }

    protected override fun write(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        compound.putInt("ProcessingTicks", processingTicks)
        compound.put("InputInfusionTank", inputInfusionTank.serializeNBT(registries))

        super.write(compound, registries, clientPacket)
    }

    protected override fun read(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        super.read(compound, registries, clientPacket)

        processingTicks = compound.getInt("ProcessingTicks")

        inputInfusionTank.setStackUnchecked(ChemicalStack.EMPTY)
        inputInfusionTank.deserializeNBT(registries, compound.getCompound("InputInfusionTank"))
    }

    fun calculateProcessingSpeed(speed: Float): Int {
        return (128 / abs(speed) * 20).toInt()
    }

    fun calculateProcessingSpeed(): Int {
        return calculateProcessingSpeed(getSpeed())
    }

    override fun calculateStressApplied(): Float {
        return 8f
    }

    override fun onSpeedChanged(prevSpeed: Float) {
        if (processingTicks == -1) return
        if (getSpeed() == 0f) {
            previousSpeed = prevSpeed
        } else {
            val rate = abs(previousSpeed) / abs(getSpeed())
            processingTicks = Mth.clamp(processingTicks / rate, 1f, calculateProcessingSpeed().toFloat()).toInt()
        }
    }

    private fun getRecipe(
        inputItem: ItemStack,
        inputInfusionStack: ChemicalStack
    ): ItemStackChemicalToItemStackRecipe? {
        return level?.recipeManager?.getRecipeFor(
            metallurgicRecipeType,
            SingleItemChemicalRecipeInput(inputItem, inputInfusionStack),
            level
        )?.getOrNull()?.value
    }

    private fun getRecipe(stack: ItemStack): List<ItemStackChemicalToItemStackRecipe> {
        return level?.recipeManager
            ?.getAllRecipesFor(metallurgicRecipeType)
            ?.filter { it.value.itemInput.test(stack) }
            ?.map(RecipeHolder<ItemStackChemicalToItemStackRecipe>::value) ?: emptyList()
    }
}