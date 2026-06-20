package dev.mayaqq.cynosure.client.utils

import com.mojang.blaze3d.vertex.VertexConsumer
import invoke.kitty.kritter.utils.color.Color
import invoke.kitty.kritter.utils.color.White
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Direction.AxisDirection

//TODO: this mess

public actual fun VertexConsumer.color(color: Color): VertexConsumer {
    this.setColor(color.toInt())
    return this
}

public actual fun VertexConsumer.color(color: UInt): VertexConsumer {
    this.setColor(color.toInt())
    return this
}

public fun VertexConsumer.vertex(x: Float, y: Float, z: Float): VertexConsumer = this.addVertex(x, y, z)

public fun VertexConsumer.vertex(x: Double, y: Double, z: Double): VertexConsumer = this.addVertex(x.toFloat(), y.toFloat(), z.toFloat())

public fun VertexConsumer.uv(u: Float, v: Float): VertexConsumer = this.setUv(u, v)

public fun VertexConsumer.overlayCoords(overlay: Int): VertexConsumer = this.setOverlay(overlay)

public fun VertexConsumer.light(light: Int): VertexConsumer = this.light(light)

public fun VertexConsumer.normal(x: Float, y: Float, z: Float): VertexConsumer = this.setNormal(x, y, z)

public actual fun VertexConsumer.square(
    start: Double,
    end: Double,
    direction: Direction,
    minU: Float,
    maxU: Float,
    minV: Float,
    maxV: Float,
    color: Color,
    overlay: Int,
    light: Int
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

    vertex(x1, y1, z1).color(color).uv(maxU, maxV).overlayCoords(overlay).light(light).normal(nx, ny, nz)//TODO:.endVertex()
    vertex(x2, y1, z2).color(color).uv(minU, maxV).overlayCoords(overlay).light(light).normal(nx, ny, nz)//.endVertex()
    vertex(x2, y2, z3).color(color).uv(minU, minV).overlayCoords(overlay).light(light).normal(nx, ny, nz)//.endVertex()
    vertex(x1, y2, z4).color(color).uv(maxU, minV).overlayCoords(overlay).light(light).normal(nx, ny, nz)//.endVertex()
}