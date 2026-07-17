package dev.mayaqq.cynosure.injection.client

import dev.mayaqq.cynosure.client.models.poses.CynosureArmAnimation
import dev.mayaqq.cynosure.client.models.poses.CynosureArmPose
import net.minecraft.client.model.HumanoidModel
import net.minecraft.world.entity.LivingEntity

public interface IHumanoidModel {
    public var `cynosure$customRightArmPose`: CynosureArmPose?
    public var `cynosure$customLeftArmPose`: CynosureArmPose?

    public var `cynosure$customRightArmAnimation`: CynosureArmAnimation?
    public var `cynosure$customLeftArmAnimation`: CynosureArmAnimation?
}

public var <T : LivingEntity> HumanoidModel<T>.customRightArmPose: CynosureArmPose?
    get() = (this as IHumanoidModel).`cynosure$customRightArmPose`
    set(value) {
        (this as IHumanoidModel).`cynosure$customRightArmPose` = value
    }

public var <T : LivingEntity> HumanoidModel<T>.customLeftArmPose: CynosureArmPose?
    get() = (this as IHumanoidModel).`cynosure$customLeftArmPose`
    set(value) {
        (this as IHumanoidModel).`cynosure$customLeftArmPose` = value
    }

public var <T : LivingEntity> HumanoidModel<T>.customRightArmAnimation: CynosureArmAnimation?
    get() = (this as IHumanoidModel).`cynosure$customRightArmAnimation`
    set(value) {
        (this as IHumanoidModel).`cynosure$customRightArmAnimation` = value
    }

public var <T : LivingEntity> HumanoidModel<T>.customLeftArmAnimation: CynosureArmAnimation?
    get() = (this as IHumanoidModel).`cynosure$customLeftArmAnimation`
    set(value) {
        (this as IHumanoidModel).`cynosure$customLeftArmAnimation` = value
    }