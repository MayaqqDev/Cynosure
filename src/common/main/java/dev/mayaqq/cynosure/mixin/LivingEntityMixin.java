package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.mayaqq.cynosure.effects.Effextras;
import dev.mayaqq.cynosure.events.api.EventBus;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.entity.EntityDamageEvent;
import dev.mayaqq.cynosure.events.entity.EntityDamageSourceEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapWithCondition(
            method = "onEffectUpdated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/attributes/AttributeMap;I)V"
            )
    )
    public boolean onEffectUpdated(MobEffect effect, LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        // Mojang calls remove and then add when updating an effect we do not want this for the Girl Power effect.r
        return !Effextras.getUpdateless(effect);
    }

    @ModifyVariable(
            method = "hurt",
            at = @At(value = "HEAD"),
            index = 1,
            argsOnly = true
    )
    private DamageSource modifyDamageSource(DamageSource source) {
        EventBus mainBus = MainBus.INSTANCE;
        EntityDamageSourceEvent event = new EntityDamageSourceEvent((LivingEntity) (Object) this, source);
        mainBus.post(event);
        return event.getResult() == null ? source : event.getResult();
    }

    @Inject(
            method = "hurt",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyFallDamage(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalFloatRef amount, @Local(argsOnly = true) DamageSource source) {
        EntityDamageEvent event = new EntityDamageEvent((LivingEntity) (Object) this, source, amount.get());
        MainBus.INSTANCE.post(event);
        var result = event.getResult();
        if (result != null) {
            if (result == 0.0F) cir.setReturnValue(false);
            else amount.set(result);
        }
    }
}
