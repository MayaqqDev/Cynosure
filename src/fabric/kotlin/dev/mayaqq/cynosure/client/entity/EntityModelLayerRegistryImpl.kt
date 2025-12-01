package dev.mayaqq.cynosure.client.entity

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry as FabricModelRegistry

internal object EntityModelLayerRegistryImpl : EntityModelLayerRegistry {
    override fun register(layer: ModelLayerLocation, definition: () -> LayerDefinition) {
        FabricModelRegistry.registerModelLayer(layer, definition)
    }
}