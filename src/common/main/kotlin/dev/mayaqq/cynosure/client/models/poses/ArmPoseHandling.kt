package dev.mayaqq.cynosure.client.models.poses

import com.mojang.blaze3d.vertex.PoseStack
import dev.mayaqq.cynosure.client.utils.pushPop
import dev.mayaqq.cynosure.helpers.McClient
import dev.mayaqq.cynosure.injection.client.customLeftArmAnimation
import dev.mayaqq.cynosure.injection.client.customLeftArmPose
import dev.mayaqq.cynosure.injection.client.customRightArmAnimation
import dev.mayaqq.cynosure.injection.client.customRightArmPose
import dev.mayaqq.cynosure.items.extensions.CustomArmAnimation
import dev.mayaqq.cynosure.items.extensions.CustomArmPose
import dev.mayaqq.cynosure.items.extensions.getExtension
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

internal object ArmPoseHandling {
    @JvmStatic
    fun addPropertiesToModel(player: AbstractClientPlayer, model: PlayerModel<AbstractClientPlayer>) {
        val isMainRight = player.mainArm == HumanoidArm.RIGHT

        // m: mainhand, o: offhand
        val mStack = player.getItemInHand(InteractionHand.MAIN_HAND)
        val oStack = player.getItemInHand(InteractionHand.OFF_HAND)

        var mPose = mStack.item.getExtension<CustomArmPose>()?.getArmPose(mStack)
        var oPose = oStack.item.getExtension<CustomArmPose>()?.getArmPose(oStack)

        var mAnim = mStack.item.getExtension<CustomArmAnimation>()?.getArmAnimation(mStack)
        var oAnim = oStack.item.getExtension<CustomArmAnimation>()?.getArmAnimation(oStack)

        if (mPose?.twohanded == true) oPose = mPose else if (oPose?.twohanded == true) mPose = oPose
        if (mAnim?.twohanded == true) oAnim = mAnim else if (oAnim?.twohanded == true) mAnim = oAnim

        if (mPose != null || mAnim != null) {
            if (isMainRight) {
                model.rightArmPose = HumanoidModel.ArmPose.EMPTY
            } else model.leftArmPose = HumanoidModel.ArmPose.EMPTY
        }
        if (oPose != null || oAnim != null) {
            if (isMainRight) {
                model.leftArmPose = HumanoidModel.ArmPose.EMPTY
            } else model.rightArmPose = HumanoidModel.ArmPose.EMPTY
        }

        if (player.mainArm == HumanoidArm.RIGHT) {
            model.customRightArmPose = mPose
            model.customLeftArmPose = oPose

            model.customRightArmAnimation = mAnim
            model.customLeftArmAnimation = oAnim
        } else {
            model.customRightArmPose = oPose
            model.customLeftArmPose = mPose

            model.customRightArmAnimation = oAnim
            model.customLeftArmAnimation = mAnim
        }
    }

    @JvmStatic
    fun modifyItemInHand(
        player: AbstractClientPlayer,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        partial: Float,
        hand: InteractionHand,
        arm: HumanoidArm,
        stack: ItemStack,
        light: Int,
        pitch: Float,
        swing: Float,
        equippedProgress: Float,
        default: () -> Unit,
        attack: () -> Unit
    ): Boolean {
        val isRight = arm == HumanoidArm.RIGHT
        if (player.isUsingItem && player.useItemRemainingTicks > 0 && player.usedItemHand == hand) {
            poseStack.pushPop {
                val animation = stack.item.getExtension<CustomArmAnimation>()?.getArmAnimation(stack)?: return false
                if (!animation.modifyInHand(
                        player, poseStack, buffer, partial, hand, arm, stack, light, pitch, swing, equippedProgress, default, attack
                )) return false
                McClient.gameRenderer.itemInHandRenderer.renderItem(
                    player,
                    stack,
                    if (isRight) ItemDisplayContext.FIRST_PERSON_RIGHT_HAND else ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                    !isRight,
                    poseStack,
                    buffer,
                    light
                )

            }
            return true
        } else {
            poseStack.pushPop {
                val pose = stack.item.getExtension<CustomArmPose>()?.getArmPose(stack)?: return false
                if (!pose.modifyInHand(
                        player, poseStack, buffer, partial, hand, arm, stack, light, pitch, swing, equippedProgress, default, attack
                )) return false
                McClient.gameRenderer.itemInHandRenderer.renderItem(
                    player,
                    stack,
                    if (isRight) ItemDisplayContext.FIRST_PERSON_RIGHT_HAND else ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                    !isRight,
                    poseStack,
                    buffer,
                    light
                )

            }
            return true
        }
    }
}