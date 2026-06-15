package dev.mayaqq.cynosure.injection

import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.functions.LootItemFunction

public interface ILootTableBuilder {
    public fun cynosure_getPools(): MutableList<LootPool>
    public fun cynosure_getFunctions(): MutableList<LootItemFunction>
    public fun cynosure_apply(functions: MutableList<LootItemFunction>)
}

public val LootTable.Builder.lootPools: MutableList<LootPool> get() = (this as ILootTableBuilder).cynosure_getPools()
public val LootTable.Builder.lootFunctions: MutableList<LootItemFunction> get() = (this as ILootTableBuilder).cynosure_getFunctions()

public fun LootTable.Builder.apply(functions: MutableList<LootItemFunction>): LootTable.Builder {
    this.lootFunctions.addAll(functions)
    return this
}

public fun LootTable.Builder.pools(pools: List<LootPool>): LootTable.Builder {
    this.lootPools.addAll(pools)
    return this
}