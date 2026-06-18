package dev.mayaqq.cynosure.events.world

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.ReturningEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.level.storage.loot.LootTable

public sealed class LoottableEvents(
    public val id: ResourceLocation,
) : ReturningEvent<LootTable?>() {
    public class Replace(
        id: ResourceLocation,
        public val original: LootTable
    ) : LoottableEvents(id)

    public class Modify(
        id: ResourceLocation,
        public val builder: LootTable.Builder
    ) : LoottableEvents(id)

    public class AllLoaded(
        public val resourceManager: ResourceManager,
    ) : Event()
}