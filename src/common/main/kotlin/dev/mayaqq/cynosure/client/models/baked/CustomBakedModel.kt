package dev.mayaqq.cynosure.client.models.baked

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f

open class CustomBakedModel(
    val mesh: Mesh,
    val texture: ResourceLocation,
    val renderType: ModelRenderType,
    val minBound: Vector3fc,
    val maxBound: Vector3fc
) {

    open fun render(
        bufferSource: MultiBufferSource, matrices: PoseStack,
        color: Int = 0xFFFFFFF,
        light: Int = LightTexture.FULL_BRIGHT,
        overlay: Int = OverlayTexture.NO_OVERLAY
    ) {
        if(mesh == Mesh.EMPTY) return

        val posVec = Vector4f()
        val normalVec = Vector3f()
        val buffer = bufferSource.getBuffer(renderType.apply(texture))

        for(i in 0 ..< mesh.vertexCount) {
            posVec.set(mesh.x(i), mesh.y(i), mesh.z(i))
                .mul(matrices.last().pose())
            normalVec.set(mesh.normalX(i), mesh.normalY(i), mesh.normalZ(i))
                .mul(matrices.last().normal())

            buffer.vertex(
                posVec.x, posVec.y, posVec.z,
                (color shr 16 and 0xFF) / 255f,
                (color shr 8 and 0xFF) / 255f,
                (color and 0xFF) / 255f,
                (color ushr 24 and 0xFF) / 255f,
                mesh.u(i), mesh.v(i),
                overlay, light,
                normalVec.x, normalVec.y, normalVec.z
            )
        }
    }
}

