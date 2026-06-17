package dev.mayaqq.cynosure.fabric.v1201.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mayaqq.cynosure.events.api.MainBus;
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapMethod(
            method = "removeEffect"
    )
    private boolean onRemove(MobEffect effect, Operation<Boolean> original) {
        if (MainBus.INSTANCE.post(new LivingEntityEvent.EffectRemove((LivingEntity) (Object) this, null, effect))) {
            return false;
        } else {
            return original.call(effect);
        }
    }
}
