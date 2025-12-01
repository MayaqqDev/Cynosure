package dev.mayaqq.cynosure.client.events.render

import com.mojang.blaze3d.vertex.PoseStack
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult

public sealed class LevelRenderEvent(
    public val level: ClientLevel,
    public val renderer: LevelRenderer,
    public val poseStack: PoseStack,
    public val partialTick: Float,
    public val camera: Camera,
    public val frustum: Frustum?,
    public val bufferSource: MultiBufferSource?
) : Event {

    /**
     * Fired at the beginning of [LevelRenderer.renderLevel].
     * Note that frustum and bufferSource will not yet be available in this event
     */
    public class Start(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ): LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    /**
     * Fired Aftter terrain rendering setup is complete but before any terrain is rendered
     */
    public class BeforeTerrain(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    /**
     * Fired after solid, cutout and cutout mipped render layers have been rendered but before any entity rendering occurs
     */
    public class AfterTerrain(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    /**
     * Fired after entities are rendered but before block entities
     */
    public class AfterEntities(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class BeforeBlockOutline(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
        public val hitResult: HitResult?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource) {
        public var renderOutline: Boolean = true
        private set

        public fun preventBlockOutline() {
            renderOutline = false
        }
    }

    public class BlockOutline(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
        public val entity: Entity,
        public val pos: BlockPos,
        public val state: BlockState
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource) {
        public var renderVanillaOutline: Boolean = true
        private set

        public fun preventVanillaOutline() {
            renderVanillaOutline = false
        }
    }

    public class DebugRender(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class AfterTranslucentTerrain(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class AfterParticles(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class Last(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class End(
        level: ClientLevel,
        renderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : LevelRenderEvent(level, renderer, poseStack, partialTick, camera, frustum, bufferSource)
}