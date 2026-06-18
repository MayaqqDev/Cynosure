package dev.mayaqq.cynosure.fabric.v1211.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.items.extensions.CustomFurnaceFuel;
import dev.mayaqq.cynosure.items.extensions.ItemExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// Mixin check: should work

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Shadow @Nullable
    public abstract RecipeHolder<?> getRecipeUsed();

    @ModifyReturnValue(
        method = "isFuel",
        at = @At("RETURN")
    )
    private static boolean hookFuel(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
        return original
            || itemStack.getItem() instanceof CustomFurnaceFuel
            || ItemExtension.Registry.getExtension(CustomFurnaceFuel.class, itemStack.getItem()) != null;
    }

    @ModifyReturnValue(
        method = "getBurnDuration",
        at = @At("RETURN")
    )
    private int hookFuelTime(int original, @Local(argsOnly = true) ItemStack itemStack) {
        CustomFurnaceFuel fuel = ItemExtension.Registry.getExtension(CustomFurnaceFuel.class, itemStack.getItem());
        RecipeHolder<?> recipe = getRecipeUsed();
        if (fuel != null) return fuel.getItemBurnTime(itemStack, recipe != null ? recipe.value().getType() : null);
        return original;
    }
}
