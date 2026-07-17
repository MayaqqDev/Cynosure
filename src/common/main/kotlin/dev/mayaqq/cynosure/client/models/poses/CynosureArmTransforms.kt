package dev.mayaqq.cynosure.client.models.poses

import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.LivingEntity

public abstract class CynosureArmPose(public val twohanded: Boolean) {
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyMainArm(entity: LivingEntity, arm: ModelPart)
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyOffhandArm(entity: LivingEntity, arm: ModelPart)
}

public abstract class CynosureArmAnimation(public val twohanded: Boolean) {
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyMainArm(entity: LivingEntity, arm: ModelPart)
    public abstract fun <T : LivingEntity> HumanoidModel<T>.modifyOffhandArm(entity: LivingEntity, arm: ModelPart)
}