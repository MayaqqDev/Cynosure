package dev.mayaqq.cynosure.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.cynosure.effects.Effextras;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

// Mixin check: should work

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "tickEffects()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;remove()V"
            )
    )
    private void onEffectExpire(CallbackInfo ci, @Local MobEffectInstance instance) {
        MainBus.INSTANCE.post(new LivingEntityEvent.EffectExpire((LivingEntity) (Object) this, instance, Effextras.effect(instance)));
    }

    @WrapOperation(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object onEffectAdd(Map map, Object o, Operation<Object> original, @Local(argsOnly = true) MobEffectInstance instance) {
        var oldInstance = (MobEffectInstance) original.call(map, o);
        MainBus.INSTANCE.post(new LivingEntityEvent.EffectApply((LivingEntity) (Object) this, instance, oldInstance, Effextras.effect(instance)));
        return oldInstance;
    }
}
