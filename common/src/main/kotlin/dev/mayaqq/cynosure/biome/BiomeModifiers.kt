package dev.mayaqq.cynosure.biome

import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import net.minecraft.world.level.levelgen.placement.PlacedFeature

public object BiomeModifiers {
    public val featureAdd: MutableList<FeatureAddBiomeModification> = mutableListOf()
    public val spawnsAdd: MutableList<SpawnAddBiomeModification> = mutableListOf()
    public val carverAdd: MutableList<CarverAddBiomeModification> = mutableListOf()

    public fun addFeature(biome: (Biome) -> Boolean, step: GenerationStep.Decoration, feature: ResourceKey<PlacedFeature>) {
        featureAdd.add(FeatureAddBiomeModification(biome, step, feature))
    }

    public fun addSpawn(biome: (Biome) -> Boolean, category: MobCategory, type: EntityType<*>, weight: Int, groupSize: Pair<Int, Int>) {
        spawnsAdd.add(SpawnAddBiomeModification(biome, category, type, weight, groupSize))
    }

    public fun addCarver(biome: (Biome) -> Boolean, step: GenerationStep.Carving, carver: ResourceKey<ConfiguredWorldCarver<*>>) {
        carverAdd.add(CarverAddBiomeModification(biome, step, carver))
    }
}

public class FeatureAddBiomeModification(
    public val biome: (Biome) -> Boolean,
    public val step: GenerationStep.Decoration,
    public val feature: ResourceKey<PlacedFeature>
)

public class SpawnAddBiomeModification(
    public val biome: (Biome) -> Boolean,
    public val category: MobCategory,
    public val type: EntityType<*>,
    public val weight: Int,
    public val groupSize: Pair<Int, Int>
)

public class CarverAddBiomeModification(
    public val biome: (Biome) -> Boolean,
    public val step: GenerationStep.Carving,
    public val carver: ResourceKey<ConfiguredWorldCarver<*>>
)