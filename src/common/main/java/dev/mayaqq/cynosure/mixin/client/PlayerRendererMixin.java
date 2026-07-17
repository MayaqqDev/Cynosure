package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.client.models.poses.ArmPoseHandling;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(
            method = "setModelProperties",
            at = @At("TAIL")
    )
    public void injectCynosureArmPose(AbstractClientPlayer player, CallbackInfo ci, @Local PlayerModel<AbstractClientPlayer> model) {
        if (!player.isSpectator()) {
            ArmPoseHandling.addPropertiesToModel(player, model);
        }
    }
}
