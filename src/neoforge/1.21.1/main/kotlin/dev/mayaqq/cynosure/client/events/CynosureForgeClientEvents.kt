
package dev.mayaqq.cynosure.client.events

import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.events.render.BeginHudRenderEvent
import dev.mayaqq.cynosure.client.events.render.EndHudRenderEvent
import dev.mayaqq.cynosure.client.events.render.LevelRenderEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.forge.mixin.client.LevelRendererAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.world.level.block.Blocks
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderGuiEvent
import net.neoforged.neoforge.client.event.RenderHighlightEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

internal val LevelRenderer.renderBuffers get() = (this as LevelRendererAccessor).renderBuffers
internal val RenderLevelStageEvent.bufferSource get() = (this.levelRenderer as LevelRendererAccessor).renderBuffers.bufferSource()
internal val LevelRenderer.level get() = (this as LevelRendererAccessor).level


internal var capturedFrustum: Frustum? = null

@EventBusSubscriber(value = [Dist.CLIENT], modid = MODID)
public object CynosureForgeClientEvents {
    @SubscribeEvent
    public fun onLevelRender(event: RenderLevelStageEvent) {
        when(event.stage) {
            RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS -> LevelRenderEvent.AfterTerrain(
                event.levelRenderer.level, event.levelRenderer, event.poseStack,
                event.partialTick.getGameTimeDeltaPartialTick(false), event.camera, event.frustum, event.bufferSource
            ).post()
            RenderLevelStageEvent.Stage.AFTER_ENTITIES -> LevelRenderEvent.AfterEntities(
                event.levelRenderer.level, event.levelRenderer, event.poseStack,
                event.partialTick.getGameTimeDeltaPartialTick(false), event.camera, event.frustum, event.bufferSource
            ).post()
            RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS -> LevelRenderEvent.AfterTranslucentTerrain(
                event.levelRenderer.level, event.levelRenderer, event.poseStack,
                event.partialTick.getGameTimeDeltaPartialTick(false), event.camera, event.frustum, event.bufferSource
            ).post()
            RenderLevelStageEvent.Stage.AFTER_PARTICLES -> LevelRenderEvent.AfterParticles(
                event.levelRenderer.level, event.levelRenderer, event.poseStack,
                event.partialTick.getGameTimeDeltaPartialTick(false), event.camera, event.frustum, event.bufferSource
            ).post()
            RenderLevelStageEvent.Stage.AFTER_WEATHER -> LevelRenderEvent.Last(
                event.levelRenderer.level, event.levelRenderer, event.poseStack,
                event.partialTick.getGameTimeDeltaPartialTick(false), event.camera, event.frustum, event.bufferSource
            ).post()
            RenderLevelStageEvent.Stage.AFTER_LEVEL -> LevelRenderEvent.End(
                event.levelRenderer.level, event.levelRenderer, event.poseStack,
                event.partialTick.getGameTimeDeltaPartialTick(false), event.camera, null, null
            ).post()
        }
    }

    @SubscribeEvent
    public fun onDrawHiglight(event: RenderHighlightEvent.Block) {
        val ev = LevelRenderEvent.BeforeBlockOutline(event.levelRenderer.level, event.levelRenderer, event.poseStack, event.deltaTracker.getGameTimeDeltaPartialTick(false),
            event.camera, capturedFrustum, event.multiBufferSource, event.target)
        ev.post()
        if(ev.renderOutline) {
            val entity = Minecraft.getInstance().cameraEntity
            val pos = event.target.blockPos
            val state = Minecraft.getInstance().level?.getBlockState(pos) ?: Blocks.AIR.defaultBlockState()
            val ev2 = LevelRenderEvent.BlockOutline(event.levelRenderer.level, event.levelRenderer, event.poseStack, event.deltaTracker.getGameTimeDeltaPartialTick(false),
                event.camera, capturedFrustum, event.multiBufferSource, entity!!, pos, state)
            ev2.post()
            event.isCanceled = !ev2.renderVanillaOutline
        } else event.isCanceled = true
    }

    @SubscribeEvent
    public fun onBeginRenderHud(event: RenderGuiEvent.Pre) {
        event.isCanceled = BeginHudRenderEvent(Minecraft.getInstance().gui, event.guiGraphics, event.partialTick.getGameTimeDeltaPartialTick(false)).post()
    }

    @SubscribeEvent
    public fun onEndRenderHud(event: RenderGuiEvent.Post) {
        EndHudRenderEvent(Minecraft.getInstance().gui, event.guiGraphics, event.partialTick.getGameTimeDeltaPartialTick(false)).post()
    }

    @SubscribeEvent
    public fun onClientTickStart(event: net.neoforged.neoforge.client.event.ClientTickEvent.Pre) {
        ClientTickEvent.Begin.post()
    }

    @SubscribeEvent
    public fun onClientTickEnd(event: net.neoforged.neoforge.client.event.ClientTickEvent.Post) {
        ClientTickEvent.End.post()
    }
}