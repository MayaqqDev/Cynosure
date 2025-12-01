package dev.mayaqq.cynosure.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.items.extensions.CustomFurnaceFuel;
import dev.mayaqq.cynosure.items.extensions.ItemExtension;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FuelRegistryImpl.class)
public class FuelRegistryMixin {

    @ModifyReturnValue(
        method = "get(Lnet/minecraft/world/level/ItemLike;)Ljava/lang/Integer;",
        at = @At("RETURN")
    )
    private Integer changeReturnValue(Integer original, @Local(argsOnly = true) ItemLike itemLike) {
        CustomFurnaceFuel fuel;
        return (fuel = ItemExtension.Registry.getExtension(CustomFurnaceFuel.class, itemLike.asItem())) != null
            ? fuel.getItemBurnTime(itemLike.asItem().getDefaultInstance(), null) : original;
    }
}
