package dev.mayaqq.cynosure.injection

import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.functions.LootItemFunction

public interface ILootTableBuilder {
    public fun cynosure_addPools(poolList: List<LootPool>)
    public fun cynosure_addFunctions(functionList: List<LootItemFunction>)
    public fun cynosure_apply(functions: MutableList<LootItemFunction>)
}

public fun LootTable.Builder.apply(functions: MutableList<LootItemFunction>): LootTable.Builder {
    (this as ILootTableBuilder).cynosure_addFunctions(functions)
    return this
}

public fun LootTable.Builder.pools(pools: List<LootPool>): LootTable.Builder {
    (this as ILootTableBuilder).cynosure_addPools(pools)
    return this
}