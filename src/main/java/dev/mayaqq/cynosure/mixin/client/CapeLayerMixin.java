package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.items.extensions.DisablesCape;
import dev.mayaqq.cynosure.items.extensions.ItemExtension;
import dev.mayaqq.cynosure.tags.FunctionalTags;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {
    @WrapOperation(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private boolean onRenderCape(ItemStack instance, Item item, Operation<Boolean> original, @Local(argsOnly = true) AbstractClientPlayer player) {
        DisablesCape disablesCape = ItemExtension.Registry.getExtension(DisablesCape.class, item);
        if (disablesCape != null) {
            return disablesCape.disablesCape(instance, player);
        } else if (instance.is(FunctionalTags.DISABLES_CAPE)) {
            return true;
        } else {
            return original.call(instance, item);
        }
    }
}
