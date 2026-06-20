package dev.mayaqq.cynosure.client.models.entity

import dev.mayaqq.cynosure.client.models.animations.AnimationDefinition
import dev.mayaqq.cynosure.client.models.baked.CustomBakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.Entity
import org.joml.Vector3f

public expect class DataBasedModel<E : Entity>(
    innerModel: CustomBakedModel,
    animations: List<Pair<(E) -> AnimationState, AnimationDefinition>>?
) {
    public constructor(id: ResourceLocation, vararg animations: Pair<(E) -> AnimationState, ResourceLocation>)
}

public object DataBasedModelCache {
    public val VEC_CACHE: Vector3f = Vector3f()
}