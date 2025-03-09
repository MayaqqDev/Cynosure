package dev.mayaqq.cynosure.forge.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.mayaqq.cynosure.client.events.CynosureForgeClientEventsKt;
import dev.mayaqq.cynosure.client.events.render.LevelRenderEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    public void onBeginWorldRender(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        var event = new LevelRenderEvent.Start((LevelRenderer) (Object) this, poseStack, partialTick, camera, null, null);
        MainBus.INSTANCE.post(event, null, null);
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V",
            shift = At.Shift.AFTER
        )
    )
    public void onSetupRender(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local Frustum frustum, @Local MultiBufferSource bufferSource) {
        var event = new LevelRenderEvent.BeforeTerrain((LevelRenderer) (Object) this, poseStack, partialTick, camera, frustum, bufferSource);
        MainBus.INSTANCE.post(event, null, null);
        CynosureForgeClientEventsKt.setCapturedFrustum(frustum);
    }
}
