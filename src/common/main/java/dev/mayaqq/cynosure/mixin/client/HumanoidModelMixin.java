package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.mayaqq.cynosure.client.models.poses.CynosureArmAnimation;
import dev.mayaqq.cynosure.client.models.poses.CynosureArmPose;
import dev.mayaqq.cynosure.injection.client.IHumanoidModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> implements IHumanoidModel {

    @Shadow
    @Final
    public ModelPart rightArm;
    @Shadow
    @Final
    public ModelPart leftArm;
    @Shadow
    public boolean crouching;
    @Unique
    CynosureArmPose cynosure$customRightArmPose = null;

    @Unique
    CynosureArmPose cynosure$customLeftArmPose = null;

    @Unique
    CynosureArmAnimation cynosure$customRightArmAnimation = null;

    @Unique
    CynosureArmAnimation cynosure$customLeftArmAnimation = null;

    @Override
    public @Nullable CynosureArmPose getCynosure$customRightArmPose() {
        return cynosure$customRightArmPose;
    }

    @Override
    public @Nullable CynosureArmPose getCynosure$customLeftArmPose() {
        return cynosure$customLeftArmPose;
    }

    @Override
    public void setCynosure$customRightArmPose(@Nullable CynosureArmPose customArmPose) {
        cynosure$customRightArmPose = customArmPose;
    }

    @Override
    public void setCynosure$customLeftArmPose(@Nullable CynosureArmPose customArmPose) {
        cynosure$customLeftArmPose = customArmPose;
    }

    @Override
    public @Nullable CynosureArmAnimation getCynosure$customRightArmAnimation() {
        return cynosure$customRightArmAnimation;
    }

    @Override
    public void setCynosure$customRightArmAnimation(@Nullable CynosureArmAnimation cynosureArmAnimation) {
        cynosure$customRightArmAnimation = cynosureArmAnimation;
    }

    @Override
    public @Nullable CynosureArmAnimation getCynosure$customLeftArmAnimation() {
        return cynosure$customLeftArmAnimation;
    }

    @Override
    public void setCynosure$customLeftArmAnimation(@Nullable CynosureArmAnimation cynosureArmAnimation) {
        cynosure$customLeftArmAnimation = cynosureArmAnimation;
    }


    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isUsingItem()Z"
            )
    )
    private void injectOnAnimation(
            T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
            CallbackInfo ci,
            @Local LocalRef<Float> rightHandYRot, @Local LocalRef<Float> leftHandYRot
    ) {
        boolean isMainRight = entity.getMainArm() == HumanoidArm.RIGHT;
        HumanoidModel<? extends LivingEntity> model = (HumanoidModel<? extends LivingEntity>) (Object) this;
        if (entity.isUsingItem()) {
            boolean isUsedMain = entity.getUsedItemHand() == InteractionHand.MAIN_HAND;

            if (isMainRight == isUsedMain) {
                if (this.cynosure$customRightArmAnimation != null) {
                    this.cynosure$customRightArmAnimation.modifyMainArm(model, entity, this.rightArm, ageInTicks);
                    float mainYRot = this.rightArm.yRot;
                    rightHandYRot.set(mainYRot);
                    if (this.cynosure$customRightArmAnimation.getTwohanded()) {
                        this.cynosure$customRightArmAnimation.modifyOffhandArm(model, entity, this.leftArm, ageInTicks);
                        float offhandArmYRot = this.leftArm.yRot;
                        leftHandYRot.set(offhandArmYRot);
                    }
                }
            } else {
                if (this.cynosure$customLeftArmAnimation != null) {
                    this.cynosure$customLeftArmAnimation.modifyMainArm(model, entity, this.leftArm, ageInTicks);
                    float mainYRot = this.leftArm.yRot;
                    leftHandYRot.set(mainYRot);
                    if (this.cynosure$customLeftArmAnimation.getTwohanded()) {
                        float offhandArmYRot = this.rightArm.yRot;
                        rightHandYRot.set(offhandArmYRot);
                        this.cynosure$customLeftArmAnimation.modifyOffhandArm(model, entity, this.rightArm, ageInTicks);
                    }
                }
            }
        } else {
            if (isMainRight) {
                if (cynosure$customRightArmPose != null) {
                    this.cynosure$customRightArmPose.modifyMainArm(model, entity, this.rightArm, ageInTicks);
                    float mainYRot = this.rightArm.yRot;
                    rightHandYRot.set(mainYRot);
                }
                if (cynosure$customLeftArmPose != null) {
                    this.cynosure$customLeftArmPose.modifyOffhandArm(model, entity, this.leftArm, ageInTicks);
                    float offhandArmYRot = this.leftArm.yRot;
                    leftHandYRot.set(offhandArmYRot);
                }
            } else {
                if (cynosure$customLeftArmPose != null) {
                    this.cynosure$customLeftArmPose.modifyMainArm(model, entity, this.leftArm, ageInTicks);
                    float mainYRot = this.leftArm.yRot;
                    leftHandYRot.set(mainYRot);
                }
                if (cynosure$customRightArmPose != null) {
                    this.cynosure$customRightArmPose.modifyOffhandArm(model, entity, this.rightArm, ageInTicks);
                    float offhandArmYRot = this.rightArm.yRot;
                    rightHandYRot.set(offhandArmYRot);
                }
            }
        }
    }

    @Definition(id = "rightArmPose", field = "Lnet/minecraft/client/model/HumanoidModel;rightArmPose:Lnet/minecraft/client/model/HumanoidModel$ArmPose;")
    @Definition(id = "SPYGLASS", field = "Lnet/minecraft/client/model/HumanoidModel$ArmPose;SPYGLASS:Lnet/minecraft/client/model/HumanoidModel$ArmPose;")
    @Expression("this.rightArmPose != SPYGLASS")
    @ModifyExpressionValue(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean cancelWhenCustomRightArm(boolean original, @Local Float rightHandYRot, @Local Float leftHandYRot) {
        if (rightHandYRot != null) {
            this.rightArm.yRot += rightHandYRot;
        }
        if (leftHandYRot != null) {
            this.leftArm.yRot += leftHandYRot;
        }

        if (cynosure$customRightArmPose != null && cynosure$customRightArmPose.getCancelSwing()) return false;
        if (cynosure$customRightArmAnimation != null && cynosure$customRightArmAnimation.getCancelSwing()) return false;

        return original;
    }

    @Definition(id = "leftArmPose", field = "Lnet/minecraft/client/model/HumanoidModel;leftArmPose:Lnet/minecraft/client/model/HumanoidModel$ArmPose;")
    @Definition(id = "SPYGLASS", field = "Lnet/minecraft/client/model/HumanoidModel$ArmPose;SPYGLASS:Lnet/minecraft/client/model/HumanoidModel$ArmPose;")
    @Expression("this.leftArmPose != SPYGLASS")
    @ModifyExpressionValue(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean cancelWhenCustomLeftArm(boolean original) {
        if (cynosure$customLeftArmPose != null && cynosure$customLeftArmPose.getCancelSwing()) return false;
        if (cynosure$customLeftArmAnimation != null && cynosure$customLeftArmAnimation.getCancelSwing()) return false;

        return original;
    }
}
