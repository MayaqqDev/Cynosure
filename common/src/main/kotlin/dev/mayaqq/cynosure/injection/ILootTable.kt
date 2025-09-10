package dev.mayaqq.cynosure.injection

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.functions.LootItemFunction

public interface ILootTable {
    public fun cynosure_getPools(): MutableList<LootPool>
    public fun cynosure_getFunctions(): MutableList<LootItemFunction>
    public fun cynosure_getRandomSequnence(): ResourceLocation
}

public val LootTable.pools: MutableList<LootPool> get() = (this as ILootTable).cynosure_getPools()
public val LootTable.functions: MutableList<LootItemFunction> get() = (this as ILootTable).cynosure_getFunctions()
public val LootTable.randomSequence: ResourceLocation get() = (this as ILootTable).cynosure_getRandomSequnence()