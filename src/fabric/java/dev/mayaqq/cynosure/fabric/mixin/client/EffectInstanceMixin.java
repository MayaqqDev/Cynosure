package dev.mayaqq.cynosure.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EffectInstance.class, priority = 949)
public class EffectInstanceMixin {


    @Redirect(
        method = "<init>",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
        ),
        require = 0
    )
    private ResourceLocation redirectCreateShaderLocation(String p_135809_, @Local(argsOnly = true) String string) {
        String[] parts = string.split(":");
        if (parts.length > 1) return new ResourceLocation(parts[0] + ":shaders/program/" + parts[1] + ".json");
        return new ResourceLocation(p_135809_);
    }

    @Redirect(
        method = "getOrCreate",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
        ),
        require = 0
    )
    private static ResourceLocation redirectCreateResourceLocation(String p_135809_, @Local(argsOnly = true) String string, @Local(argsOnly = true) Program.Type type) {
        String[] parts = string.split(":");
        if (parts.length > 1) return new ResourceLocation(parts[0] + ":shaders/program/" + parts[1] + type.getExtension());
        return new ResourceLocation(p_135809_);
    }
}
