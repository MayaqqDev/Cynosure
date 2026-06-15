package dev.mayaqq.cynosure.biome

import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import net.minecraft.world.level.levelgen.placement.PlacedFeature

public object BiomeModifiersImpl : BiomeModifiers {

    internal val featureAdd = mutableListOf<FeatureAddBiomeModification>()
    internal val spawnAdd = mutableListOf<SpawnAddBiomeModification>()
    internal val carverAdd = mutableListOf<CarverAddBiomeModification>()

    override fun addFeature(
        biome: (Holder<Biome>) -> Boolean,
        step: GenerationStep.Decoration,
        feature: ResourceKey<PlacedFeature>
    ) {
        featureAdd.add(FeatureAddBiomeModification(biome, step, feature))
    }

    override fun addSpawn(
        biome: (Holder<Biome>) -> Boolean,
        category: MobCategory,
        type: EntityType<*>,
        weight: Int,
        groupSize: Pair<Int, Int>
    ) {
        spawnAdd.add(SpawnAddBiomeModification(biome, category, type, weight, groupSize))
    }

    override fun addCarver(
        biome: (Holder<Biome>) -> Boolean,
        step: GenerationStep.Carving,
        carver: ResourceKey<ConfiguredWorldCarver<*>>
    ) {
        carverAdd.add(CarverAddBiomeModification(biome, step, carver))
    }
}