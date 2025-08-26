@file:EventSubscriber
package dev.mayaqq.cynosure.biome

import com.mojang.serialization.Codec
import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.biome.InternalForgeCynosureBiomeModifier.registryAccess
import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.Subscription
import dev.mayaqq.cynosure.events.server.ServerEvent
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.MobSpawnSettings
import net.minecraftforge.common.world.BiomeModifier
import net.minecraftforge.common.world.ModifiableBiomeInfo
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import uwu.serenity.kritter.platform.isDevEnvironment
import kotlin.jvm.optionals.getOrNull


internal object InternalForgeCynosureBiomeModifier : BiomeModifier {

    var registryAccess: RegistryAccess? = null

    override fun modify(
        biome: Holder<Biome>,
        phase: BiomeModifier.Phase,
        info: ModifiableBiomeInfo.BiomeInfo.Builder
    ) {
        when (phase) {
            BiomeModifier.Phase.BEFORE_EVERYTHING -> {
                logInIde("Forge Cynosure Biome Modifier has Started")
            }
            BiomeModifier.Phase.ADD -> {
                val generationSettings = info.generationSettings
                BiomeModifiersImpl.featureAdd.forEach { feature ->
                    if (feature.biome.invoke(biome.get())) {
                        registryAccess?.registry(Registries.PLACED_FEATURE)?.getOrNull()?.let { registry ->
                            generationSettings.addFeature(
                                feature.step,
                                registry.wrapAsHolder(registry.get(feature.feature)?: return@forEach)
                            )
                        }
                    }
                }
                val spawnSettings = info.mobSpawnSettings
                logInIde("Cynosure Adding Spawns")
                BiomeModifiersImpl.spawnAdd.forEach { spawn ->
                    if (spawn.biome.invoke(biome.get())) {
                        spawnSettings.addSpawn(spawn.category, MobSpawnSettings.SpawnerData(
                            spawn.type,
                            spawn.weight,
                            spawn.groupSize.first,
                            spawn.groupSize.second
                        ))
                    }
                }
                BiomeModifiersImpl.carverAdd.forEach { carver ->
                    if (carver.biome.invoke(biome.get())) {
                        registryAccess?.registry(Registries.CONFIGURED_CARVER)?.getOrNull()?.let { registry ->
                            generationSettings.addCarver(
                                carver.step,
                                registry.wrapAsHolder(registry.get(carver.carver)?: return@forEach)
                            )
                        }
                    }
                }
            }
            BiomeModifier.Phase.REMOVE -> {}
            BiomeModifier.Phase.MODIFY -> {}
            BiomeModifier.Phase.AFTER_EVERYTHING -> {}
        }
    }

    override fun codec(): Codec<out BiomeModifier> = CarverRegistry.CYNOSURE_BIOME_MODIFIER_CODEC.get()

    private fun logInIde(info: String) {
        if (isDevEnvironment) {
            Cynosure.info(info)
        }
    }
}

internal object CarverRegistry {
    var BIOME_MODIFIER_SERIALIZERS: DeferredRegister<Codec<out BiomeModifier>> =
        DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID)

    var CYNOSURE_BIOME_MODIFIER_CODEC: RegistryObject<Codec<InternalForgeCynosureBiomeModifier>> = BIOME_MODIFIER_SERIALIZERS.register("cynosure") {
        Codec.unit(InternalForgeCynosureBiomeModifier)
    }
}

@Subscription(priority = Subscription.HIGHEST)
public fun onServerStart(event: ServerEvent.Started) {
    registryAccess = event.server.registryAccess()
}
