package dev.mayaqq.cynosure.mixin.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.mayaqq.cynosure.client.events.ParticleRenderTypeRegistrationEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ParticleEngine.class, priority = 1200)
public class ParticleEngineMixin {

    @ModifyExpressionValue(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"
        )
    )
    private static ImmutableList<ParticleRenderType> modifyArray(ImmutableList<ParticleRenderType> original) {
        List<ParticleRenderType> renderTypes = new ArrayList<>(original);
        ParticleRenderTypeRegistrationEvent event = new ParticleRenderTypeRegistrationEvent(renderTypes);
        MainBus.INSTANCE.post(event, null, null);
        return ImmutableList.copyOf(renderTypes);
    }
}
