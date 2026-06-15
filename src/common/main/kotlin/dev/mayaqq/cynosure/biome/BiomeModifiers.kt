package dev.mayaqq.cynosure.biome

import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
public interface BiomeModifiers {

    public companion object Impl : BiomeModifiers by loadPlatform()

    public fun addFeature(biome: (Holder<Biome>) -> Boolean, step: GenerationStep.Decoration, feature: ResourceKey<PlacedFeature>)

    public fun addSpawn(biome: (Holder<Biome>) -> Boolean, category: MobCategory, type: EntityType<*>, weight: Int, groupSize: Pair<Int, Int>)

    public fun addCarver(biome: (Holder<Biome>) -> Boolean, step: GenerationStep.Carving, carver: ResourceKey<ConfiguredWorldCarver<*>>)
}

public class FeatureAddBiomeModification(
    public val biome: (Holder<Biome>) -> Boolean,
    public val step: GenerationStep.Decoration,
    public val feature: ResourceKey<PlacedFeature>
)

public class SpawnAddBiomeModification(
    public val biome: (Holder<Biome>) -> Boolean,
    public val category: MobCategory,
    public val type: EntityType<*>,
    public val weight: Int,
    public val groupSize: Pair<Int, Int>
)

public class CarverAddBiomeModification(
    public val biome: (Holder<Biome>) -> Boolean,
    public val step: GenerationStep.Carving,
    public val carver: ResourceKey<ConfiguredWorldCarver<*>>
)