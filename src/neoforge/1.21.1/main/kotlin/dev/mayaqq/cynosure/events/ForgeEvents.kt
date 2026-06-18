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
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.LogicalSide
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.util.LogicalSidedProvider
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.CommandEvent
import net.neoforged.neoforge.event.OnDatapackSyncEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.EntityMountEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.MobEffectEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent
import net.neoforged.neoforge.event.level.LevelEvent
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent

private val distContext = if(FMLEnvironment.dist.isClient) "client" else "server"

private typealias NeoForgeBlockEvent = net.neoforged.neoforge.event.level.BlockEvent
private typealias NeoForgeBlockBreakEvent = net.neoforged.neoforge.event.level.BlockEvent.BreakEvent
private typealias NeoForgeBlockPlaceEvent = net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent
private typealias NeoforgeFluidPlaceEvent = net.neoforged.neoforge.event.level.BlockEvent.FluidPlaceBlockEvent

@EventBusSubscriber(modid = MODID)
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
    public fun onBlockEvent(event: NeoForgeBlockEvent) {
        when (event) {
            is NeoForgeBlockBreakEvent -> BlockEvent.Break(event.level, event.state, event.pos, event.player).post()
            is NeoForgeBlockPlaceEvent -> BlockEvent.Place(event.level, event.state, event.pos, event.entity).post()
            is NeoforgeFluidPlaceEvent -> {}
        }
    }

    @SubscribeEvent
    public fun onPlayerInteract(event: PlayerInteractEvent) {
        var result: InteractionResult? = null

        when (event) {
            is PlayerInteractEvent.RightClickBlock -> {
                result = InteractionEvent.UseBlock(
                    event.level, event.entity, event.hand, event.hitVec
                ).post()
                if (result != null) {
                    event.cancellationResult = result
                }
            }
            is PlayerInteractEvent.LeftClickBlock -> {
                result = InteractionEvent.AttackBlock(
                    event.level, event.entity, event.hand, event.pos, event.face!!
                ).post()
            }
            is PlayerInteractEvent.EntityInteractSpecific -> {
                result = InteractionEvent.UseEntity(
                    event.level, event.entity, event.hand, event.target, event.localPos, InteractionEvent.UseEntity.Phase.SPECIFIC
                ).post()
                if (result != null) {
                    event.cancellationResult = result
                }
            }
            is PlayerInteractEvent.EntityInteract -> {
                result = InteractionEvent.UseEntity(
                    event.level, event.entity, event.hand, event.target, null, InteractionEvent.UseEntity.Phase.GENERAL
                ).post()
                if (result != null) {
                    event.cancellationResult = result
                }
            }
            is PlayerInteractEvent.RightClickItem -> {
                result = InteractionEvent.UseItem(
                    event.level, event.entity, event.hand
                ).post()
                if (result != null) {
                    event.cancellationResult = result
                }
            }
            else -> result = null
        }

        if (event is ICancellableEvent && result != null) {
            event.isCanceled = result.consumesAction()
        }
    }

    @SubscribeEvent
    public fun onAttackEntity(event: AttackEntityEvent) {
        event.isCanceled = InteractionEvent.AttackEntity(event.entity.level(), event.entity, event.entity.usedItemHand, event.target)
            .post()
            ?.consumesAction() ?: false
    }

    @SubscribeEvent
    public fun onEntityTick(event: EntityTickEvent.Pre) {
        (event.entity as? LivingEntity)?.let { event.isCanceled = LivingEntityEvent.Tick(it).post() }
    }

    @SubscribeEvent
    public fun onSyncDatapack(event: OnDatapackSyncEvent) {
        if (event.player != null) {
            DataPackSyncEvent(event.player!!, true).post()
        } else {
            for (player in event.playerList.players) {
                DataPackSyncEvent(player, false).post()
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
            .post()
    }

    @SubscribeEvent
    public fun onUnloadWorld(event: LevelEvent.Unload) {
        dev.mayaqq.cynosure.events.world.LevelEvent.Unload(event.level as Level)
            .post()
    }

    @SubscribeEvent
    public fun onSaveWorld(event: LevelEvent.Save) {
        dev.mayaqq.cynosure.events.world.LevelEvent.Save(event.level as ServerLevel).post()
    }

    @SubscribeEvent
    public fun onTickWorldPre(event: LevelTickEvent) {
        dev.mayaqq.cynosure.events.world.LevelEvent.BeginTick(event.level).post()
    }

    @SubscribeEvent
    public fun onTickWorldPost(event: LevelTickEvent) {
        dev.mayaqq.cynosure.events.world.LevelEvent.EndTick(event.level).post()
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
        ServerEvent.Starting(event.server).post()
    }

    @SubscribeEvent
    public fun onServerStarted(event: ServerStartedEvent) {
        ServerEvent.Started(event.server).post()
    }

    @SubscribeEvent
    public fun onServerStopping(event: ServerStoppingEvent) {
        ServerEvent.Stopping(event.server).post()
    }

    @SubscribeEvent
    public fun onServerStopped(event: ServerStoppedEvent) {
        ServerEvent.Stopped(event.server).post()
    }

    @SubscribeEvent
    public fun onServerTickPre(event: ServerTickEvent.Pre) {
        ServerEvent.BeginTick(event.server).post()
    }

    @SubscribeEvent
    public fun onServerTickPost(event: ServerTickEvent.Post) {
        ServerEvent.EndTick(event.server).post()
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
    public fun onPlayerTickPre(event: PlayerTickEvent.Pre) {
        dev.mayaqq.cynosure.events.entity.player.PlayerTickEvent.Begin(event.entity).post()
    }

    @SubscribeEvent
    public fun onPlayerTickPost(event: PlayerTickEvent.Post) {
        dev.mayaqq.cynosure.events.entity.player.PlayerTickEvent.End(event.entity).post()
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
    public fun onChat(event: net.neoforged.neoforge.event.ServerChatEvent) {
        val serverChatEvent = ServerChatEvent(event.player, event.message, event.rawText)
        serverChatEvent.post()
        if (serverChatEvent.isCancelled) event.isCanceled = true else event.message = serverChatEvent.message
    }

    @SubscribeEvent
    public fun onEntityEffectAdded(event: MobEffectEvent.Added) {
        event.effectInstance?.let { old ->
            LivingEntityEvent.EffectApply(event.entity, old, event.oldEffectInstance).post()
        }
    }

    @SubscribeEvent
    public fun onEntityEffectRemoved(event: MobEffectEvent.Remove) {
        LivingEntityEvent.EffectRemove(event.entity, event.effectInstance).post()
    }

    @SubscribeEvent
    public fun onEntityEffectExpired(event: MobEffectEvent.Expired) {
        LivingEntityEvent.EffectExpire(event.entity, event.effectInstance).post()
    }
}