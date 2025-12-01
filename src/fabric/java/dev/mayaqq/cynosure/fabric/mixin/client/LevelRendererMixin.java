package dev.mayaqq.cynosure.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Nullable private ClientLevel level;

    @Shadow @Final private RenderBuffers renderBuffers;

    @Inject(
        method = "renderLevel",
        slice = @Slice(
                from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;tripwire()Lnet/minecraft/client/renderer/RenderType;"
                ),
                to = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    args = "ldc=particles"
                )
            ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onRenderTripwire1(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        CynosureWorldRenderEventHandler.INSTANCE.afterTranslucentTerrain(Objects.requireNonNull(level), (LevelRenderer) (Object) this, poseStack, partialTick, camera, renderBuffers.bufferSource());
    }

    @Inject(
        method = "renderLevel",
        slice = @Slice(
            from = @At(
                value = "INVOKE:LAST",
                target = "Lnet/minecraft/client/renderer/RenderType;tripwire()Lnet/minecraft/client/renderer/RenderType;"
            ),
            to = @At(
                value = "INVOKE_STRING:LAST",
                target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                args = "ldc=particles"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onRenderTripwire2(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        CynosureWorldRenderEventHandler.INSTANCE.afterTranslucentTerrain(Objects.requireNonNull(level), (LevelRenderer) (Object) this, poseStack, partialTick, camera, renderBuffers.bufferSource());
    }
}
