package dev.mayaqq.cynosure.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.items.extensions.BarterCurrency;
import dev.mayaqq.cynosure.items.extensions.ItemExtension;
import dev.mayaqq.cynosure.items.extensions.PiglinNeutralizing;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {

    @ModifyReturnValue(
        method = "isBarterCurrency",
        at = @At("RETURN")
    )
    private static boolean customPiglinCurrency(boolean original, @Local(argsOnly = true) ItemStack stack) {
        BarterCurrency currency;
        return original
            || (((currency = ItemExtension.Registry.getExtension(BarterCurrency.class, stack.getItem())) != null)
            && currency.isBarterCurrency(stack));
    }

    @ModifyExpressionValue(
        method = "isWearingGold",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ArmorItem;getMaterial()Lnet/minecraft/world/item/ArmorMaterial;"
        )
    )
    // Currently just returning ArmorMaterials.GOLD until new mixinextras gets added to fabric
    private static ArmorMaterial returnGoldIfNeutralizing(ArmorMaterial original, @Local ItemStack stack, @Local(argsOnly = true) LivingEntity entity) {
        PiglinNeutralizing neutralizing = ItemExtension.Registry.getExtension(PiglinNeutralizing.class, stack.getItem());
        if (neutralizing != null) return (neutralizing.makesPiglinsNeutral(entity, stack)) ? ArmorMaterials.GOLD : original;
        return original;
    }
}
