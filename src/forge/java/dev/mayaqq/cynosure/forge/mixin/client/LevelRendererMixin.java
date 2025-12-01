package dev.mayaqq.cynosure.forge.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.mayaqq.cynosure.client.events.CynosureForgeClientEventsKt;
import dev.mayaqq.cynosure.client.events.render.LevelRenderEvent;
import dev.mayaqq.cynosure.client.events.render.ReloadLevelRendererEvent;
import dev.mayaqq.cynosure.events.api.MainBus;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow @Nullable private ClientLevel level;

    @Shadow @Final private RenderBuffers renderBuffers;

    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    private void onBeginWorldRender(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        var event = new LevelRenderEvent.Start(Objects.requireNonNull(level), (LevelRenderer) (Object) this, poseStack, partialTick, camera, null, null);
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
    private void onSetupRender(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local Frustum frustum) {
        var event = new LevelRenderEvent.BeforeTerrain(Objects.requireNonNull(level), (LevelRenderer) (Object) this, poseStack, partialTick, camera, frustum, renderBuffers.bufferSource());
        MainBus.INSTANCE.post(event, null, null);
        CynosureForgeClientEventsKt.setCapturedFrustum(frustum);
    }

    @Inject(
        method = "allChanged",
        at = @At("HEAD")
    )
    private void onChanged(CallbackInfo ci) {
        MainBus.INSTANCE.post(ReloadLevelRendererEvent.INSTANCE, null, null);
    }
}
