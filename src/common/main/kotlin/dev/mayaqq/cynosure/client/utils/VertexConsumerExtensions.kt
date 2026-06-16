package dev.mayaqq.cynosure.client.utils

import com.mojang.blaze3d.vertex.VertexConsumer
import invoke.kitty.kritter.utils.color.Color
import invoke.kitty.kritter.utils.color.White
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction

//TODO: this mess

public expect fun VertexConsumer.color(color: Color): VertexConsumer

public expect fun VertexConsumer.color(color: UInt): VertexConsumer

public expect fun VertexConsumer.square(
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
)