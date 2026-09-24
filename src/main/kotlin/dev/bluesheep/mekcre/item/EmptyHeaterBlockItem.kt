package dev.bluesheep.mekcre.item

import dev.bluesheep.mekcre.MekCreLang
import dev.bluesheep.mekcre.registry.MekCreMachines
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.MagmaCube
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3

class EmptyHeaterBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (interactionTarget !is MagmaCube) return InteractionResult.PASS
        if (!interactionTarget.isTiny) {
            player.displayClientMessage(MekCreLang.HEATER_CAPTURE_ERROR.translate(), true)
            return InteractionResult.PASS
        }

        val level = player.level()

        spawnCaptureEffects(level, interactionTarget.position())
        if (level.isClientSide) return InteractionResult.FAIL

        giveHeater(player, stack, usedHand)
        interactionTarget.discard()
        return InteractionResult.FAIL
    }

    // CreateのBlazeBurnerBlockItem.javaから引用
    // TODO: パーティクルを少し変更する
    private fun spawnCaptureEffects(world: Level, vec: Vec3) {
        if (world.isClientSide) {
            for (i in 0..39) {
                val motion = VecHelper.offsetRandomly(Vec3.ZERO, world.random, .125f)
                world.addParticle(ParticleTypes.FLAME, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z)
                val circle = motion.multiply(1.0, 0.0, 1.0)
                    .normalize()
                    .scale(.5)
                world.addParticle(ParticleTypes.SMOKE, circle.x, vec.y, circle.z, 0.0, -0.125, 0.0)
            }
            return
        }

        val soundPos = BlockPos.containing(vec)
        world.playSound(null, soundPos, SoundEvents.BLAZE_HURT, SoundSource.HOSTILE, .25f, .75f)
        world.playSound(null, soundPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, .5f, .75f)
    }

    private fun giveHeater(player: Player, stack: ItemStack, usedHand: InteractionHand) {
        if (!player.isCreative) {
            stack.shrink(1)
        }
        if (stack.isEmpty) {
            player.setItemInHand(usedHand, ItemStack(MekCreMachines.MAGMA_CUBE_HEATER.block))
        } else {
            player.inventory.placeItemBackInInventory(ItemStack(MekCreMachines.MAGMA_CUBE_HEATER.block))
        }
    }
}