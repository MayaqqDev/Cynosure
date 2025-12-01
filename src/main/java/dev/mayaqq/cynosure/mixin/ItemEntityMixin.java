package dev.mayaqq.cynosure.mixin;

import dev.mayaqq.cynosure.items.extensions.ItemEntityTickListener;
import dev.mayaqq.cynosure.items.extensions.ItemExtension;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow public abstract ItemStack getItem();

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        cancellable = true
    )
    public void customItemTick(CallbackInfo ci) {
        ItemStack stack = this.getItem();
        ItemEntityTickListener listener = ItemExtension.Registry.getExtension(ItemEntityTickListener.class, stack.getItem());
        if (listener != null) {
            if (listener.tickItemEntity(stack, (ItemEntity) (Object) this)) ci.cancel();
        }
    }
}
