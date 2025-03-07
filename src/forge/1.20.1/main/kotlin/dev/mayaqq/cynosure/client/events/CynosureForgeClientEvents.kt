@file:EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
package dev.mayaqq.cynosure.client.events

import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.events.render.WorldRenderEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.forge.mixin.LevelRendererAccessor
import net.minecraft.client.renderer.LevelRenderer
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

internal val LevelRenderer.renderBuffers get() = (this as LevelRendererAccessor).renderBuffers
internal val RenderLevelStageEvent.bufferSource get() = this.levelRenderer.renderBuffers.bufferSource()

@SubscribeEvent
public fun onLevelRender(event: RenderLevelStageEvent) {
    when(event.stage) {
        RenderLevelStageEvent.Stage.AFTER_SKY -> WorldRenderEvent.BeforeTerrain(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS -> WorldRenderEvent.AfterTerrain(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_ENTITIES -> WorldRenderEvent.AfterTerrain(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, event.bufferSource
        ).post()
        RenderLevelStageEvent.Stage.AFTER_PARTICLES -> WorldRenderEvent.AfterParticles(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, null
        ).post()
        RenderLevelStageEvent.Stage.AFTER_WEATHER -> WorldRenderEvent.Last(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, event.frustum, null
        ).post()
        RenderLevelStageEvent.Stage.AFTER_LEVEL -> WorldRenderEvent.End(
            event.levelRenderer, event.poseStack, event.partialTick,
            event.camera, null, null
        ).post()
    }
}