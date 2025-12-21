package dev.mayaqq.cynosure.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EffectInstance.class, priority = 0)
public class EffectInstanceMixin {

    @WrapOperation(
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
                    ordinal = 0
            ),
            method = "<init>",
            require = 0
    )
    ResourceLocation constructProgramResourceLocation(String arg, Operation<ResourceLocation> original, ResourceManager unused, String id) {
        if (!id.contains(":")) {
            return original.call(arg);
        }
        ResourceLocation split = new ResourceLocation(id);
        return new ResourceLocation(split.getNamespace(), "shaders/program/" + split.getPath() + ".json");
    }

    @WrapOperation(
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
                    ordinal = 0
            ),
            method = "getOrCreate",
            require = 0
    )
    private static ResourceLocation constructProgramResourceLocation(String arg, Operation<ResourceLocation> original, ResourceManager unused, Program.Type type, String id) {
        if (!arg.contains(":")) {
            return original.call(arg);
        }
        ResourceLocation split = new ResourceLocation(id);
        return new ResourceLocation(split.getNamespace(), "shaders/program/" + split.getPath() + type.getExtension());
    }
}
