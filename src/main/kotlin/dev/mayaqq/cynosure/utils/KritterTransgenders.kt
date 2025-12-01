package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.biome.BiomeModifiers
import dev.mayaqq.cynosure.items.extensions.CustomTooltip
import dev.mayaqq.cynosure.items.extensions.registerExtension
import dev.mayaqq.cynosure.tooltips.DescriptionTooltip
import net.minecraft.core.Holder
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.Item
import net.minecraft.world.level.biome.Biome
import uwu.serenity.kritter.stdlib.EntityBuilder
import uwu.serenity.kritter.stdlib.ItemBuilder

// Biome Modifiers
public inline fun EntityBuilder<*>.addSpawn(
    noinline biome: (Holder<Biome>) -> Boolean,
    category: MobCategory,
    weight: Int,
    groupSize: Pair<Int, Int>
) {
    onRegister {
        BiomeModifiers.addSpawn(biome, category, it, weight, groupSize)
    }
}
// Tooltip
public inline fun ItemBuilder<*>.standardTooltip() {
    onRegister {
        it.registerExtension(DescriptionTooltip(DescriptionTooltip.Theme.Default))
    }
}

public inline fun ItemBuilder<*>.tooltip(crossinline tooltip: (Item) -> CustomTooltip) {
    onRegister {
        it.registerExtension(tooltip(it))
    }
}