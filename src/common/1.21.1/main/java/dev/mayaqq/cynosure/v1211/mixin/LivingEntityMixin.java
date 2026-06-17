package dev.mayaqq.cynosure.v1211.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.mayaqq.cynosure.effects.Effextras;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapWithCondition(
            method = "onEffectUpdated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V"
            )
    )
    public boolean onEffectUpdated(MobEffect effect, AttributeMap attributeMap) {
        // Mojang calls remove and then add when updating an effect we do not want this for the Girl Power effect.r
        return !Effextras.getUpdateless(effect);
    }
}
