package dev.mayaqq.cynosure.client.render

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.VertexFormat
import dev.mayaqq.cynosure.utils.make
import net.minecraft.client.renderer.RenderType
import java.util.*

public expect fun RenderType.fixed(phase: BufferOutputStage): RenderType

public enum class BufferOutputStage {
    ENTITY,
    BLOCK_ENTITY,
    TRANSLUCENT,
    NONE
}

public object RenderTypeRegistry {

    @JvmField
    val TYPES: MutableMap<RenderType, BufferBuilder> = mutableMapOf()

    @JvmField
    val PHASES: Map<BufferOutputStage, MutableList<RenderType>> = make(EnumMap(BufferOutputStage::class.java)) {
        BufferOutputStage.entries.forEach { put(it, ArrayList()) }
    }

    @JvmStatic
    public var frozen: Boolean = false
        @JvmName("setFrozen")
        internal set
}

public expect fun registerFixedBuffer(phase: BufferOutputStage, renderType: RenderType, builder: BufferBuilder)