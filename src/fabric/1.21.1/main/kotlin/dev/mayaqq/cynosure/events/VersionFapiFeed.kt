package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.world.LoottableEvents
import net.fabricmc.fabric.api.loot.v3.LootTableEvents

internal fun versionFapiFeed() {
    LootTableEvents.REPLACE.register { id, original, source, registries ->
        LoottableEvents.Replace(id.location(), original).post()
    }
    LootTableEvents.MODIFY.register { id, builder, source, registries ->
        LoottableEvents.Modify(id.location(), builder).post()
    }
    LootTableEvents.ALL_LOADED.register { resourceManager, lootRegistry ->
        LoottableEvents.AllLoaded(resourceManager).post()
    }
}