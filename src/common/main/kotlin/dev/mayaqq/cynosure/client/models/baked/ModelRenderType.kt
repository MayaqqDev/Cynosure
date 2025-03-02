package dev.mayaqq.cynosure.client.models.baked

import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable

@Serializable
enum class ModelRenderType(private val func: (ResourceLocation) -> RenderType, private val serialName: String) : StringRepresentable {
    SOLID(RenderType::entitySolid, "solid"),
    CUTOUT(RenderType::entityCutout, "cutout"),
    TRANSLUCENT(RenderType::entityTranslucentCull, "translucent"),
    EMISSIVE(RenderType::entityTranslucentEmissive, "emissive");

    companion object {

        val CODEC: Codec<ModelRenderType> = Codec.STRING.comapFlatMap(
            fun(s) = byName(s)?.let { DataResult.success(it) } ?: DataResult.error { "no such model render type" },
            ModelRenderType::serialName
        )

        fun byName(name: String): ModelRenderType? = entries.find { it.serialName == name }
    }

    fun apply(id: ResourceLocation): RenderType = func(id)

    override fun getSerializedName(): String = serialName
}