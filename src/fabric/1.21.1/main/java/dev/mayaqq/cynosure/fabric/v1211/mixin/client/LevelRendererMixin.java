package dev.mayaqq.cynosure.fabric.v1211.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
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

// Mixin check: TODO: It does error but like it should work??

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Nullable private ClientLevel level;

    @Shadow @Final private RenderBuffers renderBuffers;

    @Definition(id = "renderSectionLayer", method = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V")
    @Definition(id = "tripwire", method = "Lnet/minecraft/client/renderer/RenderType;tripwire()Lnet/minecraft/client/renderer/RenderType;")
    @Expression("?.renderSectionLayer(tripwire(), ?, ?, ?, ?, ?)")
    @Inject(
            method = "renderLevel",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER, ordinal = 0)
    )
    private void onRenderTripwire1(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local PoseStack stack) {
        //TODO: maybe the poststack should be poststack.last()
        CynosureWorldRenderEventHandler.INSTANCE.afterTranslucentTerrain(Objects.requireNonNull(level), (LevelRenderer) (Object) this, stack, deltaTracker.getGameTimeDeltaPartialTick(false), camera, renderBuffers.bufferSource());
    }

    @Definition(id = "renderSectionLayer", method = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V")
    @Definition(id = "tripwire", method = "Lnet/minecraft/client/renderer/RenderType;tripwire()Lnet/minecraft/client/renderer/RenderType;")
    @Expression("?.renderSectionLayer(tripwire(), ?, ?, ?, ?, ?)")
    @Inject(
            method = "renderLevel",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER, ordinal = 1)
    )
    private void onRenderTripwire2(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local PoseStack stack) {
        //TODO: maybe the poststack should be poststack.last()
        CynosureWorldRenderEventHandler.INSTANCE.afterTranslucentTerrain(Objects.requireNonNull(level), (LevelRenderer) (Object) this, stack, deltaTracker.getGameTimeDeltaPartialTick(false), camera, renderBuffers.bufferSource());
    }
}
