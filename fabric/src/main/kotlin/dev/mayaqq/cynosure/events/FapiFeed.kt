package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.core.GameInstance
import dev.mayaqq.cynosure.core.GameInstanceImpl
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.command.CommandRegistrationEvent
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent
import dev.mayaqq.cynosure.events.entity.player.PlayerConnectionEvent
import dev.mayaqq.cynosure.events.entity.player.interaction.InteractionEvent
import dev.mayaqq.cynosure.events.server.DataPackSyncEvent
import dev.mayaqq.cynosure.events.server.ServerEvent
import dev.mayaqq.cynosure.events.world.LevelEvent
import dev.mayaqq.cynosure.events.world.LoottableEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.event.player.*
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.ItemStack

internal fun fapiFeed() {

    // Command registration
    CommandRegistrationCallback.EVENT.register { commandDispatcher, commandBuildContext, commandSelection ->
        CommandRegistrationEvent(commandDispatcher, commandBuildContext, commandSelection).post()
    }

    GameInstance // load GameInstance in case it hasn't been
    //Lifecycle events
    ServerLifecycleEvents.SERVER_STARTING.register {
        GameInstanceImpl.onLoadServer(it)
        ServerEvent.Starting(it).post()
    }
    ServerLifecycleEvents.SERVER_STARTED.register { ServerEvent.Started(it).post() }
    ServerLifecycleEvents.SERVER_STOPPING.register { ServerEvent.Stopping(it).post() }
    ServerLifecycleEvents.SERVER_STOPPED.register {
        ServerEvent.Stopped(it).post()
        GameInstanceImpl.onUnloadServer()
    }
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, isJoin -> DataPackSyncEvent(player, isJoin).post() }

    // Misc server events
    ServerPlayConnectionEvents.JOIN.register { handler, _, _ -> PlayerConnectionEvent.Join(handler.player).post() }
    ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> PlayerConnectionEvent.Leave(handler.player).post() }
    ServerLivingEntityEvents.ALLOW_DEATH.register { entity, source, _ -> !LivingEntityEvent.Death(entity, source).post() }
    ServerWorldEvents.LOAD.register { _, level -> LevelEvent.Load(level).post(context = "server") }
    ServerWorldEvents.UNLOAD.register { _, level -> LevelEvent.Unload(level).post(context = "server") }

    // Tick events
    ServerTickEvents.START_WORLD_TICK.register { LevelEvent.BeginTick(it).post(context = "server") }
    ServerTickEvents.END_WORLD_TICK.register { LevelEvent.EndTick(it).post(context = "server") }
    ServerTickEvents.START_SERVER_TICK.register { ServerEvent.BeginTick(it).post() }
    ServerTickEvents.END_SERVER_TICK.register { ServerEvent.EndTick(it).post() }

    // Interaction events
    UseBlockCallback.EVENT.register { player, level, hand, hit ->
        if (player.isSpectator) return@register InteractionResult.PASS
        InteractionEvent.UseBlock(level, player, hand, hit).post() ?: InteractionResult.PASS
    }
    UseEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
        if (player.isSpectator) return@register InteractionResult.PASS
        InteractionEvent.UseEntity(
            world, player, hand, entity,
            hitResult?.location?.subtract(entity.position()),
            if (hitResult != null) InteractionEvent.UseEntity.Phase.SPECIFIC else InteractionEvent.UseEntity.Phase.GENERAL
        ).post() ?: InteractionResult.PASS
    }
    UseItemCallback.EVENT.register { player, level, interactionHand ->
        if (player.isSpectator) return@register InteractionResultHolder.pass(ItemStack.EMPTY)
        InteractionResultHolder(
            (InteractionEvent.UseItem(level, player, interactionHand).post() ?: InteractionResult.PASS),
            player.getItemInHand(interactionHand)
        )
    }
    AttackBlockCallback.EVENT.register { player, level, interactionHand, blockPos, direction ->
        if (player.isSpectator) return@register InteractionResult.PASS
        InteractionEvent.AttackBlock(level, player, interactionHand, blockPos, direction).post() ?: InteractionResult.PASS
    }
    AttackEntityCallback.EVENT.register { player, level, interactionHand, entity, _ ->
        if (player.isSpectator) return@register InteractionResult.PASS
        InteractionEvent.AttackEntity(level, player, interactionHand, entity).post() ?: InteractionResult.PASS
    }
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
