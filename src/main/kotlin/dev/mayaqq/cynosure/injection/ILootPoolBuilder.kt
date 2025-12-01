package dev.mayaqq.cynosure.injection

import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition

public interface ILootPoolBuilder {
    public fun cynosure_getConditions(): MutableList<LootItemCondition>
    public fun cynosure_getEntries(): MutableList<LootPoolEntryContainer>
}

public val LootPool.Builder.conditions: MutableList<LootItemCondition> get() = (this as ILootPoolBuilder).cynosure_getConditions()
public val LootPool.Builder.entries: MutableList<LootPoolEntryContainer> get() = (this as ILootPoolBuilder).cynosure_getEntries()

public fun LootPool.Builder.conditionally(condition: LootItemCondition): LootPool.Builder {
    conditions.add(condition)
    return this
}

public fun LootPool.Builder.with(container: LootPoolEntryContainer): LootPool.Builder {
    entries.add(container)
    return this
}