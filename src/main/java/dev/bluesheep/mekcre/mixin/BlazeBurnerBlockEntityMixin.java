package dev.bluesheep.mekcre.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.bluesheep.mekcre.mixinImpl.BlazeBurnerBlockEntityMixinImpl;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.common.capabilities.heat.ITileHeatHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class BlazeBurnerBlockEntityMixin extends SmartBlockEntity implements ITileHeatHandler {
    public BlazeBurnerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void mekcre$tick(CallbackInfo ci) {
        BlazeBurnerBlockEntityMixinImpl.tick((BlazeBurnerBlockEntity) (Object) this);
    }

    @Override
    public @NotNull List<IHeatCapacitor> getHeatCapacitors(@Nullable Direction side) {
        return BlazeBurnerBlockEntityMixinImpl.getHeatCapacitors((BlazeBurnerBlockEntity) (Object) this);
    }

    @Override
    public @Nullable IHeatHandler getAdjacent(@NotNull Direction side) {
        return BlazeBurnerBlockEntityMixinImpl.getAdjacent(
                (BlazeBurnerBlockEntity) (Object) this,
                side
        );
    }

    @Override
    public void onContentsChanged() {}
}
