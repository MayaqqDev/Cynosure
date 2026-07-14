package dev.mayaqq.cynosure.events.server

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.world.entity.player.Player

@RootEventClass
public class DataPackSyncEvent(public val player: Player, public val isJoin: Boolean) : Event()