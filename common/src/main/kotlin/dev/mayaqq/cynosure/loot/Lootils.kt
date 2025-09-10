package dev.mayaqq.cynosure.loot

import dev.mayaqq.cynosure.injection.functions
import dev.mayaqq.cynosure.injection.pools
import dev.mayaqq.cynosure.injection.apply
import dev.mayaqq.cynosure.injection.randomSequence
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable

public fun LootTable.copy(): LootTable.Builder = LootTable.lootTable()
        .setParamSet(this.paramSet)
        .pools(this.pools)
        .apply(this.functions)
        .setRandomSequence(this.randomSequence)

public fun LootTable.Builder.pools(pools: List<LootPool>): LootTable.Builder {
    this.pools.addAll(pools)
    return this
}