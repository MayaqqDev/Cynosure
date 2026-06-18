package dev.mayaqq.cynosure.injection

import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition

public interface ILootPoolBuilder {
    public fun cynosure_addConditions(conditions: List<LootItemCondition>)
    public fun cynosure_addEntries(entries: List<LootPoolEntryContainer>)
}

public fun LootPool.Builder.conditionally(condition: LootItemCondition): LootPool.Builder {
    (this as ILootPoolBuilder).cynosure_addConditions(listOf(condition))
    return this
}

public fun LootPool.Builder.with(container: LootPoolEntryContainer): LootPool.Builder {
    (this as ILootPoolBuilder).cynosure_addEntries(listOf(container))
    return this
}