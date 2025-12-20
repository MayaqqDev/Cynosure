package dev.mayaqq.cynosure.fabric.mixin.client;

import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EffectInstance.class, priority = 0)
public class EffectInstanceMixin {


    @Redirect(
        method = "<init>",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
        ),
        require = 0
    )
    private ResourceLocation redirectCreateShaderLocation(String originalInput, ResourceManager manager, String string) {
        String[] parts = string.split(":");
        if (parts.length > 1) return new ResourceLocation(parts[0] + ":shaders/program/" + parts[1] + ".json");
        return new ResourceLocation(originalInput);
    }

    @Redirect(
        method = "getOrCreate",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
        ),
        require = 0
    )
    private static ResourceLocation redirectCreateResourceLocation(String originalInput, ResourceManager manager, Program.Type type, String string) {
        String[] parts = string.split(":");
        if (parts.length > 1) return new ResourceLocation(parts[0] + ":shaders/program/" + parts[1] + type.getExtension());
        return new ResourceLocation(originalInput);
    }
}
