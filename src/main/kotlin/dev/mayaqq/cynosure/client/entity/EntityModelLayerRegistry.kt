package dev.mayaqq.cynosure.client.entity

import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition

public interface EntityModelLayerRegistry {
    public companion object Impl : EntityModelLayerRegistry by loadPlatform()

    public fun register(layer: ModelLayerLocation, definition: () -> LayerDefinition)
}

public fun ModelLayerLocation.registerDefinition(provider: () -> LayerDefinition) {
    EntityModelLayerRegistry.register(this, provider)
}

