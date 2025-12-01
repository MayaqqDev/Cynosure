package dev.mayaqq.cynosure.client.utils

import com.mojang.blaze3d.vertex.VertexConsumer
import dev.mayaqq.cynosure.utils.colors.Color
import dev.mayaqq.cynosure.utils.colors.White
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Direction.AxisDirection

public fun VertexConsumer.color(color: Color): VertexConsumer {
    this.color(color.value)
    return this
}

public fun VertexConsumer.color(color: UInt): VertexConsumer {
    this.color(color.toInt())
    return this
}

public fun VertexConsumer.square(
    start: Double,
    end: Double,
    direction: Direction = Direction.NORTH,
    minU: Float = 0f,
    maxU: Float = 1f,
    minV: Float = 0f,
    maxV: Float = 1f,
    color: Color = White,
    overlay: Int = OverlayTexture.NO_OVERLAY,
    light: Int = LightTexture.FULL_BRIGHT
) {

    val axis = direction.axis
    val start1 = if(direction.axisDirection == AxisDirection.POSITIVE) start else end
    val end1 = if(direction.axisDirection == AxisDirection.NEGATIVE) end else start

    val x1: Double; val x2: Double;
    val y1: Double; val y2: Double;
    val z1: Double; val z2: Double; val z3: Double; val z4: Double

    when(axis) {
        Axis.X -> {
            x1 = 0.0; x2 = 0.0;
            z1 = start1; z2 = end1; z3 = end1; z4 = start1
        }
        Axis.Y -> {
            x1 = start1; x2 = end1
            z1 = start1; z2 = start1; z3 = end1; z4 = end1
        }
        Axis.Z -> {
            x1 = start1; x2 = end1
            z1 = 0.0; z2 = 0.0; z3 = 0.0; z4 = 0.0
        }
    }

    if(axis == Axis.Y) {
        y1 = 0.0; y2 = 0.0;
    } else {
        y1 = start1; y2 = end1;
    }

    val (nx, ny, nz) = direction.step()

    vertex(x1, y1, z1).color(color).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex()
    vertex(x2, y1, z2).color(color).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex()
    vertex(x2, y2, z3).color(color).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex()
    vertex(x1, y2, z4).color(color).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex()
}