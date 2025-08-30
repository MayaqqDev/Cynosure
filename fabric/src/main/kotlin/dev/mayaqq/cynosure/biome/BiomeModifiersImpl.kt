package dev.mayaqq.cynosure.biome

import dev.mayaqq.cynosure.Cynosure
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import kotlin.ranges.step

public object BiomeModifiersImpl : BiomeModifiers {
    override fun addFeature(
        biome: (Holder<Biome>) -> Boolean,
        step: GenerationStep.Decoration,
        feature: ResourceKey<PlacedFeature>
    ) {
        BiomeModifications.addFeature(
            { biome.invoke(it.biomeRegistryEntry) },
            step,
            feature
        )
    }

    override fun addSpawn(
        biome: (Holder<Biome>) -> Boolean,
        category: MobCategory,
        type: EntityType<*>,
        weight: Int,
        groupSize: Pair<Int, Int>
    ) {
        BiomeModifications.addSpawn(
            { biome.invoke(it.biomeRegistryEntry) },
            category,
            type,
            weight,
            groupSize.first,
            groupSize.second
        )
    }

    override fun addCarver(
        biome: (Holder<Biome>) -> Boolean,
        step: GenerationStep.Carving,
        carver: ResourceKey<ConfiguredWorldCarver<*>>
    ) {
        BiomeModifications.addCarver(
            { biome.invoke(it.biomeRegistryEntry) },
            step,
            carver
        )
    }
}