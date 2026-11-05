package dev.mayaqq.cynosure.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mayaqq.cynosure.client.models.poses.ArmPoseHandling;
import kotlin.Unit;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemInHandRenderer.class, priority = 2000)
public abstract class ItemInHandRendererMixin {
    @Shadow
    protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float v);

    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm humanoidArm, float v);

    @Inject(
            method = "renderArmWithItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectCustomAnimation(
            AbstractClientPlayer player,
            float partialTicks,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float equippedProgress,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            CallbackInfo ci
    ) {
        boolean flag = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = flag ? player.getMainArm() : player.getMainArm().getOpposite();
        if (ArmPoseHandling.modifyItemInHand(
                player,
                poseStack,
                buffer,
                partialTicks,
                hand,
                arm,
                stack,
                combinedLight,
                pitch,
                swingProgress,
                equippedProgress,
                () -> {
                    this.applyItemArmTransform(poseStack, arm, equippedProgress);
                    return Unit.INSTANCE;
                },
                () -> {
                    this.applyItemArmAttackTransform(poseStack, arm, swingProgress);
                    return Unit.INSTANCE;
                }
        )) ci.cancel();
    }
}
