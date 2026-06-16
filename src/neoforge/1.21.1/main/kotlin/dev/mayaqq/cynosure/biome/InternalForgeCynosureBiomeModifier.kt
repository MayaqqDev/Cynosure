package dev.mayaqq.cynosure.biome

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Dynamic
import com.mojang.serialization.MapCodec
import dev.mayaqq.cynosure.MODID
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.resources.RegistryOps
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.MobSpawnSettings.*
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier
import kotlin.jvm.optionals.getOrNull


internal class InternalForgeCynosureBiomeModifier(val ops: RegistryOps<*>) : BiomeModifier {

    private var internalRegistryAccess: HolderLookup.Provider? = null
    val registryAccess: HolderLookup.Provider
        get() = run {
            if (internalRegistryAccess == null) internalRegistryAccess = VanillaRegistries.createLookup()
            internalRegistryAccess!!
        }

    override fun modify(
        biome: Holder<Biome>,
        phase: BiomeModifier.Phase,
        info: ModifiableBiomeInfo.BiomeInfo.Builder
    ) {
        when (phase) {
            BiomeModifier.Phase.BEFORE_EVERYTHING -> {}
            BiomeModifier.Phase.ADD -> {
                val generationSettings = info.generationSettings
                BiomeModifiersImpl.featureAdd.forEach { feature ->
                    if (feature.biome.invoke(biome)) {
                        ops.getter(Registries.PLACED_FEATURE).getOrNull()?.let { registry ->
                            generationSettings.addFeature(
                                feature.step,
                                registry.get(feature.feature).getOrNull()?: return@forEach
                            )
                        }
                    }
                }
                val spawnSettings = info.mobSpawnSettings
                BiomeModifiersImpl.spawnAdd.forEach { spawn ->
                    if (spawn.biome.invoke(biome)) {
                        spawnSettings.addSpawn(spawn.category, SpawnerData(
                            spawn.type,
                            spawn.weight,
                            spawn.groupSize.first,
                            spawn.groupSize.second
                        )
                        )
                    }
                }
                BiomeModifiersImpl.carverAdd.forEach { carver ->
                    if (carver.biome.invoke(biome)) {
                        ops.getter(Registries.CONFIGURED_CARVER).getOrNull()?.let { registry ->
                            generationSettings.addCarver(
                                carver.step,
                                registry.get(carver.carver).getOrNull()?: return@forEach
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

    override fun codec(): MapCodec<out BiomeModifier> = CarverRegistry.CYNOSURE_BIOME_MODIFIER_CODEC.value()
}

internal object CarverRegistry {
    val BIOME_MODIFIER_SERIALIZERS: DeferredRegister<MapCodec<out BiomeModifier>> =
        DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID)

    val REGISTRY_OPS_CODEC: Codec<RegistryOps<*>> = Codec.PASSTHROUGH.comapFlatMap(
        { input -> (input.ops as? RegistryOps<*>)?.let { DataResult.success(it) } ?: DataResult.error { "Was not passed a registry ops can't load correctly" } },
        { ops -> Dynamic(ops) },
    )

    val CYNOSURE_BIOME_MODIFIER_CODEC: Holder<MapCodec<out BiomeModifier>> = BIOME_MODIFIER_SERIALIZERS.register(MODID, Supplier {
        MapCodec.assumeMapUnsafe(REGISTRY_OPS_CODEC.xmap(::InternalForgeCynosureBiomeModifier, InternalForgeCynosureBiomeModifier::ops))
    })
}
