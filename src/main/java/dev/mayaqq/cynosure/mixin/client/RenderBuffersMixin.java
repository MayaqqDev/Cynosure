package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.BufferBuilder;
import dev.mayaqq.cynosure.client.render.RenderTypeRegistry;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.SortedMap;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

    @ModifyExpressionValue(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Util;make(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;"
        )
    )
    public Object addCustomBuffers(Object original) {
        SortedMap<RenderType, BufferBuilder> value = (SortedMap<RenderType, BufferBuilder>) original;
        value.putAll(RenderTypeRegistry.TYPES);
        RenderTypeRegistry.setFrozen(true);
        return original;
    }
}
