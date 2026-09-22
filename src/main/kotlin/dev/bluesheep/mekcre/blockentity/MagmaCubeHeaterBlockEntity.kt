package dev.bluesheep.mekcre.blockentity

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock
import com.simibubi.create.content.processing.basin.BasinBlock
import com.simibubi.create.content.fluids.tank.FluidTankBlock
import net.createmod.catnip.animation.LerpedFloat
import net.createmod.catnip.math.AngleHelper
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import com.simibubi.create.content.processing.recipe.HeatCondition
import mekanism.api.IContentsListener
import mekanism.common.capabilities.heat.BasicHeatCapacitor
import mekanism.common.capabilities.heat.CachedAmbientTemperature
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.inventory.container.MekanismContainer
import mekanism.common.inventory.container.sync.SyncableDouble
import mekanism.common.inventory.container.sync.SyncableEnum
import mekanism.common.tile.interfaces.IHasMode
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class MagmaCubeHeaterBlockEntity(
    blockProvider: Holder<Block>,
    pos: BlockPos,
    state: BlockState
) : TileEntityMekanism(
    blockProvider,
    pos,
    state
), IHasMode {
    val headAngle: LerpedFloat = LerpedFloat.angular().apply {
        startWithValue((AngleHelper.horizontalAngle(direction) + 180) % 360.0)
    }

    lateinit var heatCapacitor: BasicHeatCapacitor
    var lastEnvironmentLoss = 0.0
    var lastTransferLoss = 0.0

    var heatCondition = HeatCondition.NONE
        private set

    var currentHeatLevel: BlazeBurnerBlock.HeatLevel
        get() = blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL)
        set(value) {
            if (currentHeatLevel == value) return
            level?.setBlockAndUpdate(blockPos, blockState.setValue(BlazeBurnerBlock.HEAT_LEVEL, value))
        }

    override fun nextMode() {
        cycleHeatLevel(1)
    }

    override fun previousMode() {
        cycleHeatLevel(-1)
    }

    fun cycleHeatLevel(offset: Int) {
        if (level?.isClientSide != false) return
        val entries = HeatCondition.entries
        heatCondition = entries[Math.floorMod(heatCondition.ordinal + offset, entries.size)]
        setChanged()
    }

    override fun addContainerTrackers(container: MekanismContainer) {
        super.addContainerTrackers(container)
        container.track(SyncableEnum.create(
            { HeatCondition.entries.getOrNull(it) ?: HeatCondition.NONE },
            HeatCondition.NONE,
            this::heatCondition,
            { heatCondition = it }
        ))
        container.track(SyncableDouble.create(::lastTransferLoss) { lastTransferLoss = it })
        container.track(SyncableDouble.create(::lastEnvironmentLoss) { lastEnvironmentLoss = it })
    }

    override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(nbt, provider)
        nbt.putString("heatLevel", heatCondition.name)
    }

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(nbt, provider)
        heatCondition = HeatCondition.entries.firstOrNull { it.name == nbt.getString("heatLevel") } ?: HeatCondition.NONE
    }

    override fun getInitialHeatCapacitors(
        listener: IContentsListener?,
        ambientTemperature: CachedAmbientTemperature?
    ): IHeatCapacitorHolder? {
        val builder = HeatCapacitorHelper.forSide(facingSupplier)

        heatCapacitor = BasicHeatCapacitor.create(
            100.0,
            5.0,
            1.0,
            ambientTemperature,
            listener
        )
        builder.addCapacitor(heatCapacitor)

        return builder.build()
    }

    override fun onUpdateServer(): Boolean {
        var sendUpdatePacket = super.onUpdateServer()

        var tempHeatLevel = BlazeBurnerBlock.HeatLevel.NONE
        if (canFunction()) {
            when (heatCondition) {
                HeatCondition.HEATED -> {
                    if (heatCapacitor.temperature > 1200.0) {
                        tempHeatLevel = BlazeBurnerBlock.HeatLevel.KINDLED
                        heatCapacitor.handleHeat(-200.0)
                    }
                }
                HeatCondition.SUPERHEATED -> {
                    if (heatCapacitor.temperature > 12000.0) {
                        tempHeatLevel = BlazeBurnerBlock.HeatLevel.SEETHING
                        heatCapacitor.handleHeat(-2400.0)
                    }
                }
                else -> {}
            }
        }

        if (tempHeatLevel == BlazeBurnerBlock.HeatLevel.NONE) {
            currentHeatLevel = BlazeBurnerBlock.HeatLevel.NONE
            active = false
        } else {
            currentHeatLevel = tempHeatLevel
            active = true
        }

        val transfer = simulate()
        lastEnvironmentLoss = transfer.environmentTransfer
        lastTransferLoss = transfer.adjacentTransfer

        return sendUpdatePacket
    }

    @OnlyIn(Dist.CLIENT)
    override fun onUpdateClient() {
        super.onUpdateClient()
        val world = level ?: return
        val above = blockPos.above()
        val heating = currentHeatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) &&
            (BasinBlock.isBasin(world, above) || world.getBlockState(above).block is FluidTankBlock)

        if (heating) {
            val target = (AngleHelper.horizontalAngle(direction) + 180) % 360
            headAngle.chase(target.toDouble(), 0.125, LerpedFloat.Chaser.EXP)
        } else {
            var target = 0f
            val player = Minecraft.getInstance().player
            if (player != null && !player.isInvisible) {
                val dx = player.x - (blockPos.x + 0.5)
                val dz = player.z - (blockPos.z + 0.5)
                target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90
            }
            target = headAngle.value + AngleHelper.getShortestAngleDiff(headAngle.value.toDouble(), target.toDouble())
            headAngle.chase(target.toDouble(), 0.25, LerpedFloat.Chaser.exp(5.0))
        }
        headAngle.tickChaser()
    }
}
