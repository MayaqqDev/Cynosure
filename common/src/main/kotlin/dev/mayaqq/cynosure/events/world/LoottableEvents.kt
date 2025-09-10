package dev.mayaqq.cynosure.events.world

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.ReturningEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.level.storage.loot.LootDataManager
import net.minecraft.world.level.storage.loot.LootTable

public sealed class LoottableEvents(
    public val resourceManager: ResourceManager,
    public val lootManager: LootDataManager,
    public val id: ResourceLocation,
) : ReturningEvent<LootTable?>() {
    public class Replace(
        resourceManager: ResourceManager,
        lootManager: LootDataManager,
        id: ResourceLocation,
        public val original: LootTable
    ) : LoottableEvents(resourceManager, lootManager, id)

    public class Modify(
        resourceManager: ResourceManager,
        lootManager: LootDataManager,
        id: ResourceLocation,
        public val builder: LootTable.Builder
    ) : LoottableEvents(resourceManager, lootManager, id)

    public class AllLoaded(
        public val resourceManager: ResourceManager,
        public val lootManager: LootDataManager
    ) : Event
}