package dev.mayaqq.cynosure.client.events.render

import com.mojang.blaze3d.vertex.PoseStack
import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.client.Camera
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult

@RootEventClass
public sealed class WorldRenderEvent(
    public val levelRenderer: LevelRenderer,
    public val poseStack: PoseStack,
    public val partialTick: Float,
    public val camera: Camera,
    public val frustum: Frustum?,
    public val bufferSource: MultiBufferSource?
) : Event {

    public class Start(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ): WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class BeforeTerrain(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class AfterTerrain(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class AfterEntities(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class BeforeBlockOutline(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
        public val hitResult: HitResult?
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource) {
        public var renderOutline: Boolean = true
        private set

        public fun noBlockOutline() {
            renderOutline = true
        }
    }

    public class BlockOutline(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
        public val entity: Entity,
        public val pos: BlockPos,
        public val state: BlockState
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource) {
        public var renderVanillaOutline: Boolean = true
        private set

        public fun noVanillaOutline() {
            renderVanillaOutline = true
        }
    }

    public class DebugRender(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?,
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class AfterParticles(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class Last(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)

    public class End(
        levelRenderer: LevelRenderer,
        poseStack: PoseStack,
        partialTick: Float,
        camera: Camera,
        frustum: Frustum?,
        bufferSource: MultiBufferSource?
    ) : WorldRenderEvent(levelRenderer, poseStack, partialTick, camera, frustum, bufferSource)
}