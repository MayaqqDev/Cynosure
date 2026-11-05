package dev.mayaqq.cynosure.client.models.poses

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

public abstract class CynosureArmPose(public val twohanded: Boolean = false, public val cancelSwing: Boolean = false) {
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyMainArm(entity: LivingEntity, arm: ModelPart, ageInTicks: Float)
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyOffhandArm(entity: LivingEntity, arm: ModelPart, ageInTicks: Float)
    public abstract fun modifyInHand(player: AbstractClientPlayer,
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
    ): Boolean
}

public abstract class CynosureArmAnimation(public val twohanded: Boolean = false, public val cancelSwing: Boolean = false) {
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyMainArm(entity: LivingEntity, arm: ModelPart, ageInTicks: Float)
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyOffhandArm(entity: LivingEntity, arm: ModelPart, ageInTicks: Float)
    public abstract fun modifyInHand(player: AbstractClientPlayer,
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
    ): Boolean
}