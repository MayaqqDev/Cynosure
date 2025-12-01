package dev.mayaqq.cynosure.client.entity

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraftforge.client.ForgeHooksClient

internal class EntityModelLayerRegistryImpl : EntityModelLayerRegistry {
    @Suppress("UnstableApiUsage")
    override fun register(layer: ModelLayerLocation, definition: () -> LayerDefinition) {
        ForgeHooksClient.registerLayerDefinition(layer, definition)
    }
}