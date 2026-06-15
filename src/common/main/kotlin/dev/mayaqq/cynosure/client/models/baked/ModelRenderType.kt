package dev.mayaqq.cynosure.client.models.baked

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable

public enum class ModelRenderType(private val func: (ResourceLocation) -> RenderType, private val serialName: String) : StringRepresentable {
    SOLID(RenderType::entitySolid, "solid"),
    CUTOUT(RenderType::entityCutout, "cutout"),
    TRANSLUCENT(RenderType::entityTranslucentCull, "translucent"),
    EMISSIVE(RenderType::entityTranslucentEmissive, "emissive");

    public companion object {

        public val CODEC: Codec<ModelRenderType> = Codec.STRING.comapFlatMap(
            fun(s) = byName(s)?.let { DataResult.success(it) } ?: DataResult.error { "no such model render type" },
            ModelRenderType::serialName
        )

        public fun byName(name: String): ModelRenderType? = entries.find { it.serialName == name }
    }

    public fun apply(id: ResourceLocation): RenderType = func(id)

    override fun getSerializedName(): String = serialName
}