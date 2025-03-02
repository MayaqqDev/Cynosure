package dev.mayaqq.cynosure.client.models.baked

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.mayaqq.cynosure.client.models.animations.Animatable
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import org.joml.Quaternionf
import org.joml.Vector3fc

class BakedModelTree(
    mesh: Mesh,
    texture: ResourceLocation,
    renderType: ModelRenderType,
    minBound: Vector3fc,
    maxBound: Vector3fc,
    private val origin: Vector3fc,
    private val children: Map<String, BakedModelTree>
) : CustomBakedModel(mesh, texture, renderType, minBound, maxBound), Animatable, Animatable.Provider {

    private var x: Float = 0f
    private var y: Float = 0f
    private var z: Float = 0f
    private var xRot: Float = 0f
    private var yRot: Float = 0f
    private var zRot: Float = 0f
    private var xScale: Float = 1f
    private var yScale: Float = 1f
    private var zScale: Float = 1f

    override fun offsetPosition(offset: Vector3fc) {
        x = offset.x
        y = offset.y()
        z = offset.z()
    }

    override fun offsetRotation(offset: Vector3fc) {
        xRot = offset.x()
        yRot = offset.y()
        zRot = offset.z()
    }

    override fun offsetScale(offset: Vector3fc) {
        xScale = offset.x()
        yScale = offset.y()
        zScale = offset.z()
    }

    override fun reset() {
        x = 0f
        y = 0f
        z = 0f
        xRot = 0f
        yRot = 0f
        zRot = 0f
        xScale = 1f
        yScale = 1f
        zScale = 1f
    }

    override fun getAny(key: String): Animatable? =
        children[key] ?: children.mapNotNull { (_, child) -> child.getAny(key) }.firstOrNull()

    private fun applyTransform(poseStack: PoseStack) {
        poseStack.translate(x / 16f, y / 16f, z / 16f)
        poseStack.translate(origin.x, origin.y, origin.z)

        if (xRot != 0f || yRot != 0f || zRot != 0f) {
            poseStack.mulPose(Quaternionf().rotationXYZ(xRot, yRot, zRot))
        }
        if (xScale != 1f || yScale != 1f || zScale != 1f) {
            poseStack.scale(xScale, yScale, zScale)
        }

        poseStack.translate(-origin.x, -origin.y, -origin.z)
    }

    override fun render(bufferSource: MultiBufferSource, matrices: PoseStack, color: Int, light: Int, overlay: Int) {
        applyTransform(matrices)
        super.render(bufferSource, matrices, color, light, overlay)

        children.forEach { (_, child) ->
            matrices.pushPose()
            child.render(bufferSource, matrices, color, light, overlay)
            matrices.popPose()
        }
    }
}

inline val Vector3fc.x get() = x()

inline val Vector3fc.y get() = y()

inline val Vector3fc.z get() = z()