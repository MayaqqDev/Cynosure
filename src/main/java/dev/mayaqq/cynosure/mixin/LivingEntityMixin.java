package dev.mayaqq.cynosure.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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
        mainBus.post(event, this, null);
        return event.getResult() == null ? source : event.getResult();
    }

    @ModifyVariable(
            method = "hurt",
            at = @At(value = "HEAD"),
            index = 2,
            argsOnly = true
    )
    private float modifyFallDamage(float damage, DamageSource source) {
        EventBus mainBus = MainBus.INSTANCE;
        EntityDamageEvent event = new EntityDamageEvent((LivingEntity) (Object) this, source, damage);
        mainBus.post(event, null, null);
        return event.getResult() == null ? damage : event.getResult();
    }
}
