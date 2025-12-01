package dev.mayaqq.cynosure.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.mayaqq.cynosure.client.render.BufferOutputStage;
import dev.mayaqq.cynosure.client.render.RenderTypeRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
public class LevelRendererMixin {

    @Inject(
        method = "renderLevel",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/RenderType;entitySmoothCutout(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"),
            to = @At(
                value = "INVOKE_STRING",
                target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                args = "ldc=blockentities"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;)V",
            shift = At.Shift.AFTER,
            ordinal = 0
        )
    )
    public void flushEntitiesPhase(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local MultiBufferSource.BufferSource bufferSource) {
        for(RenderType renderType : RenderTypeRegistry.PHASES.get(BufferOutputStage.ENTITY)) {
            bufferSource.endBatch(renderType);
        }
    }

    @Inject(
        method = "renderLevel",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/Sheets;chestSheet()Lnet/minecraft/client/renderer/RenderType;"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V")
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;)V",
            shift = At.Shift.AFTER,
            ordinal = 0
        )
    )

    public void flushSolidPhase(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local MultiBufferSource.BufferSource bufferSource) {
        for(RenderType renderType : RenderTypeRegistry.PHASES.get(BufferOutputStage.BLOCK_ENTITY)) {
            bufferSource.endBatch(renderType);
        }
    }

    @Inject(
        method = "renderLevel",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/RenderType;waterMask()Lnet/minecraft/client/renderer/RenderType;"
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/renderer/LevelRenderer;transparencyChain:Lnet/minecraft/client/renderer/PostChain;",
                opcode = Opcodes.GETFIELD
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;)V",
            shift = At.Shift.AFTER,
            ordinal = 0
        )
    )
    public void flushTranslucentPhase(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local MultiBufferSource.BufferSource bufferSource) {
        for(RenderType renderType : RenderTypeRegistry.PHASES.get(BufferOutputStage.TRANSLUCENT)) {
            bufferSource.endBatch(renderType);
        }
    }
}
