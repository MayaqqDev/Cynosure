package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.client.events.tooltip.ModifyTooltipComponentsEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.items.extensions.CustomTooltip;
import dev.mayaqq.cynosure.items.extensions.ItemExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {


    @Shadow public abstract Item getItem();

    @Inject(
        method = "getTooltipLines",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/TooltipFlag;isAdvanced()Z"
        )
    )
    private void addCustomLines(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        CustomTooltip extension = ItemExtension.Registry.getExtension(CustomTooltip.class, this.getItem());
        if (extension != null) {
            extension.modifyTooltip(list, (ItemStack) (Object) this, player, flag);
        }

        ModifyTooltipComponentsEvent event = new ModifyTooltipComponentsEvent((ItemStack) (Object) this, flag, player, list);
        MainBus.INSTANCE.post(event, null, null);
    }
}
