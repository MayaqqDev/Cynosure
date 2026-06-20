package dev.mayaqq.cynosure.client.render

import com.mojang.blaze3d.vertex.BufferBuilder
import dev.mayaqq.cynosure.client.render.RenderTypeRegistry.frozen
import net.minecraft.client.renderer.RenderType

public actual fun RenderType.fixed(phase: BufferOutputStage): RenderType {
    registerFixedBuffer(phase, this, BufferBuilder(bufferSize()))
    return this
}

public expect fun registerFixedBuffer(phase: BufferOutputStage, renderType: RenderType, builder: BufferBuilder) {
    require(!frozen) { "Render type registry already frozon" }
    if(RenderTypeRegistry.TYPES.containsKey(renderType)) {
        throw IllegalArgumentException("RenderType $renderType has already been registered")
    }

    RenderTypeRegistry.TYPES[renderType] = builder
    RenderTypeRegistry.PHASES[phase]!!.add(renderType)
}