package dev.mayaqq.cynosure.biome

import net.fabricmc.fabric.api.biome.v1.BiomeModifications
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
        biome: (Biome) -> Boolean,
        step: GenerationStep.Decoration,
        feature: ResourceKey<PlacedFeature>
    ) {
        BiomeModifications.addFeature(
            { biome.invoke(it.biome) },
            step,
            feature
        )
    }

    override fun addSpawn(
        biome: (Biome) -> Boolean,
        category: MobCategory,
        type: EntityType<*>,
        weight: Int,
        groupSize: Pair<Int, Int>
    ) {
        BiomeModifications.addSpawn(
            { biome.invoke(it.biome) },
            category,
            type,
            weight,
            groupSize.first,
            groupSize.second
        )
    }

    override fun addCarver(
        biome: (Biome) -> Boolean,
        step: GenerationStep.Carving,
        carver: ResourceKey<ConfiguredWorldCarver<*>>
    ) {
        BiomeModifications.addCarver(
            { biome.invoke(it.biome) },
            step,
            carver
        )
    }
}