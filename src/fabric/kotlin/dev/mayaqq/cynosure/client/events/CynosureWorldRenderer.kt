package dev.mayaqq.cynosure.client.events

import com.mojang.blaze3d.vertex.PoseStack
import dev.mayaqq.cynosure.client.events.render.LevelRenderEvent
import dev.mayaqq.cynosure.client.events.render.ReloadLevelRendererEvent
import dev.mayaqq.cynosure.events.api.post
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.world.phys.HitResult

internal object CynosureWorldRenderEventHandler : WorldRenderEvents.Start, WorldRenderEvents.AfterSetup, WorldRenderEvents.BeforeEntities,
    WorldRenderEvents.AfterEntities, WorldRenderEvents.BeforeBlockOutline, WorldRenderEvents.BlockOutline,
    WorldRenderEvents.DebugRender, WorldRenderEvents.AfterTranslucent, WorldRenderEvents.Last, WorldRenderEvents.End,
        InvalidateRenderStateCallback
{

    var frustum: Frustum? = null

    fun init() {
        WorldRenderEvents.START.register(this)
        WorldRenderEvents.AFTER_SETUP.register(this)
        WorldRenderEvents.BEFORE_ENTITIES.register(this)
        WorldRenderEvents.AFTER_ENTITIES.register(this)
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(this)
        WorldRenderEvents.BLOCK_OUTLINE.register(this)
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(this)
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this)
        WorldRenderEvents.LAST.register(this)
        WorldRenderEvents.END.register(this)
        InvalidateRenderStateCallback.EVENT.register(this)
    }

    override fun onStart(context: WorldRenderContext) {
        LevelRenderEvent.Start(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
    }

    override fun afterSetup(context: WorldRenderContext) {
        LevelRenderEvent.BeforeTerrain(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
        this.frustum = context.frustum()
    }

    override fun beforeEntities(context: WorldRenderContext) {
        LevelRenderEvent.AfterTerrain(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
    }

    override fun afterEntities(context: WorldRenderContext) {
        LevelRenderEvent.AfterEntities(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
    }

    override fun beforeBlockOutline(context: WorldRenderContext, hitResult: HitResult?): Boolean {
        val event = LevelRenderEvent.BeforeBlockOutline(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers(), hitResult)
        event.post()
        return event.renderOutline
    }

    override fun onBlockOutline(
        context: WorldRenderContext,
        blockOutlineContext: WorldRenderContext.BlockOutlineContext
    ): Boolean {
        val event = LevelRenderEvent.BlockOutline(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers(), blockOutlineContext.entity(), blockOutlineContext.blockPos(), blockOutlineContext.blockState())
        event.post()
        return event.renderVanillaOutline
    }

    override fun beforeDebugRender(context: WorldRenderContext) {
        LevelRenderEvent.DebugRender(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
    }

    fun afterTranslucentTerrain(level: ClientLevel, renderer: LevelRenderer, poseStack: PoseStack, partialTicks: Float, camera: Camera, bufferSoure: MultiBufferSource) {
        LevelRenderEvent.AfterTranslucentTerrain(level, renderer, poseStack, partialTicks, camera, frustum, bufferSoure).post()
    }

    override fun afterTranslucent(context: WorldRenderContext) {
        LevelRenderEvent.AfterParticles(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
    }

    override fun onLast(context: WorldRenderContext) {
        LevelRenderEvent.Last(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
    }

    override fun onEnd(context: WorldRenderContext) {
        LevelRenderEvent.End(context.world(), context.worldRenderer(), context.matrixStack(), context.tickDelta(),
            context.camera(), context.frustum(), context.consumers()).post()
    }

    override fun onInvalidate() {
        ReloadLevelRendererEvent.post()
    }
}