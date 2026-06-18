package dev.mayaqq.cynosure.fabric.v1211.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
                    ordinal = 0
            ),
            method = "<init>",
            require = 0
    )
    ResourceLocation constructProgramResourceLocation(String arg, Operation<ResourceLocation> original, @Local(argsOnly = true) String id) {
        if (!id.contains(":")) return original.call(arg);
        ResourceLocation split = ResourceLocation.parse(id);
        return ResourceLocation.fromNamespaceAndPath(split.getNamespace(), "shaders/program/" + split.getPath() + ".json");
    }

    @WrapOperation(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
                    ordinal = 0
            ),
            method = "getOrCreate",
            require = 0
    )
    private static ResourceLocation getOrCreateProgramResourceLocation(String arg, Operation<ResourceLocation> original, @Local(argsOnly = true) String id, @Local(argsOnly = true) Program.Type type) {
        if (!arg.contains(":")) return original.call(arg);
        ResourceLocation split = ResourceLocation.parse(id);
        return ResourceLocation.fromNamespaceAndPath(split.getNamespace(), "shaders/program/" + split.getPath() + type.getExtension());
    }
}
