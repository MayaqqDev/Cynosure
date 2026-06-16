package dev.mayaqq.cynosure.client.entity

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.neoforged.neoforge.client.ClientHooks

internal class EntityModelLayerRegistryImpl : EntityModelLayerRegistry {
    override fun register(layer: ModelLayerLocation, definition: () -> LayerDefinition) {
        ClientHooks.registerLayerDefinition(layer, definition)
    }
}