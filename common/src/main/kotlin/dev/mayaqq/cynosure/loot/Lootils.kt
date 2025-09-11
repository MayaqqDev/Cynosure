package dev.mayaqq.cynosure.loot

import dev.mayaqq.cynosure.injection.pools
import dev.mayaqq.cynosure.injection.apply
import dev.mayaqq.cynosure.injection.lootFunctions
import dev.mayaqq.cynosure.injection.lootPools
import dev.mayaqq.cynosure.injection.lootRandomSequence
import net.minecraft.world.level.storage.loot.LootTable

public fun LootTable.copy(): LootTable.Builder = LootTable.lootTable()
        .setParamSet(this.paramSet)
        .pools(this.lootPools)
        .apply(this.lootFunctions)
        .setRandomSequence(this.lootRandomSequence)
