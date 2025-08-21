@file:EventSubscriber
package dev.mayaqq.cynosure.biome

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.Subscription
import dev.mayaqq.cynosure.events.server.ServerEvent
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.minecraft.core.registries.BuiltInRegistries
import uwu.serenity.kritter.platform.isDevEnvironment

internal object InternalFabricCynosureBiomeModifier {
    internal fun register() {
        logInIde("Cynosure Adding worldgen Features")
        BiomeModifiers.featureAdd.forEach { feature ->
            BiomeModifications.addFeature(
                { feature.biome.invoke(it.biome) },
                feature.step,
                feature.feature
            )
            logInIde("Cynosure Added Feature: ${feature.feature.location()}")
        }
        logInIde("Cynosure Adding spawns")
        BiomeModifiers.spawnsAdd.forEach { spawn ->
            BiomeModifications.addSpawn(
                { spawn.biome.invoke(it.biome) },
                spawn.category,
                spawn.type,
                spawn.weight,
                spawn.groupSize.first,
                spawn.groupSize.second
            )
            logInIde("Cynosure Added Spawn: ${BuiltInRegistries.ENTITY_TYPE.getKey(spawn.type)}")
        }
        logInIde("Cynosure Adding carvers")
        BiomeModifiers.carverAdd.forEach { carver ->
            BiomeModifications.addCarver(
                { carver.biome.invoke(it.biome) },
                carver.step,
                carver.carver
            )
            logInIde("Cynosure Added Carver: ${carver.carver.location()}")
        }
    }

    private fun logInIde(info: String) {
        if (isDevEnvironment) {
            Cynosure.info(info)
        }
    }
}

@Subscription(priority = Subscription.HIGHEST)
public fun onServerStart(event: ServerEvent.Started) {
    InternalFabricCynosureBiomeModifier.register()
}