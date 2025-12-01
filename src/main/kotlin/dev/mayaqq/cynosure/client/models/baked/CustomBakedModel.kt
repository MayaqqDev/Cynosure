package dev.mayaqq.cynosure.client.models.baked

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.mayaqq.cynosure.utils.colors.*
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f

public open class CustomBakedModel(
    public val mesh: Mesh,
    public val renderType: ModelRenderType,
    public val minBound: Vector3fc,
    public val maxBound: Vector3fc
) {

    public open fun render(
        buffer: VertexConsumer, stack: PoseStack,
        color: Color = White,
        light: Int = LightTexture.FULL_BRIGHT,
        overlay: Int = OverlayTexture.NO_OVERLAY
    ) {
        if(mesh == Mesh.EMPTY) return

        val posVec = Vector4f()
        val normalVec = Vector3f()

        for(i in 0..<mesh.vertexCount) {
            posVec.set(mesh.x(i), mesh.y(i), mesh.z(i))
                .mul(stack.last().pose())
            normalVec.set(mesh.normalX(i), mesh.normalY(i), mesh.normalZ(i))
                .mul(stack.last().normal())

            buffer.vertex(
                posVec.x, posVec.y, posVec.z,
                color.floatRed, color.floatGreen, color.floatBlue, color.floatAlpha,
                mesh.u(i), mesh.v(i),
                overlay, light,
                normalVec.x, normalVec.y, normalVec.z
            )
        }
    }
}

