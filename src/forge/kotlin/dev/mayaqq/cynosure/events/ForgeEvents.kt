package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.block.BlockEvent
import dev.mayaqq.cynosure.events.command.CommandExecuteEvent
import dev.mayaqq.cynosure.events.command.CommandRegistrationEvent
import dev.mayaqq.cynosure.events.entity.EntityCreatedEvent
import dev.mayaqq.cynosure.events.entity.EntityTrackingEvent
import dev.mayaqq.cynosure.events.entity.LivingEntityEvent
import dev.mayaqq.cynosure.events.entity.MountEvent
import dev.mayaqq.cynosure.events.entity.player.PlayerConnectionEvent
import dev.mayaqq.cynosure.events.entity.player.interaction.InteractionEvent
import dev.mayaqq.cynosure.events.server.DataPackSyncEvent
import dev.mayaqq.cynosure.events.server.ServerChatEvent
import dev.mayaqq.cynosure.events.server.ServerEvent
import dev.mayaqq.cynosure.internal.CynosureHooksImpl
import dev.mayaqq.cynosure.items.extensions.CustomEntityItem
import dev.mayaqq.cynosure.items.extensions.CustomFurnaceFuel
import dev.mayaqq.cynosure.items.extensions.getExtension
import net.minecraft.server.TickTask
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraftforge.common.util.LogicalSidedProvider
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.OnDatapackSyncEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.TickEvent.*
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.EntityMountEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent
import net.minecraftforge.event.level.LevelEvent
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.event.level.BlockEvent as ForgeBlockEvent

private val distContext = if(FMLEnvironment.dist.isClient) "client" else "server"

@Mod.EventBusSubscriber(modid = MODID)
public object ForgeEvents {
    @SubscribeEvent
    public fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        PlayerConnectionEvent.Join(event.entity as ServerPlayer).post()
    }

    @SubscribeEvent
    public fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        PlayerConnectionEvent.Leave(event.entity as ServerPlayer).post()
    }

    @SubscribeEvent
    public fun onEntityDeath(event: LivingDeathEvent) {
        event.isCanceled = LivingEntityEvent.Death(event.entity, event.source).post()
    }

    @SubscribeEvent
    public fun onBlockEvent(event: ForgeBlockEvent) {
        when (event) {
            is ForgeBlockEvent.BreakEvent -> BlockEvent.Break(event.level, event.state, event.pos, event.player).post(context = event)
            is ForgeBlockEvent.EntityPlaceEvent -> BlockEvent.Place(event.level, event.state, event.pos, event.entity).post(context = event)
            is ForgeBlockEvent.FluidPlaceBlockEvent -> {}
        }
    }

    @SubscribeEvent
    public fun onPlayerInteract(event: PlayerInteractEvent) {
        val result: InteractionResult? = when (event) {
            is PlayerInteractEvent.RightClickBlock -> InteractionEvent.UseBlock(
                event.level, event.entity, event.hand, event.hitVec
            ).post(context = event)
            is PlayerInteractEvent.LeftClickBlock -> InteractionEvent.AttackBlock(
                event.level, event.entity, event.hand, event.pos, event.face!!
            ).post(context = event)
            is PlayerInteractEvent.EntityInteractSpecific -> InteractionEvent.UseEntity(
                event.level, event.entity, event.hand, event.target, event.localPos, InteractionEvent.UseEntity.Phase.SPECIFIC
            ).post(context = event)
            is PlayerInteractEvent.EntityInteract -> InteractionEvent.UseEntity(
                event.level, event.entity, event.hand, event.target, null, InteractionEvent.UseEntity.Phase.GENERAL
            ).post(context = event)
            is PlayerInteractEvent.RightClickItem -> InteractionEvent.UseItem(
                event.level, event.entity, event.hand
            ).post(context = event)
            else -> null
        }

        if (result != null) {
            event.cancellationResult = result
            event.isCanceled = result.consumesAction()
        }
    }

    @SubscribeEvent
    public fun onAttackEntity(event: AttackEntityEvent) {
        event.isCanceled = InteractionEvent.AttackEntity(event.entity.level(), event.entity, event.entity.usedItemHand, event.target)
            .post(context = event)
            ?.consumesAction() ?: false
    }

    @SubscribeEvent
    public fun onEntityTick(event: LivingTickEvent) {
        event.isCanceled = LivingEntityEvent.Tick(event.entity).post(context = event)
    }

    @SubscribeEvent
    public fun onSyncDatapack(event: OnDatapackSyncEvent) {
        if (event.player != null) {
            DataPackSyncEvent(event.player!!, true).post(context = event)
        } else {
            for (player in event.playerList.players) {
                DataPackSyncEvent(player, false).post(context = event)
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public fun onEntityJoin(event: EntityJoinLevelEvent) {
        val entity = event.entity
        if (entity.javaClass == ItemEntity::class.java) {
            val stack = (entity as ItemEntity).item
            val customEntityItem = stack.item.getExtension<CustomEntityItem>()
            if (customEntityItem != null) {
                val newEntity = customEntityItem.createItemEntity(entity, event.level, stack)
                entity.discard()
                event.isCanceled = true
                val executor = LogicalSidedProvider.WORKQUEUE[if (event.level.isClientSide) LogicalSide.CLIENT else LogicalSide.SERVER]
                executor.tell(TickTask(0) { event.level.addFreshEntity(newEntity) })
            }
        }
        event.isCanceled = EntityCreatedEvent(event.entity, event.level).post()
    }

    @SubscribeEvent
    public fun onEntityMount(event: EntityMountEvent) {
        val cynosureEvent = MountEvent(event.entityMounting, event.entityBeingMounted, event.isMounting)
        cynosureEvent.post()
        if (cynosureEvent.isCancelled) event.isCanceled = true
    }

    @SubscribeEvent
    public fun onLoadWorld(event: LevelEvent.Load) {
        dev.mayaqq.cynosure.events.world.LevelEvent.Load(event.level as Level)
            .post(context = distContext)
    }

    @SubscribeEvent
    public fun onUnloadWorld(event: LevelEvent.Unload) {
        dev.mayaqq.cynosure.events.world.LevelEvent.Unload(event.level as Level)
            .post(context = distContext)
    }

    @SubscribeEvent
    public fun onSaveWorld(event: LevelEvent.Save) {
        dev.mayaqq.cynosure.events.world.LevelEvent.Save(event.level as ServerLevel).post()
    }

    @SubscribeEvent
    public fun onTickWorld(event: LevelTickEvent) {
        val e = when(event.phase!!) {
            Phase.START -> dev.mayaqq.cynosure.events.world.LevelEvent.BeginTick(event.level)
            Phase.END -> dev.mayaqq.cynosure.events.world.LevelEvent.EndTick(event.level)
        }
        e.post(context = distContext)
    }


    @SubscribeEvent
    public fun onReloadListener(event: AddReloadListenerEvent) {
        CynosureHooksImpl.SERVER_RELOAD_LISTENERS.forEach { (_, listener) -> event.addListener(listener) }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    public fun furnaceFuel(event: FurnaceFuelBurnTimeEvent) {
        val item = event.itemStack.item
        val customBurnTime = item.getExtension<CustomFurnaceFuel>()
        if (customBurnTime != null)  {
            customBurnTime.getItemBurnTime(event.itemStack, event.recipeType)?.let {
                event.burnTime = it
                event.isCanceled = true
            }
            return
        }
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public fun furnaceFuel1(event: FurnaceFuelBurnTimeEvent) {
        val item = event.itemStack.item
        if (CynosureHooksImpl.BURN_TIME_BY_ITEM.containsKey(item))  {
            event.burnTime = CynosureHooksImpl.BURN_TIME_BY_ITEM.getInt(item)
        }
    }

    @SubscribeEvent
    public fun onServerStarting(event: ServerAboutToStartEvent) {
        ServerEvent.Starting(event.server).post(context = event)
    }

    @SubscribeEvent
    public fun onServerStarted(event: ServerStartedEvent) {
        ServerEvent.Started(event.server).post(context = event)
    }

    @SubscribeEvent
    public fun onServerStopping(event: ServerStoppingEvent) {
        ServerEvent.Stopping(event.server).post(context = event)
    }

    @SubscribeEvent
    public fun onServerStopped(event: ServerStoppedEvent) {
        ServerEvent.Stopped(event.server).post(context = event)
    }

    @SubscribeEvent
    public fun onServerTick(event: ServerTickEvent) {
        when (event.phase!!) {
            Phase.START -> ServerEvent.BeginTick(event.server).post(context = event)
            Phase.END -> ServerEvent.EndTick(event.server).post(context = event)
        }
    }

    @SubscribeEvent
    public fun onCommandRegistration(event: RegisterCommandsEvent) {
        CommandRegistrationEvent(event.dispatcher, event.buildContext, event.commandSelection).post()
    }

    @SubscribeEvent
    public fun onCommand(event: CommandEvent) {
        val evt = CommandExecuteEvent(event.parseResults, event.exception)
        event.isCanceled = evt.post()
        event.exception = evt.exception
        event.parseResults = evt.parseResults
    }

    @SubscribeEvent
    public fun onPlayerTick(event: PlayerTickEvent) {
        when (event.phase) {
            Phase.START -> dev.mayaqq.cynosure.events.entity.player.PlayerTickEvent.Begin(event.player).post(context = event)
            Phase.END -> dev.mayaqq.cynosure.events.entity.player.PlayerTickEvent.End(event.player).post(context = event)
        }
    }

    @SubscribeEvent
    public fun onStartTracking(event: PlayerEvent.StartTracking) {
        if (event.entity is ServerPlayer) EntityTrackingEvent.Start(event.target, event.entity as ServerPlayer).post()
    }

    @SubscribeEvent
    public fun onStopTracking(event: PlayerEvent.StopTracking) {
        if (event.entity is ServerPlayer) EntityTrackingEvent.Start(event.target, event.entity as ServerPlayer).post()
    }

    @SubscribeEvent
    public fun onChat(event: net.minecraftforge.event.ServerChatEvent) {
        val serverChatEvent = ServerChatEvent(event.player, event.message, event.rawText)
        serverChatEvent.post()
        if (serverChatEvent.isCancelled) event.isCanceled = true else event.message = serverChatEvent.message
    }
}