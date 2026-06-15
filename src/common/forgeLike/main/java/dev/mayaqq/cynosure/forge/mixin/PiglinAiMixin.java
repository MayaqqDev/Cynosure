package dev.mayaqq.cynosure.forge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.items.extensions.BarterCurrency;
import dev.mayaqq.cynosure.items.extensions.ItemExtension;
import dev.mayaqq.cynosure.items.extensions.PiglinNeutralizing;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @ModifyExpressionValue(
        method = "isWearingGold",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;makesPiglinsNeutral(Lnet/minecraft/world/entity/LivingEntity;)Z"
        )
    )
    private static boolean makesPiglinsNeutralExtended(boolean original, @Local ItemStack stack, @Local Item item, @Local LivingEntity entity) {
        PiglinNeutralizing extension = ItemExtension.Registry.getExtension(PiglinNeutralizing.class, item);
        if (extension != null) {
            return extension.makesPiglinsNeutral(entity, stack);
        }
        return original;
    }

    @ModifyExpressionValue(
        method = {"canAdmire", "wantsToPickup", "stopHoldingOffHandItem"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isPiglinCurrency()Z"
        )
    )
    private static boolean customBarterCurrency(boolean original, @Local ItemStack stack) {
        BarterCurrency extension = ItemExtension.Registry.getExtension(BarterCurrency.class, stack.getItem());
        if (extension != null) return extension.isBarterCurrency(stack);
        return original;
    }

    @ModifyReturnValue(
        method = "isBarterCurrency",
        at = @At("RETURN")
    )
    private static boolean customBarterCurrency1(boolean original, @Local(argsOnly = true) ItemStack stack) {
        BarterCurrency extension = ItemExtension.Registry.getExtension(BarterCurrency.class, stack.getItem());
        if (extension != null) return extension.isBarterCurrency(stack);
        return original;
    }
}
