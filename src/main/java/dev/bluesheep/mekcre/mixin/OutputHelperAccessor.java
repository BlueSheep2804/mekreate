package dev.bluesheep.mekcre.mixin;

import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.outputs.OutputHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OutputHelper.class)
public interface OutputHelperAccessor {
    @Invoker("calculateOperationsCanSupport")
    static void calculateOperationsCanSupport(CachedRecipe.OperationTracker tracker, CachedRecipe.OperationTracker.RecipeError notEnoughSpace, IInventorySlot slot, ItemStack toOutput) {
        throw new AssertionError();
    }

    @Invoker("handleOutput")
    static void handleOutput(IInventorySlot slot, ItemStack toOutput, int operations) {
        throw new AssertionError();
    }
}
