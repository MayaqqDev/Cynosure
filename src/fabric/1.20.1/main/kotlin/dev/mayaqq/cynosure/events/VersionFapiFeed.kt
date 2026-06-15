package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.world.LoottableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableEvents

internal fun versionFapiFeed() {
    LootTableEvents.REPLACE.register { resourceManager, lootManager, id, original, source ->
        LoottableEvents.Replace(resourceManager, lootManager, id, original).post()
    }
    LootTableEvents.MODIFY.register { resourceManager, lootManager, id, builder, source ->
        LoottableEvents.Modify(resourceManager, lootManager, id, builder).post()
    }
    LootTableEvents.ALL_LOADED.register { resourceManager, lootManager ->
        LoottableEvents.AllLoaded(resourceManager, lootManager).post()
    }
}