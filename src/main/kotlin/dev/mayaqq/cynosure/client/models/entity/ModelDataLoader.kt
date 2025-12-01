package dev.mayaqq.cynosure.client.models.entity

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.client.models.ModelData
import dev.mayaqq.cynosure.client.models.animations.AnimationDefinition
import dev.mayaqq.cynosure.client.models.bake
import dev.mayaqq.cynosure.client.models.baked.CustomBakedModel
import dev.mayaqq.cynosure.client.models.baked.Mesh
import dev.mayaqq.cynosure.client.models.baked.ModelRenderType
import dev.mayaqq.cynosure.utils.json.toKotlinx
import dev.mayaqq.cynosure.utils.result.failure
import kotlinx.serialization.json.Json
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.joml.Vector3f
import kotlin.jvm.optionals.getOrNull

private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

public object ModelDataLoader : SimpleJsonResourceReloadListener(GSON, "cynosure/models") {

    private val EMPTY_MODEL =  CustomBakedModel(Mesh.EMPTY, ModelRenderType.SOLID, Vector3f(), Vector3f())
    private var modelPreparations: Map<ResourceLocation, ModelData>? = null

    public fun loadAndBakeModel(id: ResourceLocation): CustomBakedModel {
        return (getModelData(id)?.bake() ?: "Model does not exist".failure())
            .onFailure { Cynosure.error("Failed to bake model $id", it) }
            .getOrDefault(EMPTY_MODEL)
    }

    public fun getModelData(id: ResourceLocation): ModelData? = modelPreparations?.get(id)

    override fun apply(p0: MutableMap<ResourceLocation, JsonElement>, p1: ResourceManager, p2: ProfilerFiller) {
        modelPreparations = p0.entries.mapNotNull { (id, json) ->
            ModelData.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(fun(err) = Cynosure.error("Error loading custom model: {}", err))
                .getOrNull()
                ?.let { id to it }
        }.toMap()
    }

}

public object AnimationDataLoader : SimpleJsonResourceReloadListener(GSON, "cynosure/animations") {

    private var animations: Map<ResourceLocation, AnimationDefinition>? = null

    public fun getAnimation(id: ResourceLocation): AnimationDefinition? = animations?.get(id)

    override fun apply(p0: MutableMap<ResourceLocation, JsonElement>, p1: ResourceManager, p2: ProfilerFiller) {
        animations = p0.entries.mapNotNull { (id, json) ->
            id to Json.decodeFromJsonElement(AnimationDefinition.serializer(), json.toKotlinx())
        }.toMap()
    }
}