package dev.bluesheep.mekcre.blockentity

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import dev.bluesheep.mekcre.MekCreLang
import dev.bluesheep.mekcre.MekCreMod
import dev.bluesheep.mekcre.recipe.create.InfusingRecipe
import dev.bluesheep.mekcre.registry.MekCreRecipeTypes
import mekanism.api.Action
import mekanism.api.chemical.*
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe
import mekanism.api.recipes.MekanismRecipeTypes
import mekanism.api.recipes.ingredients.ChemicalStackIngredient
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput
import mekanism.common.MekanismLang
import mekanism.common.util.text.TextUtils
import net.createmod.catnip.lang.LangBuilder
import net.createmod.catnip.math.VecHelper
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.crafting.SizedIngredient
import java.util.Optional
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
        if (!recipes.any { it.inputChemical.testType(inputInfusionTank.stack) }) return ProcessingResult.PASS

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
        recipes = recipes.filter { it.inputChemical.testType(infusionStack) }
        if (recipes.isEmpty()) return ProcessingResult.PASS
        if (recipes.all {
            it.inputChemical.getNeededAmount(infusionStack) > inputInfusionTank.stored
        }) return ProcessingResult.HOLD

        val recipe = getRecipe(transported.stack, infusionStack)
        recipe ?: return ProcessingResult.PASS

        if (processingTicks <= -1) {
            processingTicks = calculateProcessingSpeed()
            notifyUpdate()
            return ProcessingResult.HOLD
        }

        val output = if (recipe.recipe is InfusingRecipe) {
            recipe.recipe.rollResults(level!!.random).first()
        } else {
            recipe.output
        }
        if (!output.isEmpty) {
            transported.clearFanProcessingData()
            val outputList = mutableListOf<TransportedItemStack>()
            var held: TransportedItemStack? = null
            val result = transported.copy()
            result.stack = output
            if (!transported.stack.isEmpty) {
                held = transported.copy()
                held.stack.shrink(recipe.inputItem.count())
            }
            outputList.add(result)
            handler.handleProcessingOnItem(
                transported,
                TransportedResult.convertToAndLeaveHeld(outputList, held)
            )
            inputInfusionTank.shrinkStack(recipe.inputChemical.getNeededAmount(infusionStack), Action.EXECUTE)
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

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        MekCreLang.INFUSER_INFO.forGoggles(tooltip)

        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        tooltip.removeAt(1)

        tooltip.add(Component.empty())

        MekCreLang.INFUSER_INFO_CHEMICAL.forGoggles(tooltip)

        LangBuilder(MekCreMod.MODID)
            .text(ChatFormatting.YELLOW, TextUtils.format(inputInfusionTank.stored))
            .text(ChatFormatting.GRAY, " / ")
            .add(
                MekanismLang.GENERIC_MB.translate(
                    TextUtils.format(inputInfusionTank.capacity)
                ).withStyle(ChatFormatting.DARK_GRAY)
            )
            .forGoggles(tooltip, 1)

        return true
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
    ): RecipeData? {
        val createRecipe = RecipeData.fromCreate(
            SequencedAssemblyRecipe.getRecipe(
                level,
                inputItem,
                MekCreRecipeTypes.INFUSING,
                InfusingRecipe::class.java
            )
        )
        if (createRecipe != null) {
            return createRecipe
        }

        return RecipeData.fromMekanism(
            level?.recipeManager?.getRecipeFor(
                metallurgicRecipeType,
                SingleItemChemicalRecipeInput(inputItem, inputInfusionStack),
                level
            )?.getOrNull()?.value
        )
    }

    private fun getRecipe(stack: ItemStack): List<RecipeData> {
        val createRecipe = RecipeData.fromCreate(
            SequencedAssemblyRecipe.getRecipe(
                level,
                stack,
                MekCreRecipeTypes.INFUSING,
                InfusingRecipe::class.java
            )
        )
        if (createRecipe != null) {
            return listOf(createRecipe)
        }

        return level?.recipeManager
            ?.getAllRecipesFor(metallurgicRecipeType)
            ?.filter { it.value.itemInput.test(stack) }
            ?.map { RecipeData.fromMekanism(it.value)!! } ?: emptyList()
    }

    private data class RecipeData(
        val recipe: Recipe<*>,
        val inputItem: SizedIngredient,
        val inputChemical: ChemicalStackIngredient,
        val output: ItemStack
    ) {
        companion object {
            fun fromMekanism(recipe: ItemStackChemicalToItemStackRecipe?): RecipeData? {
                recipe ?: return null
                return RecipeData(
                    recipe,
                    recipe.itemInput.ingredient(),
                    recipe.chemicalInput,
                    recipe.getOutput(null, null)
                )
            }

            fun fromCreate(recipe: Optional<RecipeHolder<InfusingRecipe>>): RecipeData? {
                if (recipe.isEmpty) return null
                val recipe = recipe.get().value
                return RecipeData(
                    recipe,
                    SizedIngredient(recipe.ingredients.first(), 1),
                    recipe.chemicalIngredient,
                    recipe.getResultItem(null)
                )
            }
        }
    }
}